package com.p2p.tools.sms;

/**
 * 短信发送返回结果管理接口
 * */
public interface ISmsResultManager {
	/**
	 * 初始化方法
	 * */
	void init(String Message);
	/**
	 * 获取发送短信返回信息
	 * */
	String getMessageString();
	/**
	 * 获取返回信息的对应值
	 * */
	String getValue(String name);

}
