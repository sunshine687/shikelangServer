package com.sunshine687.shikelang.service;

import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.pojo.Video;
import com.sunshine687.shikelang.pojo.VideoGroup;
import com.sunshine687.shikelang.pojo.VideoItem;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

public interface IMainService {
    /**
     * 获取视频详细信息
     * @param type 视频组别
     */
    void getVideoDetail(TypeEnum type);

    /**
     * 更新视频详细信息
     * @param videoType 视频类别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务是否成功
     */
    boolean updateVideoDetail(TypeEnum videoType,Integer startDate,Integer endDate);

    /**
     * 抓取当前视频信息
     * @param videoType 视频类型
     * @param href 获取视频的地址
     * @return 返回视频对象
     */
    Video catchCurrentVideo(TypeEnum videoType, String href, Document doc);

    /**
     * 执行下次任务
     * @param videoType
     */
    public void doNextTask(TypeEnum videoType,Integer index);

    /**
     * 获取当前视频信息
     * @param videoType 视频组别
     * @param href 视频页面地址
     * return 是否存入成功
     */
    public void getCurrentVideo(TypeEnum videoType,String href);

    /**
     * 重复尝试获取对象集合
     */
    public Map<String, List> getItemsAgain(List<VideoItem> lists, List<Integer> waitList);

    /**
     * 根据url获取真实视频地址
     * @param tempUrl 网页url
     * @return 真实url
     */
    String getRealUrl(String tempUrl);

    /**
     * 测试
     */
    void getTest();
}
