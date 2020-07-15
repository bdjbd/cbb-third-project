package com.am.frame.order.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YueBin
 * @create 2016年7月6日
 * @version 
 * 说明:<br />
 *
 一、消费码格式16位数字

时间戳数字 随机数1-999 1234567890123 001

二、构建一个静态类负责产生消费码数字

1、类中构建一个map对象负责对比新生成的消费码是否重复

2、如果重复则重新生成，反之则返回该消费码
 *
 *
 */
public class ConsumerCodeProvider {

	private static Map<String,Boolean> consumerCodeMap=new HashMap<String,Boolean>();
	
	/**
	 * 获取消费码
	 * @return
	 */
	public static String getConsumerCode(){
		long time=System.currentTimeMillis();
		String randomStr=""+Math.random();
		String key=time+""+randomStr.substring(randomStr.indexOf(".")+1,randomStr.indexOf(".")+5);
		while(consumerCodeMap.containsKey(key)){
			time=System.currentTimeMillis();
			randomStr=""+Math.random();
			key=time+""+randomStr.substring(randomStr.indexOf(".")+1,randomStr.indexOf(".")+5);
		}
		
		consumerCodeMap.put(key, true);
		
		return key;
	}

	
	public static void main(String[] args) {
		for(int i=0;i<9999;i++){
			System.out.println(getConsumerCode());
		}
	}
	
	
	static{
		System.err.print("laod ConsumerCodeProvider");
	}
	
}

