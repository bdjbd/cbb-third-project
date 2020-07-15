package com.p2p.tools.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.LoggerFactory;
/**
 * 宅可短信发送类
 * */
public class SendSmsOfUMS4ZK implements ISendSmsMessage {

	// 短信发送地址
	private static final String GET_URL = "http://222.73.205.219:8080/sms.aspx";
	
	/*
	 * http://222.73.205.219:8080/sms.aspx ?userid=2865 & account=宅可商贸
	 * &password=abc123 &mobile=手机号码 &content=发送内容 &sendTime= &action= &extno=1
	 */
	@Override
	public String send(String content, String phone) {

		StringBuffer rValue = new StringBuffer();

		try {
			LoggerFactory.getLogger(SendSmsOfUMS4ZK.class).info("发送短信内容:" + content);
			// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
			String sURL = "http://222.73.205.219:8080/sms.aspx?action=send&userid=2865&account=" + 
			        URLEncoder.encode("宅可商贸", "UTF-8") + 
			        "&password=abc123&mobile=" + 
			        phone + "&content=" + 
			        URLEncoder.encode(content, "UTF-8") + 
			        "&sendTime=&extno=";

			// rValue=sURL + "<br>";

			URL getUrl = new URL(sURL);
			// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			HttpURLConnection connection = (HttpURLConnection) getUrl
					.openConnection();
			// 服务器
			connection.connect();

			// 取得输入流，并使用Reader读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String lines;
			while ((lines = reader.readLine()) != null) {
				rValue.append(lines);
			}

			reader.close();
			// 断开连接
			connection.disconnect();

		} catch (Exception e) {
			rValue.append("<br>" + e.getMessage());
			e.printStackTrace();
		}
		LoggerFactory.getLogger(SendSmsOfUMS4ZK.class).info("返回内容:" + rValue);
		return rValue.toString();
	}

}
