package com.wd.globalsearch.sstl;

import org.json.JSONArray;

import com.wd.globalsearch.IGlobalSearch;
import com.wd.tools.CommonUtil;

/**
 * 神硕铁路项目知识检索类-文档
 * */
public class GlobalSearchFiles implements IGlobalSearch {

	/**
	 * 按照关键字搜索指定的文档
	 * @param keyName 关键字
	 * @return 文档信息集合，包括：文档名称，路径，后缀名
	 * */
	@Override
	public JSONArray search(String keyName) {
		// TODO Auto-generated method stub
		String extension="'doc','docx','xls','xlsx','ppt','pptx','pdf'";
		JSONArray js=CommonUtil.GlobalSearchFilesInfo(keyName, extension);
		return js;
	}

}
