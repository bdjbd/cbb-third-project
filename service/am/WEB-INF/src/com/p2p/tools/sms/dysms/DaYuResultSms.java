package com.p2p.tools.sms.dysms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.p2p.tools.sms.ISmsResultManager;

/**
 * 短信返回结果处理接口
 * @author guorenjie
 *
 */
public class DaYuResultSms implements ISmsResultManager {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void init(String Message) {
		logger.info("发送状态描述"+Message);	
	}

	@Override
	public String getMessageString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
