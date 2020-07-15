package com.p2p.tools.sms.factory;

import org.slf4j.LoggerFactory;

import com.fastunit.Var;
import com.p2p.tools.sms.ISmsResultManager;

/**
 * 获取短信返回结果处理类
 * */
public class SmsResultManagerFacTory {
	
	/**
	 * 返回结果处理类实例
	 * */
	public static ISmsResultManager getInstance(){
		//取设置变量中的变量
				Var var = new Var();
				String className = Var.get("ISmsResultManager", "");
				LoggerFactory.getLogger(SmsResultManagerFacTory.class).info("ISmsResultManager className=" + className);
				if(className.equals("")){
					className = "com.p2p.tools.sms.SmsResultManager";
				}
				try {
					return (ISmsResultManager) Class.forName(className).newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
	}

}
