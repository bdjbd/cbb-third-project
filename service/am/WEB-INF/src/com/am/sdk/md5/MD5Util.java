package com.am.sdk.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



import org.apache.commons.lang3.StringUtils;

/**
 * MD5加密
 * @author xinalin
 *2016年10月12日
 */


public class MD5Util {

	private volatile static MD5Util singleton;  
	private MD5Util (){}
	public static MD5Util getSingleton() {  
		if (singleton == null) {
			synchronized (MD5Util.class) {
				if (singleton == null) {  
					singleton = new MD5Util();  
				}  
			}  
		}  
		return singleton;  
	}  
	
	
	/**
	* 1.对文本进行32位小写MD5加密
	* @param plainText 要进行加密的文本
	* @return 加密后的内容
	*/
	public String textToMD5L32(String plainText){
	String result = null;
	//首先判断是否为空
	if(StringUtils.isBlank(plainText)){
	return null;
	}
	try{
	//首先进行实例化和初始化
	MessageDigest md = MessageDigest.getInstance("MD5");
	//得到一个操作系统默认的字节编码格式的字节数组
	byte[] btInput = plainText.getBytes();
	//对得到的字节数组进行处理
	md.update(btInput);
	//进行哈希计算并返回结果
	byte[] btResult = md.digest();
	//进行哈希计算后得到的数据的长度
	StringBuffer sb = new StringBuffer();
	for(byte b : btResult){
	int bt = b&0xff;
	if(bt<16){
	sb.append(0);
	}
	sb.append(Integer.toHexString(bt));
	}
	result = sb.toString();
	}catch(NoSuchAlgorithmException e){
	e.printStackTrace();
	}
	return result;
	}

	/**
	* 2.对文本进行32位MD5大写加密
	* @param plainText 要进行加密的文本
	* @return 加密后的内容
	*/
	public String textToMD5U32(String plainText){
	if(StringUtils.isBlank(plainText)){
	return null;
	}
	String result = textToMD5L32(plainText);
	return result.toUpperCase();
	}
	
	public static void main(String[] args) {
		String base = "MTgxODEwOTYxODkyMDJjYjk2MmFjNTkwNzViOTY0YjA3MTUyZDIzNGI3MGlQaG9uZemZleilv+ecgeilv+WuieW4guaWsOWfjuWMuueOr+WMl+i3rzg1LTLlj7czNC4yODQxMDA4MjkzNDExMDguOTU5NDg2NzcxODk2OEM2MjEzNURCODY1MzRGODdBNDJCMzI0Rjk1RTU3";
		
		System.out.println(MD5Util.getSingleton().textToMD5L32(base));
		
	}
	
}
