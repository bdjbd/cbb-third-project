package com.wd.globalsearch;

import org.json.JSONArray;
/**
 * 全文搜索接口，所有搜索类都要实现该将接口
 * @author 丁照祥
 * */
public interface IGlobalSearch {

	/**
	 * 全文搜索
	 * @param keyName 搜索关键字
	 * @return 搜索结果
	 * */
	public JSONArray search(String keyName);
}
