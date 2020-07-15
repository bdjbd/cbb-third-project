package com.am.frame.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 *	读取properties文件的工具类
 * @author wz
 * 2016年3月23日15:57:38
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
			in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("resource.properties"),"UTF-8");
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
		String value=p.getProperty(key);
		return value;
	}
}