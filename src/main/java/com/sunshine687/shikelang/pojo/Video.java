package com.sunshine687.shikelang.pojo;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 * 视频详情类
 */
public class Video {
    private Integer id; //数据库中的ID
    private String name; //视频名称
    private String imgUrl; //视频封面地址
    private String updateStatus; //更新状态，如：更新至36集
    private Integer year; //年份
    private String area; //地区
    private String director; //导演
    private String mainPerformer; //霍建华,李兰杰（多个用英文,隔开）
    //视频分类id，电影:1|电视剧:2|动漫:3|综艺:4|纪录片:5|日韩剧:6|欧美剧:7|港台剧:8|大陆剧:9|动作片:10|
    //爱情片:11|科幻片:12|恐怖片:13|喜剧片:14|剧情片:15|战争片:16
    private Integer videoGroupId;
    private String instruction; //简介
    private Timestamp createTime; //创建时间
    private Timestamp modifyTime; //修改时间
    private String updateTime; //更新时间
    private List<VideoItem> list; //视频剧集列表

    public Video() {
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", updateStatus='" + updateStatus + '\'' +
                ", year=" + year +
                ", area='" + area + '\'' +
                ", director='" + director + '\'' +
                ", mainPerformer='" + mainPerformer + '\'' +
                ", videoGroupId=" + videoGroupId +
                ", instruction='" + instruction + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", updateTime=" + updateTime +
                ", list=" + list +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getMainPerformer() {
        return mainPerformer;
    }

    public void setMainPerformer(String mainPerformer) {
        this.mainPerformer = mainPerformer;
    }

    public Integer getVideoGroupId() {
        return videoGroupId;
    }

    public void setVideoGroupId(Integer videoGroupId) {
        this.videoGroupId = videoGroupId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<VideoItem> getList() {
        return list;
    }

    public void setList(List<VideoItem> list) {
        this.list = list;
    }

    public Video(Integer id, String name,String imgUrl, String updateStatus, Integer year, String area, String director, String mainPerformer, Integer videoGroupId, String instruction, Timestamp createTime,  Timestamp modifyTime, String updateTime, List<VideoItem> list) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.updateStatus = updateStatus;
        this.year = year;
        this.area = area;
        this.director = director;
        this.mainPerformer = mainPerformer;
        this.videoGroupId = videoGroupId;
        this.instruction = instruction;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.updateTime = updateTime;
        this.list = list;
    }
}
