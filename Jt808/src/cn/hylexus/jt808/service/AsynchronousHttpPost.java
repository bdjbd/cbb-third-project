package cn.hylexus.jt808.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.util.HttpAsyncMap;
import io.netty.channel.Channel;

public class AsynchronousHttpPost {
	
	 Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 异步HttpPost请求 
	 * @param url			请求地址
	 * @param reqParams		请求参数
	 * @param ctx			连接管道
	 */
	public  void AsynchronousHttpPost(String url, ArrayList<NameValuePair> reqParams ,Channel ctx) {
		
		log.info("[AsynchronousHttpPost发送请求进入方法！]");

        // 创建 post 实例
        HttpPost post = new HttpPost(url);
        
        System.err.println("<<<<<<[reqParams]>>>>>> = " + reqParams);
        
        // 设置 post 参数
        if (reqParams != null){
            
            try {
				post.setEntity(new UrlEncodedFormEntity(reqParams, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
      
        log.info("[AsynchronousHttpPost发送请求开始！]");
        // 发送请求
        CloseableHttpAsyncClient asyncClient =	HttpAsyncMap.getGatewayChannel(ctx.id().asLongText());
        asyncClient.execute(post, new CallbackString());
        log.info("[AsynchronousHttpPost发送请求完成！]");
	}
}
