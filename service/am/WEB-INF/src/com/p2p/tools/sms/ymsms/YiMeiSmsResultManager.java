package com.p2p.tools.sms.ymsms;

import java.util.HashMap;

import com.p2p.tools.sms.ISmsResultManager;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月2日 上午11:10:10
 *@version 说明：
 */
public class YiMeiSmsResultManager implements ISmsResultManager {
	private HashMap ResultList=new HashMap();
	private String mMessageString="";
	
	@Override
	public void init(String Message) {
		
	}

	@Override
	public String getMessageString() {
		
		return mMessageString;
	}

	@Override
	public String getValue(String name) {
		
		String rValue=ResultList.get(name).toString();
		
		return rValue;
	}
}