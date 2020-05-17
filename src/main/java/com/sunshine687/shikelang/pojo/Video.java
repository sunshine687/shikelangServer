package com.sunshine687.shikelang.pojo;

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
    private String year; //年份
    private String area; //地区
    private String director; //导演
    private String mainPerformer; //霍建华,李兰杰（多个用英文,隔开）
    private Integer videoGroupId;//组别id，电影:1|电视剧:2|动漫:3|综艺:4|纪录片:5
    private String type;//分类，如国产剧，日韩剧
    private String instruction; //简介
    private Timestamp createTime; //创建时间
    private Timestamp modifyTime; //修改时间
    private String updateTime; //更新时间
    private String updateUrl; //用于更新视频的url
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
                ", type=" + type +
                ", instruction='" + instruction + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", updateTime=" + updateTime +
                ", updateUrl=" + updateUrl +
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public List<VideoItem> getList() {
        return list;
    }

    public void setList(List<VideoItem> list) {
        this.list = list;
    }

    public Video(Integer id, String name,String imgUrl, String updateStatus, String year, String area, String director, String mainPerformer, Integer videoGroupId, String type,String instruction, Timestamp createTime,  Timestamp modifyTime, String updateTime, String updateUrl, List<VideoItem> list) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.updateStatus = updateStatus;
        this.year = year;
        this.area = area;
        this.director = director;
        this.mainPerformer = mainPerformer;
        this.videoGroupId = videoGroupId;
        this.type = type;
        this.instruction = instruction;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.updateTime = updateTime;
        this.updateUrl = updateUrl;
        this.list = list;
    }
}
