package com.p2p.tools.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendSmsOfUMS implements ISendSmsMessage 
{
	public static final String GET_URL = "http://sx.ums86.com:8899/sms/Api/Send.do";
	
	/*http://sx.ums86.com:8899/sms/Api/Send.do
	 * ?SpCode=201445
	 * &LoginName=sx_ylj
	 * &Password=rj9008
	 * &MessageContent=您的验证码112345
	 * &UserNumber=15029078585
	 * &SerialNumber=
	 * &ScheduleTime=
	 * &f=1
	 * */
	
	@Override
	public String send(String content,String phone) 
	{
		
		
		String rValue="";
        
		try 
		{
			//String param=new String(content.getBytes("UTF-8"),"GB2312");
			//String param=content;
			
			// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
			String sURL = GET_URL + "?SpCode=214279&LoginName=sx_ylj&Password=rj9221&SerialNumber=&ScheduleTime=&f=1"
					+ "&MessageContent=" + content //URLEncoder.encode(content, "GB2312")
					+ "&UserNumber=" + phone;
			
			//rValue=sURL + "<br>";

	        URL getUrl = new URL(sURL);
	        
	        // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
	        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
	        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
	        
	        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
	        // 服务器
	        connection.connect();
	        
	        // 取得输入流，并使用Reader读取
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	        String lines;
	        while ((lines = reader.readLine()) != null) 
	        {
	            rValue+=lines;
	        }

	        reader.close();
	        // 断开连接
	        connection.disconnect();

		} 
		catch (Exception e) 
		{
			rValue+="<br>" + e.getMessage();
			e.printStackTrace();
		}
		
		return rValue;
	}
	
	
}
