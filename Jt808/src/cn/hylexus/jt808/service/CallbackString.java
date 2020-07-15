package cn.hylexus.jt808.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gaowenfeng on 2017/8/23.
 * 回调类结果封装
 * 实现FutureCallback接口
 */

public class CallbackString implements FutureCallback<HttpResponse>{
	
	Logger log = LoggerFactory.getLogger(getClass());

	/**
     * 成功返回时的回调方法
     * @param response
     */
	
    public void completed(HttpResponse response) {
        String content = "";
        try {
            content = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.debug("<<<<<<<<<<API返回值>>>>>>>>>> = " + content);
            System.err.println("<<<<<<<<<<API返回值>>>>>>>>>> = " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 失败时候的回调方法
     * @param e
     */
    public void failed(Exception e) {
        e.printStackTrace();
    }

    /**
     * 取消时候的回调方法
     */
    public void cancelled() {
    }
}
