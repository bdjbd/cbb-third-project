package com.am.frame.systemAccount.business;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.rechange.Rechange;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;

/**
 * 处理充值回调 执行转账
 * @author mac
 *
 */
public class AccountRechangeBusinessCallBack extends AbstraceBusinessCallBack{

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		
		logger.info("处理充值回调 执行转账回调 type:"+type+"\t business:"+business);
		
		JSONObject businessJso = new JSONObject(business);
		
		String payment_id = businessJso.getString("payment_id");
		
		String in_account_code = businessJso.getString("in_account_code");
		
		String memberid = businessJso.getString("memberid");
		
		String orders = businessJso.getString("orders");
		
		String outremakes = businessJso.getString("outremakes");
		
		//充值金额 单位，元
		String paymoney = businessJso.getString("paymoney");

		Rechange rechange = new Rechange();
		
		JSONObject obj = new JSONObject();
		
//		obj = rechange.rechangeExc(money, in_account_code, memberid);
		
		VirementManager vir = new VirementManager();
		
		if(!checkProcessBuissnes(id,db))
		{
			obj = vir.execute(db, "", memberid, "", SystemAccountClass.GROUP_CASH_ACCOUNT, paymoney, "充值操作", "", "", false);
		}else
		{
			obj.put("code", "999");
		}
		
		logger.info("处理充值回调 执行转账回调 返回结果:"+obj);
		
		if(obj!=null&&"0".equals(obj.getString("code"))){
			updateProcessBuissnes(id,db,"1");
			logger.info("充值成功!");
			
			//如果入账账号为现金账户
			
			
		}
		
		return obj.toString();
	}

}
