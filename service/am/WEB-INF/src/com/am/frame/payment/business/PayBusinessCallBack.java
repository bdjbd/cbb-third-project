package com.am.frame.payment.business;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.rechange.Rechange;
import com.fastunit.jdbc.DB;

/**
 * 处理充值回调 执行转账
 * @author mac
 *
 */
public class PayBusinessCallBack implements IBusinessCallBack{

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
			
			//如果入账账号为现金账户
			if(SystemAccountClass.CREDIT_MARGIN_ACCOUNT.equals(in_account_code)){
				
				//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
				TaskEngine taskEngine=TaskEngine.getInstance();
				RunTaskParams params=new RunTaskParams();
				params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
				
				//任务触发点，信用保证金账号充值
				params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,
						MemberAuthorityBadgeTask.CREDIT_MARGIN_RECHANGE);
				//信用保证金账号 充值金额，单位元
				params.pushParam(MemberAuthorityBadgeTask.CREDIT_MARGIN_RECHANGE, paymoney);
				
				params.setMemberId(memberid);
				taskEngine.executTask(params);
			}
			
			
		}
		
		return obj.toString();
	}

}
