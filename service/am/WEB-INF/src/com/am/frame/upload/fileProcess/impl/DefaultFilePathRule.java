package com.am.frame.upload.fileProcess.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.am.frame.upload.UploadDataItem;
import com.am.frame.upload.fileProcess.FilePathRule;

/**
 * @author YueBin
 * @create 2016年2月26日
 * @version 
 * 说明:<br />
 * 默认获取路径
 */
public class DefaultFilePathRule implements FilePathRule {

	@Override
	public String getFilePath() {
		String filePath="";
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd");
		String str=sdf.format(new Date());
		
		String[] strs=str.split("-");
		
		for(String st:strs){
			filePath+=File.separator+st;
		}
		
		filePath=filePath+File.separator;
		
		return filePath;
	}

	
	
	@Override
	public String getFilePath(Map<String, UploadDataItem> uploadData) {
		return null;
	}

}
