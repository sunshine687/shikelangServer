package com.sunshine687.shikelang.service;

import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.pojo.VideoGroup;

import java.util.List;

public interface IMainService {
    void getVideoDetail(TypeEnum videoType);
    List<VideoGroup> getTest();
}
