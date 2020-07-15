package com.am.frame.volumteers.callback;

import org.jgroups.util.UUID;
import org.json.JSONObject;

import com.am.frame.task.instance.MemberInfoPerfectionTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;

/**
 * 购买志愿者服务账号等级操作业务回调
 * @author mac
 *
 */
public class PayVolumteersBailCallBack implements IBusinessCallBack 
{

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		
		JSONObject businessJso = new JSONObject(business);
		
		//支付id
		String payment_id = businessJso.getString("payment_id");
		
		//入账账号
		String in_account_code = businessJso.getString("in_account_code");
		
		//会员id
		String memberid = businessJso.getString("memberid");
		
		//订单id
		String orders = businessJso.getString("orders");
		
		//支付金额
		String paymoney = businessJso.getString("paymoney");
		
		VirementManager vir = new VirementManager();
		
		String sql ="insert into volunteers_pay_record (id,member_id,create_time,mall_trade_id,pay_amount,status) "
				+ "values('"+UUID.randomUUID()+"','"+memberid+"','now()','"+payment_id+"','"+VirementManager.changeY2F(paymoney)+"','0')";
		
		int i = db.execute(sql);
		
		JSONObject resoult = new JSONObject();
		
		resoult.put("code", "0");
		
		// 获取志愿者账号提现资格任务 START
		TaskEngine taskEngine = TaskEngine.getInstance();
		RunTaskParams params = new RunTaskParams();
		params = new RunTaskParams();
		params.setTaskCode(MemberInfoPerfectionTask.TASK_ECODE);
		params.pushParam(MemberInfoPerfectionTask.INCREASE_PAYMENT_OF_DEPOSIT,paymoney);

		params.setMemberId(memberid);
		taskEngine.executTask(params);
		// 获取志愿者账号提现资格任务 END
		
		return resoult.toString();
	}

}
