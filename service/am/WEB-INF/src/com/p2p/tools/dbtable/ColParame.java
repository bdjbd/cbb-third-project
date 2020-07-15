package com.p2p.tools.dbtable;

import java.util.HashMap;

public class ColParame 
{
	private HashMap<String, String> mParameList = new HashMap<String, String>();
	
	public void add(String name,String value)
	{
		mParameList.put(name, value);
	}
	
	public String getValueOfName(String name)
	{
		return mParameList.get(name);
	}
}
