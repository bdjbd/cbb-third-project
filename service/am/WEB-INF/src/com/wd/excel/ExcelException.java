package com.wd.excel;

/**
 * 此类是用来导入到处excel时，出现异常使用，可用过 throw new ExcelException("异常原因"),
 * 通过抛出此异常，可终止程序，并将错误信息提示到前台
 * 
 * @author 张小龙
 * */
@SuppressWarnings("serial")
public class ExcelException extends Exception {
	public String message;

	public ExcelException(String s) {
		super(s);
		message = s;
	}
}
