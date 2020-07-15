package com.weChat.exception;
/**
 *   说明:
 * 	 api功能未授权
 *   @creator	岳斌
 *   @create 	Feb 19, 2014 
 *   @version	$Id
 */
public class IsValidateException extends Exception {
	private static final long serialVersionUID = 1L;
	public IsValidateException(String msg){
		super(msg);
	}
}
