package com.sunshine687.shikelang.pojo;

/**
 * 用于视频类型获取地址的枚举类
 */
public enum TypeEnum {
    BASEURL("http://www.panpanso.com"),
    DIANSHIJU("http://www.panpanso.com/search.php?page=1&searchtype=5&order=hit&tid=2");//记住要用分号结束

    private String url;
    TypeEnum(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}
