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
        String AllVideoOfCurrentPage = videoUtils.getListByType(videoType);
        System.out.println(total);
    }
}
