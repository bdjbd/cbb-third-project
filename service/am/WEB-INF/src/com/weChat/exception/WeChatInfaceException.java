package com.weChat.exception;
/**
 *   说明:
 *   	微信接口调用异常，如在平台IO，返回结果解析。
 *   @creator	岳斌
 *   @create 	Nov 22, 2013 
 *   @version	$Id
 */
public class WeChatInfaceException extends Throwable {
	private static final long serialVersionUID = 1L;
	public WeChatInfaceException(){};
	public WeChatInfaceException(String msg){
		super(msg);
	}
	@Override
	public void printStackTrace() {
		System.out.println("微信接口调用失败!");
		super.printStackTrace();
	}

}
