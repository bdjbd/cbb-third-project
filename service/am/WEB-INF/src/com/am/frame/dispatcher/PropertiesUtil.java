package com.am.frame.dispatcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 *读取properties文件的工具类
 * @author xianlin
 * 2015-12-02
 */
public class PropertiesUtil {
	
	private static Properties p = new Properties();
	private static PropertiesUtil popUtils;
	/**
	 * 读取properties配置文件信息
	 */
	private PropertiesUtil(){
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("config.properties"),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static PropertiesUtil getPropertiesUtil(){
		if(popUtils==null){
			popUtils=new PropertiesUtil();
		}
		return popUtils;
	}
	
	public static String PropertiesUtil(String value)
	{
		
		return p.getProperty(value);
	}
	
	/**
	 * 根据key得到value的值
	 */
	public String getValue(String key)
	{
		String Value="";
		Value= new String(p.getProperty(key)).toString();
//		try {
//			Value= new String(p.getProperty(key).getBytes("iso-8859-1"),"utf-8").toString();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return Value;
	}
	
	
	/**
	 * 编码返回
	 */
//	public static String getValueToEncode(String key)
//	{
//		String Value="";
//		try {
//			Value= new String(p.getProperty(key).getBytes("gbk2312"),"utf-8").toString();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return Value;
//	}
}