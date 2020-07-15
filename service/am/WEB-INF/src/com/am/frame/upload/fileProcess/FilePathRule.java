package com.am.frame.upload.fileProcess;

import java.util.Map;

import com.am.frame.upload.UploadDataItem;

/**
 * @author YueBin
 * @create 2016年2月26日
 * @version 
 * 说明:<br />
 * 获取图片路径 接口
 */
public interface FilePathRule {
	
	/**
	 * 获取保存文件路径 不包含更目录
	 * @return 保存文件路径
	 */
	public String getFilePath();
	
	/**
	 * 获取保存文件路径 不包含更目录
	 * @return 保存文件路径
	 */
	public String getFilePath(Map<String, UploadDataItem> uploadData);
	
}

