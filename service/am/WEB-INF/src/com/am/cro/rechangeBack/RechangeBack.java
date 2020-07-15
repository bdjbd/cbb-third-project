package com.am.cro.rechangeBack;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.am.frame.pay.PayManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.rechange.Rechange;
import com.fastunit.jdbc.DB;

public class RechangeBack implements IBusinessCallBack {
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {

		
		logger.info("处理充值回调 执行转账回调 type:"+type+"\t business:"+business);
		
		JSONObject businessJso = new JSONObject(business);
		
		String payment_id = businessJso.getString("payment_id");
		
		String in_account_code = businessJso.getString("in_account_code");
		
		String memberid = businessJso.getString("memberid");
		
		String orders = businessJso.getString("orders");
		
		String orgcode = businessJso.getString("orgcode");
		
		//充值金额 单位，元
		String paymoney = businessJso.getString("paymoney");
		
		//充值金额元，转换为分
		String str=(Double.parseDouble(paymoney)*100)+"";
		
		Long money = Long.valueOf(str.substring(0,str.indexOf(".")));
		
		Rechange rechange = new Rechange();
		
		JSONObject obj = new JSONObject();
		
		obj = rechange.rechangeExc(money, in_account_code, memberid);
		
		logger.info("处理充值回调 执行转账回调 返回结果:"+obj);
		
		if(obj!=null&&"0".equals(obj.getString("code"))){
		logger.info("充值成功!");
		
		PayManager payManager = new PayManager();
		JSONObject jsonObj = new JSONObject(business);
		String in_remakes = jsonObj.getString("in_remakes");//充值描述
	
		Rechange rechange2 = new Rechange();
		JSONObject jsonObject = rechange2.rechangeExc(Long.parseLong(paymoney)*100,SystemAccountClass.GROUP_CASH_ACCOUNT,orgcode);
		logger.info("汽修厂账户入账结果" + jsonObject);
		
		payManager.updateMemberScore(memberid,Integer.parseInt(paymoney), db);
		payManager.addMemberScore(memberid,Integer.parseInt(paymoney),in_remakes, db);
		payManager.updateMemberLevel(memberid, db);
		}
		return obj.toString();
	}
}
