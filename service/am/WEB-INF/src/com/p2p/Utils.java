package com.p2p;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.p2p.base.MakeCertPirc;

/**
 * Author: Mike
 * 2014年7月15日
 * 说明：
 *
 **/
public class Utils {

	private static String resultStr="{\"errcode\":%s, \"errmsg\": \"%s\"}";
	private static String smtphost="";
	private static int port=456;
	private static String emailAddr="";
	private static String emailPassword="";
	
	
	public static final JsonObject ResultSetToJsonObject(ResultSet rs) {
        JsonObject element = null;
        JsonArray ja = new JsonArray();
        JsonObject jo = new JsonObject();
        ResultSetMetaData rsmd = null;
        String columnName, columnValue = null;
        try {
            rsmd = rs.getMetaData();
            while (rs.next()) {
                element = new JsonObject();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i + 1);
                    columnValue = rs.getString(columnName);
                    element.addProperty(columnName, columnValue);
                }
                ja.add(element);
            }
            jo.add("result", ja);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jo;
    }
	
	
	/**
	 * 输出执行结构
	 * @param ercode 错误代码
	 * @param ermsg 错误信息
	 * @return {"errcode":错误代码, "errmsg": "错误信息"}
	 */
	public static String executeResult(int ercode,String ermsg){
		@SuppressWarnings("resource")
		Formatter f = new Formatter();
		f.format(resultStr,ercode,ermsg);
		System.out.println(f.toString());
		return f.toString();
	}
	
	/**
	 * 发送邮件
	 * @param recvEmail 收件箱地址
	 * @param title  标题
	 * @param msg 正文
	 */
	
	public static void  sendEmailTest(String recvEmail,String title,String msg) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(smtphost);
			email.setSmtpPort(456);
			email.setAuthenticator(new DefaultAuthenticator(
					emailAddr.split("@")[0],emailPassword));
//			email.setSSLOnConnect(true);
			email.setFrom(emailAddr);
			email.setSubject(title);
			email.setMsg(msg);
			email.addTo(recvEmail);
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取一个随机的字符串
	 * @param length 随机字符串长度
	 * @return  获取的随机字符串
	 */
	public static String getRandomStr(int length){
		if(length<1)return null;
		StringBuffer sb=new StringBuffer();
		Random rand=new Random();
		for(int i=0;i<length;i++){
			sb.append(MakeCertPirc.mapTable[rand.nextInt(MakeCertPirc.mapTable.length)]);
		}
		
		return sb.toString();
	}
	
	
	static{
		try {
			Properties prop=new Properties();
			InputStream in=Utils.class.getClassLoader().getResourceAsStream("email.properties");
			prop.load(in);
			smtphost=(String)prop.get("smtphost");
			port=Integer.parseInt(prop.get("port").toString());
			emailAddr=(String)prop.get("email");
			emailPassword=(String)prop.get("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEmail(){
		sendEmailTest("anglezhang_116@126.com", "测试消息", "测试消息");
		Utils.sendEmailTest("anglezhang_116@126.com",
				"找回密码",
				"这是您在电商服务平台的密码："+"32121321321"+"，请您登录后重新修改密码。");
	}
	
	
	 /** 
     * MD5 加密 
     */  
    public static String getMD5Str(String str) {  
        MessageDigest messageDigest = null;  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.reset();  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }  
}