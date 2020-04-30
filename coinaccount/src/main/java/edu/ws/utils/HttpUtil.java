package edu.ws.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


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
        AtomicReference<String> name = new AtomicReference<>("");
        JSONObject jsonObject = new JSONObject();
        params.forEach((k,v)->{
            jsonObject.put(k,v);
            name.set(v);
        });
        String result = HttpUtil.post(jsonObject, uriStr);
        System.out.println(name + "---->" + "响应结果:"+result);
        return result;
    }


    /**
     * 发送post请求
     * @param json
     * @param url
     * @return
     */
    public static String post(JSONObject json, String url){
        String result = "";
        HttpPost post = new HttpPost(url);
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.addHeader("Authorization", "Basic YWRtaW46");
            StringEntity postingString = new StringEntity(json.toString(),"utf-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);

            InputStream in = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            br.close();
            in.close();
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK){
                result = "服务器异常";
            }
        } catch (Exception e){
            System.out.println("请求异常");
            throw new RuntimeException(e.getMessage());
        } finally{
            post.abort();
        }
        return result;
    }

}


