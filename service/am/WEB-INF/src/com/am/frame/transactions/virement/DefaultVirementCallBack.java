package com.am.frame.transactions.virement;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.callback.IBusinessCallBack;
import com.fastunit.jdbc.DB;


/**
 * 默认转账成功回调类
 * 15968818290
 * @author yuebin
 */
public class DefaultVirementCallBack implements IBusinessCallBack{

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("code","0");
		
		logger.info("business:"+business);
		
		return obj.toString();
	}

}
