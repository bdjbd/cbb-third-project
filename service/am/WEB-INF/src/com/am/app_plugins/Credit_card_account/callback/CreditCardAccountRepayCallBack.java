package com.am.app_plugins.Credit_card_account.callback;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

//信誉卡还款操作  现金账户转信誉卡账户
public class CreditCardAccountRepayCallBack extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		logger.info("信誉卡还款操作  现金账户转信誉卡账户 type:"+type+"\t business:"+business);
		
		JSONObject jso = new JSONObject();
		
		//判断业务是否被处理过
		if(checkProcessBuissnes(id,db))
		{
			jso.put("code", "999");
			jso.put("msg", "业务已被处理过");
			
			logger.info("信誉卡还款操作  现金账户转信誉卡账户 type:999\t msg:业务已被处理过");
			return jso.toString();
		}
		
		MapList accountClassInfo =  getAccountInfos(id,db);
		
		//根据交易记录id查询账号信息
		if(Checker.isEmpty(accountClassInfo))
		{
			jso.put("code", "999");
			jso.put("msg", "账户信息不存在");
			
			logger.info("信誉卡还款操作  现金账户转信誉卡账户 type:999\t msg:账户信息不存在或交易记录不存在");
			return jso.toString();
			
		}else
		{
			
			switch (accountClassInfo.getRow(0).get("sa_code")) {
			//更新已用授信额度
			case SystemAccountClass.CREDIT_CARD_ACCOUNT:
				
				execCreditCardAccountOr(accountClassInfo,id,db);
				
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
	
	
	//更新信誉卡操作  更新已用额度 和可用额度
	public void execCreditCardAccountOr(MapList list,String id,DB db) throws Exception
	{
		//操作金额
		Long money = list.getRow(0).getLong("trade_total_money", 0);
		//已用额度
		Long used_amount = list.getRow(0).getLong("used_amount", 0);
		//可用额度
		Long available_amount = list.getRow(0).getLong("available_amount", 0)-money;
		//已用取现额度
		Long used_in_cash = list.getRow(0).getLong("used_in_cash", 0);
		//取现次数
		int withdraw_num = list.getRow(0).getInt("withdraw_num",0);
		//可用取现额度
		Long available_cash_amount = list.getRow(0).getLong("available_cash_amount", 0);
		//授信额度
		Long credit_amount = list.getRow(0).getLong("credit_amount", 0);
		//滞纳金
		Long late_fee = list.getRow(0).getLong("late_fee",0);
		//获取还款日期
		String repayment_date = list.getRow(0).get("repayment_date");
		
		//更新已用额度 已用额度-操作金额
		used_amount = used_amount-money;
		
		//更新可用额度 授信额度-已用额度
		available_amount = credit_amount - used_amount;
		
		
		//可用额度
		if(used_amount+late_fee<=0)
		{
			used_in_cash = 0L;
			late_fee = 0L;
			repayment_date = null;
		}
		
		String sql = "update mall_account_info set used_amount = '"+used_amount+"'"
				    + ",available_amount = '"+available_amount+"',"
				    + "balance = '"+available_amount+"',"
		   			+ "used_in_cash = '"+used_in_cash+"',"
					+ "late_fee = '"+late_fee+"',";
		
		if(used_amount+late_fee<=0)
		{
			sql += "repayment_date = "+repayment_date+"";
		}
					
		sql += " where id = '"+list.getRow(0).get("id")+"'";
		
		db.execute(sql);
		
	}
	
}
