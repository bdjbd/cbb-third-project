package com.am.frame.upload.fileProcess.impl;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.upload.UploadDataItem;
import com.am.frame.upload.fileProcess.FileProcessManager;
import com.am.frame.upload.fileProcess.UploadFileProcess;

/**
 * @author YueBin
 * @create 2016年2月15日
 * @version 
 * 说明:<br />
 * 后面文件后置处理器
 */
public class DefaultAfterFileProcess implements UploadFileProcess {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String process(File file, ServletContext scontext,
			Map<String,String> reques, HttpServletResponse response,
			String savePath) {
		JSONObject result=new JSONObject();
		
		logger.info(result.toString());
		
		return result.toString();
	}

	
	@Override
	public String process(ServletContext scontext,
			Map<String, UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response) {
		JSONObject result=new JSONObject();
		
		logger.info(result.toString());
		return result.toString();
		
	}


	@Override
	public String process(ServletContext scontext,
			Map<String, UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response,
			FileProcessManager fm) {
		
		return null;
	}

}
