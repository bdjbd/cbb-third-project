package com.wd.comp;

import com.fastunit.Path;

public class Constant {
	/**
	 * 操作成功
	 * */
	public static final String SUCCESS = "success";
	/**
	 * 操作失败
	 * */
	public static final String FAILED = "failed";
	/**
	 * fastunit上传文件路径
	 * */
	public static final String UPLOADFILES = "files";
	/**
	 * 指定socketserver监听的端口
	 * */
	public static final int MESSAGE_PORT = 10086;
	/**
	 * 编码格式
	 * */
	public static final String MESSAGE_ENCODING = "UTF-8";
	/**
	 * 客户端登陆用户id的前缀标识
	 * */
	public static final String MESSAGE_USERID_MARK = "@";
	/**
	 * 新消息接收者的前缀标识
	 * */
	public static final String MESSAGE_RECEIVER_MARK = "#";
	/**
	 * excel临时文件夹
	 * */
	public static final String EXCEL_TEMP_FILEPATH = Path.getHomePath()
			+ "/excelTemp/";
	/**
	 * excel模板路径
	 * */
	public static final String EXCEL_TEMPLATE_FILEPATH = Path.getRootPath()
			+ "/WEB-INF/src/excelTemplate/";
	
	/**
	 * 班组圈子列表每页行数
	 * */
	public static final int PAGE_SIZE=10;
}
