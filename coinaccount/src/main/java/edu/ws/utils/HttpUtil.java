package edu.ws.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author 王松
 * HttpClient工具类
 */
public class HttpUtil {

    /**
     * 发送带参数的post请求
     * @param uriStr
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPostAndParam(String uriStr,Map<String,String> params) throws Exception{
        //设置请求方式与参数
        URI uri = new URI(uriStr);
        HttpPost httpPost = new HttpPost(uri);
        httpPost.getParams().setParameter("http.socket.timeout", new Integer(500000));//请求超时时间
        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
//        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
        httpPost.setHeader("IConnection", "close");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        params.forEach((k,v)->{
            nvps.add(new BasicNameValuePair(k, v));
        });
        //...
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));


//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("KEY1", "VALUE1");
//        jsonObject.put("KEY2", "VALUE2");
//        httpPost.setEntity(new StringEntity(jsonObject.toString()));

        //执行请求
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter("Content-Encoding", "UTF-8");
        HttpResponse response = httpclient.execute(httpPost);

        //获取返回
        HttpEntity entity = response.getEntity();
        BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }


}


