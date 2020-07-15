package com.p2p.tools.sms.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import com.p2p.tools.sms.ISmsResultManager;

/**
 * @author YueBin
 * @create 2016年5月20日
 * @version 
 * 说明:<br />
 * 云片网短息验证处理类
 * http://www.yunpian.com/api2.0/demo.html#top 
 */
public class YunPianSmsResultManager implements ISmsResultManager {
	private HashMap ResultList=new HashMap();
	private String mMessageString="";
	
	public YunPianSmsResultManager(String resultMsg,String str1,String str2)
	{
		String[] msgList=resultMsg.split(str1);
		
		String[] tMsg;
		
		mMessageString=resultMsg;
		
		for(String stemp:msgList)
		{  
			tMsg=stemp.split(str2);
			
			System.out.println("[" + str2 + "]tMsg.length=" + tMsg.length);
			
			if(tMsg.length>1)
				ResultList.put(tMsg[0], tMsg[1]);
		} 
	}
	
	
	@Override
	public void init(String Message) {
		try {
			String  resultMsg=URLDecoder.decode(Message,"UTF-8");
			String str1 = "&";
			String str2 = "=";

			
			String[] msgList=resultMsg.split(str1);
			
			String[] tMsg;
			
			mMessageString=resultMsg;
			
			for(String stemp:msgList)
			{  
				tMsg=stemp.split(str2);
				
				System.out.println("[" + str2 + "]tMsg.length=" + tMsg.length);
				
				if(tMsg.length>1)
					ResultList.put(tMsg[0], tMsg[1]);
			} 

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getMessageString()
	{
		return mMessageString;
	}
	
	@Override
	public String getValue(String name)
	{
		String rValue=ResultList.get(name).toString();
		
		return rValue;
	}

	
	
}
