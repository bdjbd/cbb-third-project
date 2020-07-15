package com.wd.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.User;
import com.fastunit.user.UserFactory;

/**
 * 定义内置全局变量
 * */
public class GlobalParamList {

	private JSONObject params=null;
	
	public GlobalParamList(String userid)
	{
		params=new JSONObject();
		User user=null;
		try {
			user = UserFactory.getUser(userid);
			params.put("userId",user.getId());
			params.put("orgId",user.getOrgId());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public String getValue(String key)
	{
		String value="";
		if(key!=null || !key.equalsIgnoreCase(""))
		{
			try {
				value =params.getString(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return value;
	}
	
	public String getValue(int index)
	{
		if(index==0)
		{
			return getValue("orgId");
		}
		else if(index==1)
		{
			return getValue("userId");
		}
		else
		{
			return "";
		}
	}
	
	public String getKey(int index)
	{
		String values=params.toString().replace("\"","");
		values=values.replace("{","");
		values=values.replace("}","");
		return values.split(",")[index].split(":")[0].trim();
	}
	
	/**
	 * 全局变量的个数
	 * */
	public int length()
	{
		return params.length();
	}
	
}
