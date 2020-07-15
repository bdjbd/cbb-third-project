package com.p2p.service.bt;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;

/*
 * @brief 推送单播消息(消息类型为透传，由开发方应用自己来解析消息内容) message_type = 0 (默认为0)
 * 调用百度云推送，向用户终端发送消息
 */
/**
 * @author Administrator
 *
 */
public class SendMessageManager 
{
	private String mUserID;
	private Long mChannelID;
	
	/**
	 * 构建推送消息类
	 * @param userid 百度云推送产生的用户设备id
	 * @param channelid 百度云推送的开发者代码
	 */
	public SendMessageManager(String userid,Long channelId)
	{
		mUserID=userid;
		mChannelID=channelId;
	}
	
	/**
	 * 向用户终端推送消息
	 * @param msg 要推送的消息内容
	 * @param device_type 要推送的设备类型 device_type => 1: web 2: pc 3:android 4:ios 5:wp
	 */
	public String send(String msg,Integer device_type) 
	{
		String rValue="true";
		
		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "iywlFX8lwKAMv6qargj4pEHf";
		String secretKey = "eWCpx03bnHfzRE2ycHf7qXA974GY0MiG";
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);

		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) 
			{
				System.out.println("SendMessageManager.setChannelLogHandler:\n" + event.getMessage());
			}
		});

		try
		{

			// 4. 创建请求类对象
			// 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			
			// device_type => 1: web 2: pc 3:android 4:ios 5:wp
			request.setDeviceType(device_type); 
			
			request.setChannelId(mChannelID);
			request.setUserId(mUserID);

			request.setMessage(msg);

			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient.pushUnicastMessage(request);

			// 6. 认证推送成功
			System.out.println("认证推送成功  push amount : " + response.getSuccessAmount());

		} 
		catch (ChannelClientException e) 
		{
			// 处理客户端错误异常
			e.printStackTrace();
			
			rValue=e.getMessage();
			System.out.println("SendMessageManager.ChannelClientException:\n" + rValue);
			
		} 
		catch (ChannelServerException e) 
		{
			// 处理服务端错误异常
			rValue=String.format("request_id: %d, error_code: %d, error_message: %s",
					e.getRequestId(), e.getErrorCode(), e.getErrorMsg());
			System.out.println("SendMessageManager.ChannelServerException:\n"+rValue);

		}
		
		return rValue;
	}
}
