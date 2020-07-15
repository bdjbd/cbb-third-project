package com.wd.globalsearch;

import org.json.JSONArray;

/**
 * 请求百度，搜索相关信息
 * */
public class GlobalSearchBaidu implements IGlobalSearch {

	 /**
	 * 请求百度，搜索相关信息
	 * @param keyName 搜索关键字
	 * @return 百度搜索结果的web地址
	 * */
	@Override
	public JSONArray search(String keyName) {
		// TODO Auto-generated method stub
		JSONArray js=new JSONArray();
		if(keyName!=null && !keyName.equalsIgnoreCase(""))
		{
			js.put("http://www.baidu.com.cn/s?wd="+keyName);
		}
		return js;
	}

}
