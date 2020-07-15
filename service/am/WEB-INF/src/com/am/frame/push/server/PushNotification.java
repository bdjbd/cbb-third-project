package com.am.frame.push.server;

import org.json.JSONObject;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushBroadcastMessageRequest;
import com.baidu.yun.channel.model.PushBroadcastMessageResponse;
import com.baidu.yun.channel.model.PushTagMessageRequest;
import com.baidu.yun.channel.model.PushTagMessageResponse;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;

public class PushNotification 
{
	private String mUserID="";
	private Long mChannelID;
	
	// 1. 设置developer平台的ApiKey/SecretKey
	private String apiKey = "ecIk3dOXbAZWKAsnrZvZHjWO";
	private String secretKey = "NZqKYWdR8nAMtkrppqMWoNVc3xZIP9PH";
	
	/**
	 * 构建推送通知类
	 * @param userid 百度云推送产生的用户设备id
	 * @param channelid 百度云推送的开发者代码
	 */
	public PushNotification(String userid,Long channelId)
	{
		mUserID=userid;
		mChannelID=channelId;
	}
	
	/**
	 * 向用户终端推送消息
	 * @param msg 要推送的消息内容
	 * @param device_type 要推送的设备类型 device_type => 1: web 2: pc 3:android 4:ios 5:wp
	 */
	public Boolean send(String title,String description,JSONObject jsonObj,Integer device_type) 
	{
		Boolean rValue=true;
		
		/*
         * @brief 推送单播通知(Android Push SDK拦截并解析) message_type = 1 (默认为0)
         */
        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        // 3. 若要了解交互细节，请注册YunLogHandler类
        channelClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

        try {

            // 4. 创建请求类对象
            // 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
            PushUnicastMessageRequest request = new PushUnicastMessageRequest();
            request.setDeviceType(device_type); // device_type => 1: web 2: pc 3:android
                                      // 4:ios 5:wp

			request.setChannelId(mChannelID);
			request.setUserId(mUserID);
			request.setMessageType(1);

            
            request.setMessage("{\"title\":\"" + title + "\""
            		+ ",\"description\":\"" + description + "\""
            		+ ",\"custom_content\" : " + jsonObj.toString() + "}");

            // 5. 调用pushMessage接口
            PushUnicastMessageResponse response = channelClient
                    .pushUnicastMessage(request);

            // 6. 认证推送成功
            System.out.println("push amount : " + response.getSuccessAmount());

        } catch (ChannelClientException e) {
            // 处理客户端错误异常
            e.printStackTrace();
            
            rValue=false;
        } catch (ChannelServerException e) {
            // 处理服务端错误异常
            System.out.println(String.format(
                    "request_id: %d, error_code: %d, error_message: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            rValue=false;
        }
		
		return rValue;
	}
	
	/**
	 * 向所有用户终端推送消息
	 * @param msg 要推送的消息内容
	 * @param device_type 要推送的设备类型 device_type => 1: web 2: pc 3:android 4:ios 5:wp
	 */
	public Boolean sendAll(String title,String description,JSONObject jsonObj,Integer device_type) 
	{
		Boolean rValue=true;
		
		// 1. 设置developer平台的ApiKey/SecretKey
        ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        // 3. 若要了解交互细节，请注册YunLogHandler类
        channelClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

        try {

            // 4. 创建请求类对象
            PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
            request.setMessageType(1);
            request.setDeviceType(device_type);
            request.setMessage("{\"title\":\"" + title + "\""
            		+ ",\"description\":\"" + description + "\""
            		+ ",\"custom_content\" : " + jsonObj.toString() + "}");

            // 5. 调用pushMessage接口
            PushBroadcastMessageResponse response = channelClient
                    .pushBroadcastMessage(request);
            if (response.getSuccessAmount() == 1) {
                // TODO
            }

        } catch (ChannelClientException e) {
            // 处理客户端错误异常
            e.printStackTrace();
            rValue=false;
        } catch (ChannelServerException e) {
            // 处理服务端错误异常
            System.out.println(String.format(
                    "request_id: %d, error_code: %d, error_message: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            rValue=false;
        }
		
		return rValue;
	}
	
	/**
	 * 向所有Tag终端推送消息
	 * @param msg 要推送的消息内容
	 * @param device_type 要推送的设备类型 device_type => 1: web 2: pc 3:android 4:ios 5:wp
	 */
	public Boolean sendTag(String tag,String title,String description,JSONObject jsonObj,Integer device_type) 
	{
		Boolean rValue=true;
		
		// 1. 设置developer平台的ApiKey/SecretKey
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

        // 2. 创建BaiduChannelClient对象实例
        BaiduChannelClient channelClient = new BaiduChannelClient(pair);

        // 3. 若要了解交互细节，请注册YunLogHandler类
        channelClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

        try {

            // 4. 创建请求类对象
            PushTagMessageRequest request = new PushTagMessageRequest();
            request.setMessageType(1);
            request.setDeviceType(3);
            request.setTagName(tag);
            request.setMessage("{\"title\":\"" + title + "\""
            		+ ",\"description\":\"" + description + "\""
            		+ ",\"custom_content\" : " + jsonObj.toString() + "}");

            // 5. 调用pushMessage接口
            PushTagMessageResponse response = channelClient
                    .pushTagMessage(request);
            if (response.getSuccessAmount() == 1) {
                // TODO
            }

        } catch (ChannelClientException e) {
            // 处理客户端错误异常
            e.printStackTrace();
            rValue=false;
        } catch (ChannelServerException e) {
            // 处理服务端错误异常
            System.out.println(String.format(
                    "request_id: %d, error_code: %d, error_message: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            rValue=false;
        }
		
		return rValue;
	}
}
