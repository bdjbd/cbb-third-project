package cn.hylexus.jt808.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import cn.hylexus.jt808.service.AsynchronousHttpPost;

public class TestAsynchronousHttpPost {
	public static void main(String[] argv) throws Exception {
		
//		JSONObject params = new JSONObject();
//		
//		params.put("terminalId","15170400007");
		
		AsynchronousHttpPost asynchronousHttpPost = new AsynchronousHttpPost();
		
		ArrayList<NameValuePair> reqParams = new ArrayList<NameValuePair>();
		reqParams.add(new BasicNameValuePair("terminalId", "15170400007"));
		
//		asynchronousHttpPost.AsynchronousHttpPost("http://192.168.1.135:8080/AmRes/com.am.nw.api.ApiHeartBeat.do", reqParams);
    }	
	
	/**
     * http post 异步请求
     * 1、创建 client 实例
     * 2、创建 post 实例
     * 3、设置 post 参数
     * 4、发送请求
     */
	public static void TestAsynchronousHttpPost(String url, JSONObject params) {
		
		Map<String,String> maps = (Map)JSON.parse(params.toString());  	
		
		// 1、创建 client 实例
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        CountDownLatch latch = new CountDownLatch(1);
        
        // 2、创建 post 实例
        HttpPost post = new HttpPost(url);
        
        ArrayList<NameValuePair> reqParams = null;
        if (maps != null && !maps.isEmpty()) {
            reqParams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> e : maps.entrySet()) {
                reqParams.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
        }
        
        System.err.println("[reqParams] = " + reqParams);
        
        // 3、设置 post 参数
        if (reqParams != null){
            
            try {
				post.setEntity(new UrlEncodedFormEntity(reqParams, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
      
        // 4、发送请求
        //httpclient.execute(post, new CallbackString());
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
