package com.wd.globalsearch.sstl;

import org.json.JSONArray;

import com.wd.globalsearch.IGlobalSearch;
import com.wd.tools.CommonUtil;

/**
 * 按照关键字搜索指定的视频
 * */
public class GlobalSearchVideo implements IGlobalSearch {

	/**
	 * 按照关键字搜索指定的视频
	 * @param keyName 关键字
	 * @return 文档信息集合，包括：文档名称，路径，后缀名
	 * */
	@Override
	public JSONArray search(String keyName) {
		String extension="'mp4','flv','mov','mpv','3gp'";
		JSONArray js=CommonUtil.GlobalSearchFilesInfo(keyName, extension);
		return js;
	}

}
