package com.sunshine687.shikelang.util;

import com.sunshine687.shikelang.pojo.TypeEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 视频工具类
 */
public class VideoUtils {
    /**
     * 通过视频类型（电视剧、电影等）获取列表
     */
    public String getListByType(TypeEnum videoType){
        String urlString = videoType.getUrl();
        Document doc = null;
        InputStream in = null;
        String html = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //使用代理且需要登录，添加这段代码
            /*conn.setRequestProperty("Proxy-Authorization", " Basic " +
            new BASE64Encoder().encode("用户名:密码".getBytes()));*/
            //该项必须配置，很多网站会拒绝非浏览器的访问，不设置会返回403，访问被服务器拒绝
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "text/html");
            conn.setRequestProperty("Connection", "close");
            conn.setUseCaches(false);
            conn.setConnectTimeout(5 * 1000);
            String encode = "utf-8";
            in = conn.getInputStream();
            doc = Jsoup.parse(in,encode,urlString);
            html = doc.toString();
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return html;
    }

    /**
     * 获取列表总页数
     */
    public Integer getListTotal(TypeEnum videoType){
        String urlStr = videoType.getUrl();
        Document doc = setConnectionParam(urlStr);
        int total = 1;
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
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //使用代理且需要登录，添加这段代码
            /*conn.setRequestProperty("Proxy-Authorization", " Basic " +
            new BASE64Encoder().encode("用户名:密码".getBytes()));*/
            //该项必须配置，很多网站会拒绝非浏览器的访问，不设置会返回403，访问被服务器拒绝
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "text/html");
            conn.setRequestProperty("Connection", "close");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10 * 1000);
            in = conn.getInputStream();
            String encode = "utf-8";
            doc = Jsoup.parse(in,encode,urlStr);
        }catch (Exception e) {
                e.printStackTrace();
        }finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return doc;
    }
}
