package com.am.frame.upload.fileProcess;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.upload.UploadDataItem;

/**
 * @author YueBin
 * @create 2016年2月15日
 * @version 
 * 说明:<br />
 * 上传文件处理器
 */
public interface UploadFileProcess {

	/**
	 * 文件上传处理，可以问为后置和前置两种
	 * @param file  前置为上传之前的临时文件，后置为处理后的文件
	 * @param scontext ServletContext
	 * @param reques  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param savePath 文件保存路径
	 * @return 字符串
	 */
	public String process(File file,ServletContext scontext,Map<String,String> reques, HttpServletResponse response,String savePath );
	
	
	/**
	 * 文件处理
	 * @param scontext ServletContext
	 * @param uploadDatas 上传数据集合
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 处理结果
	 */
	public String process(ServletContext scontext,Map<String,UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 文件处理
	 * @param scontext ServletContext
	 * @param uploadDatas 上传数据集合
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 处理结果
	 */
	public String process(ServletContext scontext,Map<String,UploadDataItem> uploadDatas,
			HttpServletRequest request, HttpServletResponse response,FileProcessManager fm);
	
}
