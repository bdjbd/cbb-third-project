package com.am.frame.systemAccount.business;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins.Credit_card_account.service.StartUpCapital;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.JudgeBonusRewardTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

//现金账户转账消费账户  分红权
public class CashAccountVirementCallBack extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		logger.info("处理现在账户转消费账户分红权处理回调 执行转账回调 type:"+type+"\t business:"+business);
		
		JSONObject jso = new JSONObject();
		
		//判断业务是否被处理过
		if(checkProcessBuissnes(id,db))
		{
			jso.put("code", "999");
			jso.put("msg", "业务已被处理过");
			
			logger.info("处理现在账户转消费账户分红权处理回调 执行转账回调 type:999\t msg:业务已被处理过");
			return jso.toString();
		}
		
		MapList accountClassInfo =  getAccountInfos(id,db);
		
		//根据交易记录id查询账号信息
		if(Checker.isEmpty(accountClassInfo))
		{
			jso.put("code", "999");
			jso.put("msg", "账户信息不存在");
			
			logger.info("处理现在账户转消费账户分红权处理回调 执行转账回调 type:999\t msg:账户信息不存在或交易记录不存在");
			return jso.toString();
			
		}else
		{
			
			switch (accountClassInfo.getRow(0).get("sa_code")) {
			//当为抗风险自救金时候处理分红权任务
			case SystemAccountClass.CONSUMER_ACCOUNT:
				execCaschAccountOr(accountClassInfo,id,db);
				
				break;
			case SystemAccountClass.CREDIT_CARD_ACCOUNT:
				logger.info("处理现金账户转入信誉卡账户 执行更新信誉卡业务 ");
				StartUpCapital ss = new StartUpCapital();
				ss.callBackExec(id, business, db, type);
				
				break;

			default:
				break;
			}
			
		}
		//更新业务处理
		updateProcessBuissnes(id,db,"1");
		
		jso.put("code", "0");
		jso.put("msg","回调处理完成");
		
		return jso.toString();
	}
	
	//根据交易记录id查询账户信息
	public MapList getAccountInfos (String id,DB db) throws Exception
	{
		String sql = "select * from mall_trade_detail where id = '"+id+"'";
		MapList list = db.query(sql);
		MapList mlist = null;
		if(!Checker.isEmpty(list))
		{
			sql = "select mai.*,msac.sa_code,mtd.trade_total_money,mtd.member_id"
					+ " from  mall_account_info as mai"
					+ " left join mall_system_account_class as msac on msac.id = mai.a_class_id "
					+ " left join mall_trade_detail as mtd on mtd.account_id = mai.id"
					+ " where mai.id = '"+list.getRow(0).get("account_id")+"'"
							+ " and  msac.id = '"+list.getRow(0).get("sa_class_id")+"'"
							+ " and  mtd.id = '"+id+"'";
			mlist = db.query(sql);
		}
		return mlist;
	}
	
	
	//处理现金账户转消费账户 分红权处理 执行分红权处理业务
	public void execCaschAccountOr(MapList list,String id,DB db)
	{
		
		TaskEngine taskEngine=TaskEngine.getInstance();
		RunTaskParams params=new RunTaskParams();
		params.pushParam(JudgeBonusRewardTask.RECHARGEMONEY, list.getRow(0).getLong("trade_total_money", 0));
		params.setTaskCode(JudgeBonusRewardTask.TASK_CODE); //
		params.setMemberId(list.getRow(0).get("member_id"));
		taskEngine.executTask(params);
		
	}
	
}
