package com.am.app_plugins.Credit_card_account.callback;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

//信誉卡提现操作  转账至消费者账户
public class CreditCardAccountWidthCallBack extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		logger.info("信誉卡提现操作  转账至消费者账户 type:"+type+"\t business:"+business);
		
		JSONObject jso = new JSONObject();
		
		//判断业务是否被处理过
		if(checkProcessBuissnes(id,db))
		{
			jso.put("code", "999");
			jso.put("msg", "业务已被处理过");
			
			logger.info("信誉卡提现操作  转账至消费者账户 type:999\t msg:业务已被处理过");
			return jso.toString();
		}
		
		MapList accountClassInfo =  getAccountInfos(id,db);
		
		//根据交易记录id查询账号信息
		if(Checker.isEmpty(accountClassInfo))
		{
			jso.put("code", "999");
			jso.put("msg", "账户信息不存在");
			
			logger.info("信誉卡提现操作  转账至消费者账户 type:999\t msg:账户信息不存在或交易记录不存在");
			return jso.toString();
			
		}else
		{
			
			switch (accountClassInfo.getRow(0).get("sa_code")) {
			//当为消费账户时 处理
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
		Long used_in_cash = list.getRow(0).getLong("used_in_cash", 0)+money;
		//取现次数
		int withdraw_num = list.getRow(0).getInt("withdraw_num",0);
		//可用取现额度
		Long available_cash_amount = list.getRow(0).getLong("available_cash_amount", 0);
		//授信额度
		Long credit_amount = list.getRow(0).getLong("credit_amount", 0);
		//获取还款日期
		String repayment_date = list.getRow(0).get("repayment_date");
		
		
		//计算后的可用取现额度
		double available_cash_d = 0;
		
		String sqls = "select * from MALL_BASE_SETTING  WHERE menu_code='credit_card_withdraw' order by bname asc";
		
		MapList mlist = db.query(sqls);
		
		VirementManager vir = new VirementManager();
		
		used_amount = used_amount+money;
		
		withdraw_num = withdraw_num+1;
		
		if(!Checker.isEmpty(mlist))
		{
			if(withdraw_num>mlist.getRow(mlist.size()-1).getInt("bname", 0))
			{
				//先将数据库中的分转换为元在乘以每次取现的比例
				available_cash_d = (credit_amount/100)*(mlist.getRow(mlist.size()-1).getFloat("bvalue", 0)/100);
			}else
			{
				for (int i = 0; i < mlist.size(); i++) 
				{
				
					if(withdraw_num == mlist.getRow(i).getFloat("bname", 0))
					{
						available_cash_d = (credit_amount/100)*(mlist.getRow(i).getFloat("bvalue", 0)/100);
					}
				}
				
			}
		}
		
		String sql = "update mall_account_info set used_amount = '"+used_amount+"'"
				   + ",available_amount = '"+available_amount+"'"
				   + ",balance = '"+available_amount+"'"
				   + ",used_in_cash = '"+used_in_cash+"'"
				   + ",withdraw_num = '"+withdraw_num+"'"
				   + ",available_cash_amount = '"+VirementManager.changeY2F(String.valueOf(available_cash_d))+"' ";
		
		if(Checker.isEmpty(repayment_date))
		{
			sql += ",repayment_date=(SELECT to_timestamp(extract(year from (SELECT now() + interval '1 month') )||'-'||extract(month from (SELECT now() + interval '1 month') )||'-01','yyyy-MM-dd HH24:MI:SS'))";
		}
		
		sql += " where id = '"+list.getRow(0).get("id")+"'";
		
		db.execute(sql);
		
	}
	
}
