package com.wd.globalsearch;

import org.json.JSONArray;

/**
 * 请求谷歌，搜索相关信息
 * */
public class GlobalSearchGoogle implements IGlobalSearch {

	/**
	 * 请求谷歌，搜索相关信息
	 * @param keyName 搜索关键字
	 * @return 谷歌搜索结果的web地址
	 * */
	@Override
	public JSONArray search(String keyName) {
		// TODO Auto-generated method stub
		JSONArray js=new JSONArray();
		if(keyName!=null && !keyName.equalsIgnoreCase(""))
		{
			js.put("https://www.google.com.hk/search?hl=zh-CN&q="+keyName);
		}
		return js;
	}

}
