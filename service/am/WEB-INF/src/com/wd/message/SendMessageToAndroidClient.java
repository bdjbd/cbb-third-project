package com.wd.message;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 发送消息公共类
 * */
public class SendMessageToAndroidClient {

	/**
	 * 发送消息给所有android客户端
	 * 
	 * @param java
	 *            .lang.String 用户ID英文逗号分隔
	 */
	public static void sentMessageToAllClient(String userIDs) {
		// 向服务端发送key = value对
		StringBuffer sb = new StringBuffer();
		sb.append("title=");
		sb.append("&message=" + userIDs);
		sb.append("&uri=");
		sentMessageToAndroidClient(sb.toString());
	}

	/**
	 * android推送服务
	 * 
	 * @param java
	 *            .lang.String 消息发送字符串
	 */
	private static void sentMessageToAndroidClient(String sentStr) {
		try {
			// 服务地址
			URL url = new URL(
					"http://127.0.0.1:7070/notification.do?action=send");
			// 设定连接的相关参数
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream(), "UTF-8");
			out.write(sentStr);
			out.flush();
			out.close();
			// 获取服务端的反馈,不用做任何处理
			connection.getInputStream();
			connection.getInputStream();
		} catch (Exception e) {
			System.out.print("android消息通知发送失败！");
			e.printStackTrace();
		}
	}
}
