package com.sunshine687.shikelang.service.impl;

import com.sunshine687.shikelang.mappers.MainMapper;
import com.sunshine687.shikelang.pojo.*;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service("mainService")
public class MainServiceImpl implements IMainService {
    @Autowired
    private MainMapper mainMapper;
    @Value("${cus.timeOutTime}")
    private String timeOutTime;
    private VideoUtils videoUtils = new VideoUtils();
    private String message = "";
    private final List<String> nextTaskList = new ArrayList<>();//加入失败的视频，存入下次任务
    private final List<String> otherLineTaskList = new ArrayList<>();//获取真实视频地址失败的视频，存入换线任务

    private Long flagStart = System.currentTimeMillis();
    private static final Logger LOGGER = LoggerFactory.getLogger(MainServiceImpl.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd") ;


    /**
     * 获取视频详细信息
     */
    public void getVideoDetail(TypeEnum videoType) {
        List<Video> insertedVideos = mainMapper.getAllVideo(videoType.getGroupId(),videoType.getName());
        //获取当前视频类型的总记录数
        Integer total = videoUtils.getListTotal(videoType);
        String url_base = videoType.getUrl();
        String url_before = url_base.substring(0,url_base.indexOf(".html"));

        //遍历每一页的视频
        for (int i = 0;i<total;i++) {
            LOGGER.error("shikelang-未保存任务列表：" + nextTaskList.toString());
            LOGGER.error("shikelang-换线任务列表：" + otherLineTaskList.toString());
            System.out.println("开始获取第" + (i + 1) + "页的数据");
            String url = "";
            url = url_before + "/page/" + (i + 1) + ".html";
            Document document = videoUtils.httpGet_setTime(url, 30);
            if(document != null){
                Elements elements = document.select("ul.fed-list-info li.fed-list-item");
                //遍历当前页下的视频
                for (Element element : elements) {
                    String href = element.selectFirst("a.fed-list-pics").attr("href");
                    Video v = videoUtils.isExistVideo(insertedVideos,href);
                    if(v == null){
                        getCurrentVideo(videoType,href);
                    }
                }
            }else{
                System.out.println("获取页码视频发出异常");
            }
        }

        if(nextTaskList.size() != 0){//执行下次任务
            doNextTask(videoType,1);
        }
    }

    /**
     * 更新视频详细信息
     */
    public boolean updateVideoDetail(TypeEnum videoType,Integer startDate,Integer endDate){
        List<Video> insertedVideos = mainMapper.getAllVideo(videoType.getGroupId(),videoType.getName());
        //获取当前视频类型的总记录数
        Integer total = videoUtils.getListTotal(videoType);
        String url_base = videoType.getUrl();
        String url_before = url_base.substring(0,url_base.indexOf(".html"));

        List<String> list = new ArrayList<>();
        boolean flag = false;
        int totalNumber = 0;
        //遍历每一页的视频
        for (int i = 0;i<total;i++) {
            if(flag){
                break;
            }
            LOGGER.error("shikelang-开始获取第" + (i + 1) + "页的数据");
            String url = "";
            url = url_before + "/page/" + (i + 1) + ".html";
            Document document = videoUtils.httpGet_setTime(url, 30);
            if(document != null){
                Elements elements = document.select("ul.fed-list-info li.fed-list-item");
                //遍历当前页下的视频
                for (Element element : elements) {
                    String href = element.selectFirst("a.fed-list-pics").attr("href");
                    Document doc = videoUtils.httpGet_setTime(TypeEnum.BASEURL.getUrl() + href, 30);
                    Video video = catchCurrentVideo(videoType,href,doc);
                    if(video != null && !"".equals(video.getUpdateTime())){
                        int updateDate = Integer.parseInt(video.getUpdateTime().replaceAll("\\.",""));
                        if(updateDate >= startDate && updateDate <= endDate){
                            totalNumber++;
                            Video v = videoUtils.isExistVideo(insertedVideos,href);
                            //首先判断数据库中是否存在该视频，如果不存在则直接添加
                            if(v == null){
                                LOGGER.error("shikelang-" + video.getName() + "--数据库中不存在，直接添加");
                                //需要插入视频并获取剧集
                                List<VideoItem> videoItems = getItemsOfVideo(video,doc,href,null);
                                if(videoItems != null){
                                    Integer video_is_success = mainMapper.insertVideo(video);//插入视频
                                    if (video_is_success == 1) {//新增视频成功之后将集数列表插入其中
                                        for (VideoItem videoItem : videoItems) {
                                            videoItem.setVideoId(video.getId());
                                        }
                                        video.setList(videoItems);
                                        mainMapper.insertVideoItems(videoItems);//插入集数列表
                                        LOGGER.error("shikelang-" + video.getName() + "---插入成功");
                                    }
                                }
                            }else{//库中已存在，需要更新
                                LOGGER.error("shikelang-" + video.getName() + "--数据库中已存在，但需要更新");
                                //更新库中不存在的剧集
                                List<VideoItem> dbItems = mainMapper.getAllVideoItem(v);
                                //需要插入视频并获取剧集
                                List<VideoItem> videoItems = getItemsOfVideo(video,doc,href,dbItems);
                                if(videoItems != null && videoItems.size() > 0){
                                    video.setId(v.getId());
                                    mainMapper.updateVideo(video);//更新视频状态、更新时间
                                    for (VideoItem videoItem : videoItems) {
                                        videoItem.setVideoId(v.getId());
                                    }
                                    mainMapper.insertVideoItems(videoItems);//插入集数列表
                                    LOGGER.error("shikelang-" + video.getName() + "---更新成功");
                                }
                            }
                        }else{
                            flag = true;
                            break;
                        }
                    }else{
                        LOGGER.error("shikelang-捕获视频失败");
                    }
                }
            }else{
                LOGGER.error("shikelang-获取页码视频发出异常");
            }
        }
        LOGGER.error("shikelang-" + videoType.getName() + "-本次总共更新记录数：" + totalNumber);
        return true;
    }

    /**
     * 更新当前视频信息
     * @param videoType 视频组别
     * @param href 视频页面地址
     * return 返回视频对象（未获取视频剧集）
     */
    public Video catchCurrentVideo(TypeEnum videoType,String href,Document doc){
        Video video = null;
        if(doc != null){
            Element elem = doc.selectFirst("dl.fed-deta-info");
            String flagName = doc.selectFirst("div.fed-tabs-info ul li").text().trim();
            Element e_element_1 = elem.selectFirst("dt.fed-deta-images");
            Element e_element_2 = elem.selectFirst("dd.fed-deta-content");

            if("播放列表".equals(flagName)){//存在播放列表时才执行插入操作
                //获取视频基本信息
                video = new Video();
                String video_name = e_element_2.selectFirst("h1 a").text().trim();
                String video_imgUrl = e_element_1.selectFirst("a.fed-list-pics").attr("data-original");
                String video_updateStatus = e_element_1.selectFirst("a.fed-list-pics span.fed-list-remarks").text().trim();
                StringBuilder video_mainMapper = new StringBuilder();
                StringBuilder video_director = new StringBuilder();

                Elements baseInfos = e_element_2.select("ul li");
                //主演
                Elements mainMappers = baseInfos.get(0).select("a");
                for (Element e1 : mainMappers) {
                    video_mainMapper.append(e1.text().trim()).append(",");
                }
                //导演
                Elements directors = baseInfos.get(1).select("a");
                for (Element e1 : directors) {
                    video_director.append(e1.text().trim()).append(",");
                }
                //分类
                String video_type = baseInfos.get(2).selectFirst("a") == null ? "未知" : baseInfos.get(2).selectFirst("a").text().trim();
                //地区
                String video_area = baseInfos.get(3).selectFirst("a") == null ? "未知" : baseInfos.get(3).selectFirst("a").text().trim();
                //年份
                String video_year = baseInfos.get(4).selectFirst("a") == null ? "未知" : baseInfos.get(4).selectFirst("a").text().trim();
                //更新时间
                String video_updateTime = baseInfos.get(5).text().trim();
                //简介
                String video_instruction = baseInfos.get(6).selectFirst("div").text().trim();

                video.setName(video_name);
                video.setImgUrl(video_imgUrl);
                video.setUpdateStatus(video_updateStatus);
                video.setYear(video_year);
                video.setArea(video_area);
                video.setDirector("".equals(video_director.toString()) ? "" : (video_director.toString().substring(0,video_director.toString().length() - 1)));
                video.setMainPerformer("".equals(video_mainMapper.toString()) ? "" : (video_mainMapper.toString().substring(0,video_mainMapper.toString().length() - 1)));
                video.setVideoGroupId(videoType.getGroupId());
                video.setType(video_type);
                video.setUpdateTime(video_updateTime.split("：").length == 0 ? "" : video_updateTime.split("：")[1]);
                video.setUpdateUrl(href);
                video.setInstruction(video_instruction.split("简介：").length == 0 ? "" : video_instruction.split("简介：")[1]);
                video.setCreateTime(new Timestamp(new Date().getTime()));
            }
        }else{
            System.out.println("获取视频剧集页面发出异常");
        }
        return video;
    }


    /**
     * 执行下次任务
     * @param videoType
     */
    public void doNextTask(TypeEnum videoType,Integer index){
        if(index <= 3){
            LOGGER.error("shikelang-执行任务列表：" + nextTaskList.toString());
            try {
                List<String> tempList = videoUtils.deepCopy(nextTaskList);
                nextTaskList.clear();
                for (String urlStr : tempList) {
                    getCurrentVideo(videoType,urlStr);
                }
                if(nextTaskList.size() != 0){//执行下次任务
                    index++;
                    doNextTask(videoType, index);
                }
            } catch (Exception e) {
                System.out.println("执行下次任务catch");
            }
        }else{
            LOGGER.error("shikelang-最终未执行的列表：" + nextTaskList.toString());
        }
    }

    /**
     * 获取当前视频信息
     * @param videoType 视频组别
     * @param href 视频页面地址
     * return 是否存入成功
     */
    public void getCurrentVideo(TypeEnum videoType,String href){
        Document doc = videoUtils.httpGet_setTime(TypeEnum.BASEURL.getUrl() + href, 30);
        if(doc != null){
            Element elem = doc.selectFirst("dl.fed-deta-info");
            String flagName = doc.selectFirst("div.fed-tabs-info ul li").text().trim();
            Element e_element_1 = elem.selectFirst("dt.fed-deta-images");
            Element e_element_2 = elem.selectFirst("dd.fed-deta-content");


            if("播放列表".equals(flagName)){//存在播放列表时才执行插入操作
                //获取视频基本信息
                Video video = new Video();
                String video_name = e_element_2.selectFirst("h1 a").text().trim();
                String video_imgUrl = e_element_1.selectFirst("a.fed-list-pics").attr("data-original");
                String video_updateStatus = e_element_1.selectFirst("a.fed-list-pics span.fed-list-remarks").text().trim();
                StringBuilder video_mainMapper = new StringBuilder();
                StringBuilder video_director = new StringBuilder();

                Elements baseInfos = e_element_2.select("ul li");
                //主演
                Elements mainMappers = baseInfos.get(0).select("a");
                for (Element e1 : mainMappers) {
                    video_mainMapper.append(e1.text().trim()).append(",");
                }
                //导演
                Elements directors = baseInfos.get(1).select("a");
                for (Element e1 : directors) {
                    video_director.append(e1.text().trim()).append(",");
                }
                //分类
                String video_type = baseInfos.get(2).selectFirst("a") == null ? "未知" : baseInfos.get(2).selectFirst("a").text().trim();
                //地区
                String video_area = baseInfos.get(3).selectFirst("a") == null ? "未知" : baseInfos.get(3).selectFirst("a").text().trim();
                //年份
                String video_year = baseInfos.get(4).selectFirst("a") == null ? "未知" : baseInfos.get(4).selectFirst("a").text().trim();
                //更新时间
                String video_updateTime = baseInfos.get(5).text().trim();
                //简介
                String video_instruction = baseInfos.get(6).selectFirst("div").text().trim();

                video.setName(video_name);
                video.setImgUrl(video_imgUrl);
                video.setUpdateStatus(video_updateStatus);
                video.setYear(video_year);
                video.setArea(video_area);
                video.setDirector("".equals(video_director.toString()) ? "" : (video_director.toString().substring(0,video_director.toString().length() - 1)));
                video.setMainPerformer("".equals(video_mainMapper.toString()) ? "" : (video_mainMapper.toString().substring(0,video_mainMapper.toString().length() - 1)));
                video.setVideoGroupId(videoType.getGroupId());
                video.setType(video_type);
                video.setUpdateTime(video_updateTime.split("：").length == 0 ? "" : video_updateTime.split("：")[1]);
                video.setUpdateUrl(href);
                video.setInstruction(video_instruction.split("简介：").length == 0 ? "" : video_instruction.split("简介：")[1]);
                video.setCreateTime(new Timestamp(new Date().getTime()));

                //获取剧集
                List<VideoItem> videoItems = getItemsOfVideo(video,doc,href,null);
                //判断剧集列表是否为null，如果为null则加入下次任务
                if(videoItems != null){
                    Integer video_is_success = mainMapper.insertVideo(video);//插入视频
                    if (video_is_success == 1) {//新增视频成功之后将集数列表插入其中
                        for (VideoItem videoItem : videoItems) {
                            videoItem.setVideoId(video.getId());
                        }
                        video.setList(videoItems);
                        mainMapper.insertVideoItems(videoItems);//插入集数列表
                    }
                }
            }
        }else{
            System.out.println("获取视频剧集页面发出异常");
        }
    }

    /**
     * 获取视频对象集合（获取临时视频地址）
     */
    public List<VideoItem> getItemsOfVideo(Video video, Document doc,String video_url,List<VideoItem> videoItems) {
        flagStart = System.currentTimeMillis();
        long flagStart_nei = System.currentTimeMillis();
        Elements items = doc.select("div.fed-play-data div.fed-play-item ul.fed-part-rows").get(1).select("li a");
        List<Integer> ll = new ArrayList<>();//待处理剧集列表
        List<VideoItem> lists = new ArrayList<>();//剧集列表

        //初始化时全部是未处理状态
        for (int i = 0;i<items.size();i++) {
            Element item = items.get(i);

            //判断是否有需要更新的剧集
            boolean add_ = false;//默认不添加到任务
            if(videoItems != null){//更新操作，判断库中是否存在
                if(videoUtils.isExistVideoItem(videoItems,item.text().trim()) == null){//不存在，需要加入任务
                    add_ = true;
                }
            }else{//添加操作，直接加入
                add_ = true;
            }
            if(add_){
                VideoItem v = new VideoItem();
                v.setName(item.text().trim());
                v.setItemHref(item.attr("href"));
                v.setUpdateIndex(i);
                lists.add(v);
                ll.add(i);
            }
        }

        //和库中视频剧集一致，返回null
        if(ll.size() == 0){
            LOGGER.error("shikelang-" + video.getName() + "--数据相同，取消更新");
            return null;
        }
        List<VideoItem> itemList = setCount(lists,ll,1,video,flagStart_nei,video_url);
        if(itemList == null){
            LOGGER.error("shikelang-" + video.getName() + "--数据更新失败");
        }
        return itemList;
    }


    /**
     * 重复获取次数 3
     * @param lists 视频剧集
     * @param ll 未获取的剧集
     * @param index 执行次数
     * @return 返回视频剧集
     */
    public List<VideoItem> setCount(List<VideoItem> lists, List<Integer> ll,Integer index,Video video,long flagStart_nei,String video_url){
        if(index <= 3){
            System.out.println(video.getName() + "-获取次数" + index);
            Map<String, List> map = getItemsAgain(lists, ll);
            if(map != null){
                lists = map.get("lists");
                ll = map.get("ll");

                //判断url是否空缺，如果空缺则继续执行，知道任务超时
                boolean flag = true;
                for (VideoItem v : lists) {
                    if(v.getUrl() == null || "".equals(v.getUrl())){
                        flag = false;
                        break;
                    }
                }

                long end = System.currentTimeMillis();
                if (ll.size() == 0 && flag) {//获取成功
                    LOGGER.error("shikelang-获取'" + video.getName() + "'耗时：" + ((end - flagStart_nei) / 1000) + "秒");
                }else {
                    long totalTime = (end - flagStart_nei) / 1000;
                    long tmout = (lists.size()/40) == 0 ? Long.parseLong(timeOutTime) : (lists.size()/40) * Long.parseLong(timeOutTime);
                    if (totalTime > tmout) {//获取超时，加入下次任务
                        System.out.println("保存剧集超时，加入下次任务");
                        lists = null;
                        nextTaskList.add(video_url);
                    }else{
                        index++;
                        lists = setCount(lists, ll,index,video,flagStart_nei,video_url);
                    }
                }
            }else{//map为null时，添加到换线任务
                lists = null;
                otherLineTaskList.add(video_url);
                System.out.println("已加入到换线任务");
            }

        }else{//获取次数过多，加入下次任务
            System.out.println("获取次数过多，已加入下次任务");
            lists = null;
            nextTaskList.add(video_url);
        }
        return lists;
    }

    /**
     * isUpdate 是否是更新
     * 重复尝试获取对象集合
     */
    public Map<String, List> getItemsAgain(List<VideoItem> lists, List<Integer> waitList) {
        Map<String, List> map = new HashMap<>();
        int threadNumber = 16 * 2;//默认线程数16*2
        // 创建一个线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threadNumber, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        List<Integer> ll = Collections.synchronizedList(new ArrayList<>());

        //创建一个数组长度为线程数量的数组用于添加线程需要执行的数组
        List<List<Integer>> tasks = new ArrayList<>();
        if (waitList.size() != 0) {
            for(int i = 0;i<threadNumber;i++){
                List<Integer> task = new ArrayList<>();
                for (int in : waitList) {
                    if(in % threadNumber == i){
                        task.add(in);
                    }
                }
                tasks.add(i,task);
            }
        }

        final boolean[] flag = {true};//判断是否停止线程池，并加入换线任务的标识
        for (List<Integer> taskList : tasks) {
            if (taskList.size() != 0) {
                pool.submit(new Runnable() {
                    public void run() {
                        for (Integer integer : taskList) {
                            for (int j = 0; j < lists.size(); j++) {
                                if (lists.get(j).getUpdateIndex().equals(integer)) {
                                    //访问url
                                    Document doc1 = null;
                                    String html = videoUtils.getHtmlPageResponse(TypeEnum.BASEURL.getUrl() + lists.get(j).getItemHref());
                                    if (!"".equals(html)) {
                                        doc1 = Jsoup.parse(html);
                                    }
                                    if (doc1 != null) {
                                        String tempUrl = doc1.selectFirst("iframe#fed-play-iframe").attr("src");
                                        if (tempUrl != null && !"".equals(tempUrl)) {
                                            if (tempUrl.contains("url=") && tempUrl.contains(".m3u8")) {
                                                lists.get(j).setUrl(tempUrl.substring(tempUrl.indexOf("url=") + 4, tempUrl.indexOf(".m3u8") + 5));
                                            } else {//处理临时地址
                                                String realUrl = getRealUrl(tempUrl);
                                                if (!"".equals(realUrl)) {
                                                    if("error".equals(realUrl)){//发现一个无法获取立即停止线程池，加入换线任务中
                                                        flag[0] = false;
                                                    }else{
                                                        lists.get(j).setUrl(realUrl);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        ll.add(j);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        pool.shutdown();//不再接收新的线程任务

        while (true) {
            if(flag[0]){
                if (pool.isTerminated()) {
                    long end = System.currentTimeMillis();
                    if (ll.size() != 0) {
                        System.out.println("未保存剧集" + ll.toString() + "即将重试--已耗时：" + ((end - flagStart) / 1000) + "秒");
                    }
                    map.put("lists", lists);
                    map.put("ll", ll);
                    break;
                }
            }else{//立即停止线程池
                pool.shutdownNow();
                map = null;
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    /**
     * 根据临时url返回真实url
     * @param tempUrl 临时url
     * @return 真实url
     */
    public String getRealUrl(String tempUrl){
        String realUrl = "";
        Document dd = videoUtils.httpGet_setTime(tempUrl,30);
        if(dd != null){
            Elements els = dd.select("script");
            String scriptString = "";
            for (Element ele : els) {
                String tempStr = ele.data().toString().replaceAll(" ", "");
                if (tempStr.length() > 88) {
                    scriptString = tempStr;
                    break;
                }
            }
            if (!"".equals(scriptString)) {
                scriptString = scriptString.replaceAll(" ","").replaceAll("\n","").replaceAll("\0","").replaceAll("\t","");
                if(scriptString.contains("varmain=")){
                    realUrl = scriptString.split("varmain=")[1];
                    realUrl = realUrl.substring(1, realUrl.indexOf(".m3u8") + 5);
                }
                if (!realUrl.contains("http")) {
                    realUrl = tempUrl.substring(0, tempUrl.indexOf("/", 10)) + realUrl;
                }
            }
        }else{
            realUrl = "error";//无法访问真实地址，加入换线获取目录
        }
        return realUrl;
    }



    /**
     * 测试
     */
    public void getTest() {
        Video video = new Video();
        video.setId(2896);
        System.out.println(mainMapper.getAllVideoItem(video).toString());
    }
}
