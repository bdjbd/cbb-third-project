package com.am.frame.upload.fileProcess.impl;

import java.util.Map;

import com.am.frame.upload.UploadDataItem;
import com.am.frame.upload.fileProcess.FilePathRule;

/**
 * @author YueBin
 * @create 2016年5月31日
 * @version 
 * 说明:<br />
 * 平台保存文件路径规则
 */
public class FSDefaultFileSavaPathRule implements FilePathRule  {

	@Override
	public String getFilePath() {
		return "";
	}

	@Override
	public String getFilePath(Map<String, UploadDataItem> uploadData) {
		return "";
	}

}
