package com.p2p.tools.sms.ymsms;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.p2p.tools.sms.ISendSmsMessage;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月1日 下午4:58:47
 *@version 说明：亿美短信接口
 */
@SuppressWarnings("deprecation")
public class YiMeiSendSmsMessage implements ISendSmsMessage{
	
	//测试地址：http://sdk4http.eucp.b2m.cn/sdkproxy/sendsms.action
	//正式地址：http://sdkhttp.eucp.b2m.cn:8080/sdkproxy/sendsms.action?
	public static String hostUrl = "http://sdkhttp.eucp.b2m.cn:8080/sdkproxy/sendsms.action?";//同步发送即时短信的Http接口地址
	public static String softwareSerialNo = "3SDK-EMY-0130-REXPR";// 软件序列号
	public static String password = "036205";// 密码
	
	@Override
	public String send(String content, String phone) {
		
		String code = doPost(hostUrl,softwareSerialNo,password,phone,content);
		return code;
	}
	
	/**
	 * 发送短信
	 * @param hostUrl
	 * @param cdkey
	 * @param password
	 * @param phone
	 * @param message
	 * @return
	 */
	public String doPost(String hostUrl,String cdkey,String password,String phone,String message){  
        String result = "";  
        HttpPost httpRequst = new HttpPost(hostUrl);//创建HttpPost对象  
          
        List <NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("cdkey", cdkey));  //用户序列号。
        params.add(new BasicNameValuePair("password", password));  //用户密码
        params.add(new BasicNameValuePair("phone", phone)); //手机号码（最多200个），多个用英文逗号(,)隔开
        params.add(new BasicNameValuePair("message", message)); //短信内容（UTF-8编码）（最多500个汉字或1000个纯英文）。
        params.add(new BasicNameValuePair("addserial", ""));//附加号（最长10位，可置空）。
          
        try {  
            httpRequst.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);  
            if(httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);//取出应答字符串  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        } 
        
        return result;  
    }  
}

