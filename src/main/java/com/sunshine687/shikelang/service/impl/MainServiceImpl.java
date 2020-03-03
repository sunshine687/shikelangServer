package com.sunshine687.shikelang.service.impl;

import com.sunshine687.shikelang.mappers.MainMapper;
import com.sunshine687.shikelang.pojo.*;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("mainService")
public class MainServiceImpl implements IMainService{
    @Autowired
    private MainMapper mainMapper;
    private VideoUtils videoUtils = new VideoUtils();
    private String message = "";
    private static final Logger LOGGER = LoggerFactory.getLogger(MainServiceImpl.class);

    public void getVideoDetail(TypeEnum videoType){
        Integer total = videoUtils.getListTotal(videoType);
        String url_base = videoType.getUrl();
        String url_before = url_base.split("\\?")[0] + "?";
        String url_after = url_base.split("\\?")[1].substring(url_base.split("\\?")[1].indexOf("&"));

        //遍历每一页的数据
        for (int i = 0; i < total; i++) {
            String url = url_before + "page=" + (i+1) + url_after;
            Document document = videoUtils.setConnectionParam(url);
            Elements elements = document.select("ul.myui-vodlist li a.myui-vodlist__thumb");
            //获取迭代器，遍历每个视频的信息
            System.out.println("当前页"+elements.size()+"个视频");
            for (Element element : elements) {
//                Element element = elements.get(12);
                Video video = new Video();
                String href = element.attr("href");
                Integer video_flag = Integer.parseInt(href.split("\\?")[1].split("\\.")[0]);
                String video_name = element.attr("title");
                System.out.println("开始插入视频" + video_name);
                String video_imgUrl = element.attr("data-original");
                String video_updateStatus = element.select("span.pic-text").text();
                video.setFlag(video_flag);
                video.setName(video_name);
                video.setImgUrl(video_imgUrl);
                video.setUpdateStatus(video_updateStatus);
                Document doc = videoUtils.setConnectionParam(TypeEnum.BASEURL.getUrl() + href);
                Elements elems = doc.select(".myui-content__detail p");
                //获取第一行数据（分类、地区、年份）
                String video_group_name = elems.get(0).select("a").get(0).text();
                String video_area = elems.get(0).select("a").get(1).text();
                String video_year = elems.get(0).select("a").get(2).text();
                Integer video_group = VideoGroupEnum.valueOf(video_group_name).getNo();
                video.setVideoGroupId(video_group);
                video.setYear(Integer.parseInt(video_year));
                video.setArea(video_area);
                //获取第二行数据（主演，多个用,隔开）
                Elements actors = elems.get(1).select("a");
                StringBuilder video_mainPerformer = new StringBuilder();
                for (Element actor : actors) {
                    video_mainPerformer.append(actor.text()).append(",");
                }
                if (video_mainPerformer.toString().length() > 0) {
                    video_mainPerformer.deleteCharAt(video_mainPerformer.toString().length() - 1);
                }
                video.setMainPerformer(video_mainPerformer.toString());
                //获取第三行数据（导演，多个用,隔开）
                Elements directors = elems.get(2).select("a");
                StringBuilder video_director = new StringBuilder();
                for (Element director : directors) {
                    video_director.append(director.text()).append(",");
                }
                if (video_director.toString().length() > 0) {
                    video_director.deleteCharAt(video_director.toString().length() - 1);
                }
                video.setDirector(video_director.toString());
                //获取第四行数据（更新时间）
                String video_updateTime = elems.get(3).text().split("：")[1];
                video.setUpdateTime(video_updateTime);
                //获取第五行数据（简介）
                String video_instruction = elems.get(4).select("span.data").get(0).text();
                video.setInstruction(video_instruction);
                video.setCreateTime(new Timestamp(new Date().getTime()));
                Integer video_is_success = mainMapper.insertVideo(video);//插入视频

                if(video_is_success == 1){//新增视频成功之后将集数列表插入其中
                    boolean flag = true;
                    for (int j = 0; j < 6; j++) {
                        List<VideoItem> list = getVideoItemList(video,doc,href,j);
                        if(list != null){
                            video.setList(list);
                            boolean isSuccess = mainMapper.insertVideoItems(list);//插入集数列表
                            //插入剧集成功后更新视频的pn类型
                            if(isSuccess){
                                video.setPn(list.get(0).getPn());
                                mainMapper.updateVideo(video);
                            }
                            flag = false;
                            break;
                        }else{
                            System.out.println("切换剧集list");
                        }
                    }
                    if(flag){
                        message = "未保存'" + video.getName() + "'的剧集，原因是切换多次剧集都产生错误";
                        LOGGER.error(message);
                        System.out.println(message);
                    }else{
                        System.out.println("成功插入视频" + video.getName());
                    }
                }
            }
            break;
        }
    }

    public List<VideoItem> getVideoItemList(Video video,Document doc,String href,Integer index){
        //获取剧集
        List<VideoItem> list = new ArrayList<>();
        Document doc2 = null;
        if(index >= doc.select("ul.stui-content__playlist").size()){
            return null;
        }
        Elements items = doc.select("ul.stui-content__playlist").get(index).select("li a");
        for (Element item : items) {
            VideoItem videoItem = new VideoItem();
//            try{
//                Thread.sleep(100);//延迟200毫秒
//            }catch(Exception e){
//                System.out.println("******* 线程异常 ********");
//            }
            System.out.println(item.text());
            videoItem.setName(item.text());
            videoItem.setVideId(video.getId());
            //访问url
            Document doc1 = videoUtils.setConnectionParam(TypeEnum.BASEURL.getUrl() + item.attr("href"));
            if(doc1 != null){
                String[] vars = doc1.select("div.embed-responsive").select("script").get(0).data().toString().split("var");
                String pn = "";
                String item_url = "";
                for (String var : vars) {
                    if(var.contains("pn=")){
                        pn = var.split("=")[1];
                        if(!"".equals(pn)){
                            pn = pn.substring(1,pn.length()-3);
                        }
                    }
                    if (var.contains("now=")) {
                        item_url = var.split("=")[1];
                    }
                }
                videoItem.setPn(pn);
                if(!"".equals(item_url)){
                    item_url = item_url.substring(1,item_url.length()-2);
                    if(item_url.contains(".m3u8")){//包含直接保存
                        videoItem.setUrl(item_url);
                    }else{//不包含则需要访问url进行提取
                        doc2 = videoUtils.setConnectionParam(item_url);
                        String videoItem_url = "";
                        if(doc2 != null){
                            Elements els = doc2.select("script");
                            String scriptString = "";
                            for (Element ele : els) {
                                String tempStr = ele.data().toString().replaceAll(" ","");
                                if(tempStr.length() > 88){
                                    scriptString = tempStr;
                                    break;
                                }
                            }

                            if(!"".equals(scriptString)){
                                //根据pn类型提取视频地址
                                if("zkyun".equals(pn)){
                                    videoItem_url = scriptString.split("var")[3].split("url:")[1];
                                    videoItem_url = videoItem_url.substring(1,videoItem_url.indexOf(".m3u8") + 5);
                                }else if("kuyun".equals(pn)){
                                    videoItem_url = scriptString.split("main=")[1];
                                    videoItem_url = videoItem_url.substring(1,videoItem_url.indexOf(".m3u8") + 5);
                                }else{
                                    message = "保存" + video.getName() + item.text() + "失败，原因是没有pn：'" + pn + "'的处理方案";
                                    LOGGER.error(message);
                                    System.out.println(message);
                                }
                                if(!"".equals(videoItem_url) && !videoItem_url.contains("http")){
                                    videoItem_url = item_url.substring(0,item_url.indexOf("/",10)) + videoItem_url;
                                }
                            }else{
                                message = "保存" + video.getName() + item.text() + "失败，原因是原网页没有script标签无法提取视频地址";
                                LOGGER.error(message);
                                System.out.println(message);
                            }
                        }else{
                            list = null;//如果发现其中一个为空则返回null
                            break;
                        }
                        videoItem.setUrl(videoItem_url);
                    }
                }
            }else{
                videoItem.setUrl("");
                message = "保存" + video.getName() + item.text() + "失败，原因是访问链接：" + TypeEnum.BASEURL.getUrl() + item.attr("href") + "失败";
                LOGGER.error(message);
                System.out.println(message);
            }
            list.add(videoItem);
        }
        return list;
    }

    public List<VideoGroup> getTest(){
        System.out.println(mainMapper.getVideoGroup().toString());
        return mainMapper.getVideoGroup();
    }
}
