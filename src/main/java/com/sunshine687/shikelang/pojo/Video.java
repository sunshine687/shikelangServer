package com.sunshine687.shikelang.pojo;

import java.sql.Timestamp;
import java.util.List;

/**
 * 视频详情类
 */
public class Video {
    private Integer id;
    private String name;
    private String updateStatus;
    private Integer year;
    private String area;
    private String director; //导演
    private String mainPerformer; //霍建华,李兰杰（多个用，隔开）
    private Integer videoGroupId; //电视剧、电影等
    private Integer videoTypeId; //剧情片等
    private String instruction;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<VideoItem> list;

    public Video() {
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", updateStatus='" + updateStatus + '\'' +
                ", year=" + year +
                ", area='" + area + '\'' +
                ", director='" + director + '\'' +
                ", mainPerformer='" + mainPerformer + '\'' +
                ", videoGroupId=" + videoGroupId +
                ", videoTypeId=" + videoTypeId +
                ", instruction='" + instruction + '\'' +
                ", createTime=" + createTime +
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

    public Integer getVideoTypeId() {
        return videoTypeId;
    }

    public void setVideoTypeId(Integer videoTypeId) {
        this.videoTypeId = videoTypeId;
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

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public List<VideoItem> getList() {
        return list;
    }

    public void setList(List<VideoItem> list) {
        this.list = list;
    }

    public Video(Integer id, String name, String updateStatus, Integer year, String area, String director, String mainPerformer, Integer videoGroupId, Integer videoTypeId, String instruction, Timestamp createTime, Timestamp updateTime, List<VideoItem> list) {
        this.id = id;
        this.name = name;
        this.updateStatus = updateStatus;
        this.year = year;
        this.area = area;
        this.director = director;
        this.mainPerformer = mainPerformer;
        this.videoGroupId = videoGroupId;
        this.videoTypeId = videoTypeId;
        this.instruction = instruction;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.list = list;
    }
}
