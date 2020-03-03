package com.sunshine687.shikelang.util;

import com.sunshine687.shikelang.pojo.TypeEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 视频工具类
 */
public class VideoUtils {

    /**
     * 获取列表总页数
     */
    public Integer getListTotal(TypeEnum videoType){
        String urlStr = videoType.getUrl();
        int total = 1;
        Document doc = setConnectionParam(urlStr);
        Elements elements = doc.select("ul.myui-page .visible-xs a.btn-warm");
        String totalStr = elements.get(0).html();
        total = Integer.parseInt(totalStr.substring(totalStr.indexOf("/") + 1));
        return total;
    }

    /**
     * 设置访问链接需要的参数
     * @param urlStr 链接地址
     * @return 返回doc对象
     */
    public Document setConnectionParam(String urlStr){
        Document doc = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            //使用代理且需要登录，添加这段代码
            /*conn.setRequestProperty("Proxy-Authorization", " Basic " +
            new BASE64Encoder().encode("用户名:密码".getBytes()));*/
            //该项必须配置，很多网站会拒绝非浏览器的访问，不设置会返回403，访问被服务器拒绝
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "text/html");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(100 * 1000); //读取时间100s
            conn.connect();
            Thread.sleep(5 * 1000);//睡眠5秒钟，让数据读取完毕
            int state = conn.getResponseCode();
            if(state == 200){
                in = conn.getInputStream();
                String encode = "utf-8";
                InputStreamReader inputStreamReader = new InputStreamReader(
                        in, encode);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                StringBuffer sb = new StringBuffer();
                int BUFFER_SIZE = 10240;
                char[] buffer = new char[BUFFER_SIZE]; // or some other size,
                int charsRead = 0;
                while ( (charsRead  = bufferedReader.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    sb.append(buffer, 0, charsRead);
                }
                doc = Jsoup.parse(sb.toString());
            }
        }catch (Exception e) {
            System.out.println("工具内处理异常"+urlStr);
            doc = null;
            e.printStackTrace();
        }finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != conn){
                conn.disconnect();
            }
        }
        return doc;
    }
}
