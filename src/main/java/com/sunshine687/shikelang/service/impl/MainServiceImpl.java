package com.sunshine687.shikelang.service.impl;

import com.sunshine687.shikelang.mappers.MainMapper;
import com.sunshine687.shikelang.pojo.*;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
            List<Video> ll = new ArrayList<>();
            //获取迭代器，遍历每个视频的信息
            for (Element element : elements) {
                Video video = new Video();
                String href = element.attr("href");
                String video_name = element.attr("title");
                String video_imgUrl = element.attr("data-original");
                String video_updateStatus = element.select("span.pic-text").text();
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
                //获取剧集
                Elements items = doc.select("#playlist1 ul.stui-content__playlist li a");
                List<VideoItem> list = new ArrayList<>();
                for (Element item : items) {
                    VideoItem videoItem = new VideoItem();
                    videoItem.setName(item.text());
                    videoItem.setUrl(item.attr("href"));
                    list.add(videoItem);
                }
                video.setList(list);
                //创建时间
                video.setCreateTime(new Timestamp(new Date().getTime()));
                ll.add(video);
//                break;
            }
            System.out.println(ll.toString());
            break;
        }
    }

    public List<VideoGroup> getTest(){
        System.out.println(mainMapper.getVideoGroup().toString());
        return mainMapper.getVideoGroup();
    }
}
