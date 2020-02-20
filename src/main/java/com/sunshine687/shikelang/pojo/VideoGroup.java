package com.sunshine687.shikelang.pojo;

public class VideoGroup {
    private Integer id;
    private String name;

    public VideoGroup(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public VideoGroup() {
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

    @Override
    public String toString() {
        return "VideoGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
