package com.sunshine687.shikelang.service.impl;

import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.springframework.stereotype.Service;

@Service("mainService")
public class MainServiceImpl implements IMainService{
    private VideoUtils videoUtils = new VideoUtils();

    public void getVideoDetail(TypeEnum videoType){
        Integer total = videoUtils.getListTotal(videoType);
        String url_base = videoType.getUrl();
        String url_before = url_base.split("\\?")[0] + "?";
        String url_after = url_base.split("\\?")[1].substring(url_base.split("\\?")[1].indexOf("&"));

        for (int i = 0; i < total; i++) {
            String url = url_before + "page=" + (i+1) + url_after;
            System.out.println(url);
            String allVideoOfCurrentPage = videoUtils.getListByType(videoType);
            break;
        }
        System.out.println(total);
    }
}
