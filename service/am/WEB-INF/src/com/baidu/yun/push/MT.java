package com.baidu.yun.push;

import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushUnicastMessageRequest;
import com.baidu.yun.channel.model.PushUnicastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;

/**
 * 移动终端
 * 
 * @author baojie
 * 
 */
public class MT {
	private String apiKey;
	private String secretKey;
	private Long ChannelId;
	private String UserId;

	public MT() {
//		this.apiKey = "MKHj01e3eVCgbafrCmfjif2u";
//		this.secretKey = "pcutRvU7GPLhpbmuVE3xXagSuGY8Fp8K";
		this.apiKey = "iywlFX8lwKAMv6qargj4pEHf";
		this.secretKey = "eWCpx03bnHfzRE2ycHf7qXA974GY0MiG";
//		this.ChannelId = 4184634293659727485L;
//		this.UserId = "688095840631898343";
	}

	public void push(String message) throws ChannelClientException, ChannelServerException {
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});
//		try {

			// 4. 创建请求类对象
			// 手机端的ChannelId， 手机端的UserId， 先用1111111111111代替，用户需替换为自己的
			PushUnicastMessageRequest request = new PushUnicastMessageRequest();
			request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
										// 4:ios 5:wp
			request.setChannelId(this.ChannelId);
			request.setUserId(this.UserId);

			request.setMessage(message);

			// 5. 调用pushMessage接口
			PushUnicastMessageResponse response = channelClient
					.pushUnicastMessage(request);

			// 6. 认证推送成功
			System.out.println("push amount : " + response.getSuccessAmount());

//		} catch (ChannelClientException e) {
//			// 处理客户端错误异常
//			e.printStackTrace();
//		} catch (ChannelServerException e) {
//			// 处理服务端错误异常
//			System.out.println(String.format(
//					"request_id: %d, error_code: %d, error_message: %s",
//					e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
//		}
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Long getChannelId() {
		return ChannelId;
	}

	public void setChannelId(Long channelId) {
		ChannelId = channelId;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

}
