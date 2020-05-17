package com.sunshine687.shikelang.pojo;

/**
 * 用于视频分类
 */
public enum VideoGroupEnum {
    电影(1,"电影"),
    电视剧(2,"电视剧"),
    动漫(3,"动漫("),
    综艺(4,"综艺"),
    纪录片(5,"纪录片");//记住要用分号结束

    private Integer no;
    private String name;
    VideoGroupEnum(Integer no,String name) {
        this.no = no;
        this.name = name;
    }

    public Integer getNo(){
        return no;
    }
    public String getName(){
        return name;
    }

}
