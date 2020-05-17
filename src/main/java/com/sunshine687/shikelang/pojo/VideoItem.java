package com.sunshine687.shikelang.pojo;

public class VideoItem {
    private Integer id;
    private String name;
    private String url;
    private String tempUrl;
    private String itemHref;
    private Integer updateIndex;
    private Integer videoId;

    public VideoItem(Integer id, String name, String url,String tempUrl,String itemHref,Integer updateIndex,Integer videoId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.tempUrl = tempUrl;
        this.itemHref = itemHref;
        this.updateIndex = updateIndex;
        this.videoId = videoId;
    }

    public VideoItem() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public void setTempUrl(String tempUrl) {
        this.tempUrl = tempUrl;
    }

    public String getItemHref() {
        return itemHref;
    }

    public void setItemHref(String itemHref) {
        this.itemHref = itemHref;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getUpdateIndex() {
        return updateIndex;
    }

    public void setUpdateIndex(Integer updateIndex) {
        this.updateIndex = updateIndex;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", tempUrl='" + tempUrl + '\'' +
                ", itemHref='" + itemHref + '\'' +
                ", updateIndex='" + updateIndex + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }
}
