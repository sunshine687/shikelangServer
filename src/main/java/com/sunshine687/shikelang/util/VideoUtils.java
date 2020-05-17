package com.sunshine687.shikelang.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sunshine687.shikelang.pojo.TypeEnum;
import com.sunshine687.shikelang.pojo.Video;
import com.sunshine687.shikelang.pojo.VideoItem;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        Document doc = httpGet_setTime(urlStr, 20);
        if(doc != null){
            Elements elements = doc.select("a.fed-page-jump");
            String totalStr = elements.get(0).attr("data-total");
            total = Integer.parseInt(totalStr);
        }else{
            System.out.println("获取页面总页码发出异常");
        }
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
            conn.setRequestProperty("Connection", "close");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(100 * 1000); //读取时间100s
            if(conn.getResponseCode() == 200){
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


    /**
     * 使用httpClient访问url
     * @param urlStr url
     * @return Document
     */
    public Document httpGet(String urlStr){
        Document doc = null;   //get请求返回结果
        try {
            CloseableHttpClient httpclient =  HttpClientBuilder.create().build();
            //发送get请求
            HttpGet request = new HttpGet(urlStr);
            URI realUrl = new URI(urlStr);
            request.setURI(realUrl);
            request.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            request.setHeader("Content-type", "text/html");
            request.setHeader("Connection", "close");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10 * 1000).setConnectTimeout(60 * 1000).build();//设置请求和传输超时时间
            request.setConfig(requestConfig);
            HttpResponse response = httpclient.execute(request);

            /* 请求发送成功，并得到响应 */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /*读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                doc = Jsoup.parse(strResult);
            } else {
                System.out.println("get请求提交失败:" + urlStr);
            }
        } catch (Exception e) {

        }
        return doc;
    }

    /**
     * post请求以及参数是json
     */
    public String doPostForJson(String url, String jsonParams) {
        String returnStr = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonParams, ContentType.create("application/json", "utf-8")));
        try {

            //执行请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取返回值
            String html = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if(!"".equals(html)){
                returnStr = html;
            }
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnStr;
    }

    /**
     * key-value形式发送post
     * @param url 请求地址
     * @param paramsMap 参数map
     * @return 返回值
     */
    public String doPost(String url, Map<String, Object> paramsMap) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        //配置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)
                .build();
        HttpPost httpPost = new HttpPost(url);
        //设置超时时间
        httpPost.setConfig(requestConfig);

        //装配post请求参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String key : paramsMap.keySet()) {
            list.add(new BasicNameValuePair(key, String.valueOf(paramsMap.get(key))));
        }

        try {
            //将参数进行编码为合适的格式,如将键值对编码为param1=value1&param2=value2
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(urlEncodedFormEntity);

            //执行 post请求
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            String strRequest = "";
            if (null != closeableHttpResponse && !"".equals(closeableHttpResponse)) {
                System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    strRequest = EntityUtils.toString(httpEntity);
                } else {
                    strRequest = "Error Response" + closeableHttpResponse.getStatusLine().getStatusCode();
                }
            }
            return strRequest;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "协议异常";
        } catch (ParseException e) {
            e.printStackTrace();
            return "解析异常";
        } catch (IOException e) {
            e.printStackTrace();
            return "传输异常";
        } finally {
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
















    /**
     * 使用httpClient访问url
     * @param urlStr url
     * @param longTime 单位秒
     * @return Document
     */
    public Document httpGet_setTime(String urlStr,Integer longTime){
        Document doc = null;   //get请求返回结果
        try {
            CloseableHttpClient httpclient =  HttpClientBuilder.create().build();
            //发送get请求
            HttpGet request = new HttpGet(urlStr);
            URI realUrl = new URI(urlStr);
            request.setURI(realUrl);
            request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:39.0) Gecko/20100101 Firefox/39.0");
            request.setHeader("Content-type", "text/html");
            request.setHeader("Connection", "keep-alive");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(longTime * 1000).setConnectTimeout(60 * 1000).build();//设置请求和传输超时时间
            request.setConfig(requestConfig);
            HttpResponse response = httpclient.execute(request);

            /* 请求发送成功，并得到响应 */
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /*读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                doc = Jsoup.parse(strResult);
                if(doc == null){
                    doc = httpGet_setTime(urlStr,longTime);
                }
            }
        } catch (Exception e) {
            System.out.println("get请求发生异常,获取页面源码失败");
            doc = null;
        }
        return doc;
    }

    /**
     * 获取页面文档字串(等待异步JS执行)
     *
     * @param url 页面URL
     * @return
     * @throws Exception
     */
    public String getHtmlPageResponse(String url) {
        String result = "";
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);//是否启用CSS
//        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

        webClient.getOptions().setTimeout(15 * 1000);//设置“浏览器”的请求超时时间
        webClient.getCache().setMaxSize(400);
//        webClient.setJavaScriptTimeout(30 * 1000);//设置JS执行的超时时间

        HtmlPage page;
        try {
            page = webClient.getPage(url);
            if(page != null){
                result = page.asXml();
            }
            webClient.close();
            System.gc();
        } catch (Exception e) {
            result = "";
            webClient.close();
            System.gc();
        }
        webClient.waitForBackgroundJavaScript(1000);//该方法阻塞线程
        return result;
    }

    /**
     * 判断库中是否存在视频，存在则返回video实例，否则返回null
     * @param videos videos
     * @param updateHref updateHref
     * @return return
     */
    public Video isExistVideo(List<Video> videos,String updateHref){
        Video v = null;
        for(Video video : videos){
            if(updateHref.equals(video.getUpdateUrl())){
                v = video;
                break;
            }
        }
        return v;
    }

    /**
     * 判断库中是否存在视频剧集，存在则返回videoItem实例，否则返回null
     * @param videoItems 库中的剧集列表
     * @param videoItemName 获取到的视频剧集名称
     * @return return
     */
    public VideoItem isExistVideoItem(List<VideoItem> videoItems, String videoItemName){
        List<String> rule = getRules(videoItemName);
        VideoItem v = null;
        for(VideoItem videoItem : videoItems){
            if(rule == null){
                if(videoItemName.equals(videoItem.getName())){
                    v = videoItem;
                    break;
                }
            }else{
                //判断从库中获取到的名称是否符合规则之一，如果符合则存在，否则不存在
                if(rule.contains(videoItem.getName())){
                    v = videoItem;
                    break;
                }
            }
        }
        return v;
    }

    /**
     * 深度克隆
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * 获取兼容规则
     * @return
     */
    public List<String> getRules(String videoItemName){
        List<List<String>> rules = new ArrayList<>();
        List<String> one = new ArrayList<>();
        one.add("第一集");
        one.add("第1集");
        one.add("第01集");
        rules.add(one);

        List<String> two = new ArrayList<>();
        two.add("第二集");
        two.add("第2集");
        two.add("第02集");
        rules.add(two);

        List<String> three = new ArrayList<>();
        three.add("第三集");
        three.add("第3集");
        three.add("第03集");
        rules.add(three);

        List<String> four = new ArrayList<>();
        four.add("第四集");
        four.add("第4集");
        four.add("第04集");
        rules.add(four);

        List<String> five = new ArrayList<>();
        five.add("第五集");
        five.add("第5集");
        five.add("第05集");
        rules.add(five);

        List<String> six = new ArrayList<>();
        six.add("第六集");
        six.add("第6集");
        six.add("第06集");
        rules.add(six);

        List<String> seven = new ArrayList<>();
        seven.add("第七集");
        seven.add("第7集");
        seven.add("第07集");
        rules.add(seven);

        List<String> eight = new ArrayList<>();
        eight.add("第八集");
        eight.add("第8集");
        eight.add("第08集");
        rules.add(eight);

        List<String> nine = new ArrayList<>();
        nine.add("第九集");
        nine.add("第9集");
        nine.add("第09集");
        rules.add(nine);

        for (List<String> rule : rules) {
            //根据name匹配规则，有则返回规则列表，如果没有匹配到则返回null
            if (rule.contains(videoItemName)) {
                return rule;
            }
        }
        return null;
    }
}
