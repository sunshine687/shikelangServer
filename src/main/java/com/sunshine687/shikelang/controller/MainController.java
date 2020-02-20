package com.sunshine687.shikelang.controller;

import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;


//@CrossOrigin(origins = "*", maxAge = 10000)   //处理跨域请求(接收来自某个地址的跨域请求)
@CrossOrigin(origins = "${corss.url}", maxAge = 10000)   //处理跨域请求(接收来自某个地址的跨域请求)
@RestController
@RequestMapping("/shikelang")
public class MainController {

    private final IMainService mainService;
    @Autowired
    public MainController(IMainService mainService) {
        this.mainService = mainService;
    }

    @Value("${server.port}")
    private String port;

    /**
     * 测试用
     * @return 测试返回值
     */
    @RequestMapping("/test")
    public String test(){
        return port;
    }

    /**
     * 获取视频列表
     */
    @RequestMapping("/getVideoList")
    public void getVideoList(@RequestParam("type") String type){
        TypeEnum typeEnum = null;
        if(type.equals("电视剧")){
            typeEnum = TypeEnum.DIANSHIJU;
        }
//        mainService.getVideoDetail(typeEnum);
        mainService.getTest();
    }
}
