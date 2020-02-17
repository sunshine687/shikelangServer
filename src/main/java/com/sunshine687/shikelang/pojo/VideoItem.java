package com.sunshine687.shikelang.pojo;

public class VideoItem {
    private Integer id;
    private String name;
    private String url;

    public VideoItem(Integer id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
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

    @Override
    public String toString() {
        return "VideoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
