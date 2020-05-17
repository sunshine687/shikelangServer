package com.sunshine687.shikelang.pojo;

/**
 * 用于视频类型获取地址的枚举类
 */
public enum TypeEnum {
    BASEURL("https://kuyun.tv",0,""),
    DIANSHIJU_GC("https://kuyun.tv/vod/show/id/13.html",2,"国产剧"),
    DIANSHIJU_GT("https://kuyun.tv/vod/type/id/14.html",2,"港台剧"),
    DIANSHIJU_RH("https://kuyun.tv/vod/type/id/15.html",2,"日韩剧"),
    DIANSHIJU_OM("https://kuyun.tv/vod/type/id/16.html",2,"欧美剧"),
    DIANSHIJU_QT("https://kuyun.tv/vod/type/id/22.html",2,"其他剧");

    private final String url;
    private final Integer groupId;
    private final String name;
    TypeEnum(String url,Integer groupId,String name) {
        this.url = url;
        this.groupId = groupId;
        this.name = name;
    }

    public String getUrl(){
        return url;
    }
    public Integer getGroupId(){
        return groupId;
    }
    public String getName(){
        return name;
    }
}
