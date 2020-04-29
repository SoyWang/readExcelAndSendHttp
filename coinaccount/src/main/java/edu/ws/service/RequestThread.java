package edu.ws.service;

import edu.ws.utils.HttpUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 王松
 *  循环发送http的post请求
 */
public class RequestThread {
    //系统核心数个线程池
    private static ExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private int delay = 3;//延时

    private RequestThread(){}

    /**
     * 运行
     */
    public static void run(String url, List<String> researchUsers){
        researchUsers.forEach( user -> {
            Map<String,String> map = new ConcurrentHashMap<>();
            map.put("username",user); //模拟页面传递的参数
            executor.execute(new DoRequest(url,map));
        });
        executor.shutdown();
    }



    private static class DoRequest implements Runnable{
        String url;
        Map params;

        public DoRequest(String url, Map params){
            this.url = url;
            this.params = params;
        }

        @Override
        public void run() {
            try {
                String response = HttpUtil.doPostAndParam(url,params);
                System.out.println("添加账户接口响应结果："+response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
