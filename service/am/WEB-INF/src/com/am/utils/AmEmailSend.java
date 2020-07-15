package com.am.utils;

import org.slf4j.LoggerFactory;

import com.fastunit.email.EmailSender;
/**
 * @author Mike
 * @create 2015年6月15日
 * @version 
 * 说明:<br />
 * Am 发送邮件类
 */
public class AmEmailSend {

	
	
	public static void send(String to, String from, String fromName, String subject, String msg) throws Exception{
		
		
		LoggerFactory.getLogger("AmEmailSend").info("to："+to);
		LoggerFactory.getLogger("AmEmailSend").info("from："+from);
		LoggerFactory.getLogger("AmEmailSend").info("fromName："+fromName);
		LoggerFactory.getLogger("AmEmailSend").info("subject："+subject);
		LoggerFactory.getLogger("AmEmailSend").info("msg："+msg);
		LoggerFactory.getLogger("AmEmailSend 发送邮件类").info("\tto:"+to+"\tfrom:"+from+"\t"+"fromName:"+fromName+"\tsubject"+subject+"\tmsg:"+msg);
		
		EmailSender.send(to, from,fromName, subject, msg);
		
	}

}
