package com.sunshine687.shikelang.controller;

import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.pojo.VideoItem;
import com.sunshine687.shikelang.service.IMainService;
import com.sunshine687.shikelang.util.VideoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


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

    private final VideoUtils videoUtils = new VideoUtils();

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd") ;

    //每天小时数为3的倍数时，执行任务
    @Scheduled(cron = "0 0 0,3,6,9,12,15,18,21 * * ?")
    public void task() {
        updateDianShiJu();
    }

    /**
     * 测试用
     * @return 测试返回值
     */
    @RequestMapping("/test")
    public String test(){
        List<VideoItem> lists = new ArrayList<>();
        VideoItem v1 = new VideoItem();
        v1.setUpdateIndex(2);
        lists.add(v1);

        VideoItem v2 = new VideoItem();
        v2.setUpdateIndex(1);
        lists.add(v2);

        lists.sort(new Comparator<VideoItem>() {
            public int compare(VideoItem o1, VideoItem o2) {
                //按照CityModel的city_code字段进行升序排列
                if (o1.getUpdateIndex() > o2.getUpdateIndex()) {
                    return 1;
                }
                if (o1.getUpdateIndex().equals(o2.getUpdateIndex())) {
                    return 0;
                }
                return -1;
            }
        });
        System.out.println(lists.toString());
//        Document dd = videoUtils.httpGet_setTime("https://iqiyi.cdn8-okzy.com/share/56a8da1d3bcb2e9b334a778be5b1d781",30);
//        System.out.println(dd);
//       mainService.getCurrentVideo(TypeEnum.DIANSHIJU_GC,"/vod/detail/id/35292.html");
//        mainService.getTest();
        return "|";
    }



    /**
     * 获取电视剧列表
     */
    @RequestMapping("/insertDianShiJu")
    public void insertDianShiJu(){
        //国产
        mainService.getVideoDetail(TypeEnum.DIANSHIJU_GC);
//        //港台
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_GT);
//        //日韩
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_RH);
//        //欧美
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_OM);
//        //其他
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_QT);
    }

    @RequestMapping("/updateDianShiJu")
    public void updateDianShiJu(){
        Integer startDate = Integer.parseInt(dateFormat.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000)));
        Integer endtDate = Integer.parseInt(dateFormat.format(new Date()));

        //国产
        mainService.updateVideoDetail(TypeEnum.DIANSHIJU_GC,startDate,endtDate);

//        //港台
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_GT);
//        //日韩
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_RH);
//        //欧美
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_OM);
//        //其他
//        mainService.getVideoDetail(TypeEnum.DIANSHIJU_QT);
    }


    @RequestMapping("/createOrder")
    public String createOrder(String payId,Integer type,String price,String param){
        //拿到参数创建md5加密
        String key = "d213b73c984402e2eeaccbbe21904f3d";
        String md5 = payId + param + type + price + key;
        String sign = DigestUtils.md5DigestAsHex(md5.getBytes(StandardCharsets.UTF_8));

        System.out.println("|" + md5 + "|");
        System.out.println("|" + sign + "|");

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("payId",payId);
        map.put("type",type);
        map.put("price",price);
        map.put("param",param);
        map.put("sign",sign);
        return videoUtils.doPost("http://120.77.227.5:8080/createOrder",map);
    }

    @RequestMapping("/searchOrder")
    public String searchOrder(String orderId){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("orderId",orderId);
        return videoUtils.doPost("http://120.77.227.5:8080/checkOrder",map);
    }

    @RequestMapping("/payReturn")
    public String testRequest(HttpServletRequest request){
        System.out.println("访问payReturn");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String str = "";
            StringBuilder wholeStr = new StringBuilder();
            while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
                wholeStr.append(str);
            }
            System.out.println(wholeStr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "test";
    }
}
