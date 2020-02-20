package com.sunshine687.shikelang.pojo;

/**
 * 用于视频分类
 */
public enum VideoGroupEnum {
    电影(1,"电影"),
    电视剧(2,"电视剧"),
    动漫(3,"动漫("),
    综艺(4,"综艺"),
    纪录片(5,"纪录片"),
    日韩剧(6,"日韩剧"),
    欧美剧(7,"欧美剧"),
    港台剧(8,"港台剧"),
    大陆剧(9,"大陆剧"),
    动作片(10,"动作片"),
    爱情片(11,"爱情片"),
    科幻片(12,"科幻片"),
    恐怖片(13,"恐怖片"),
    喜剧片(14,"喜剧片"),
    剧情片(15,"剧情片"),
    战争片(16,"战争片");//记住要用分号结束

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
