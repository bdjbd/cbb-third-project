package com.p2p.tools.sms;

import java.util.HashMap;


//解析：result=0&description=发送成功
public class SmsResultManager implements ISmsResultManager
{
	private HashMap ResultList=new HashMap();
	private String mMessageString="";
	
	public SmsResultManager(String resultMsg,String str1,String str2)
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

	@Override
	public void init(String resultMsg) {
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

	}
}
