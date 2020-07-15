package com.am.app_plugins.Credit_card_account.service;

import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 现金账户向信誉卡账户转账后的处理类
 * @author xiechao
 * 时间：2016年11月16日14:02:26
 * 
 * 
 * 信誉卡
 * 充值
 *1. 现金账户转账至信誉卡 
 *回调操作：
 *更新 启动资金累加  更新 授信额度
 *更新 可用额度字段
 * 还款
 * 判断 已用额度字段是否>0 如果大于零 则可以操作还款
 * 回调操作
 * js 判断输入的金额是否大于已用额度
 * 更新 已用额度字段 和可用额度字段调用转账方法
 * 
 * 取现
 * 
 * 
 */
public class StartUpCapital {

public String callBackExec(String id, String business, DB db, String type)
	throws Exception 
	{
		MapList list =  getAccountInfos(id,db);
		
		//根据交易记录id查询账号信息
		if(!Checker.isEmpty(list))
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
			//捐助者金额
			Long consumer_donor_amount = list.getRow(0).getLong("consumer_donor_amount", 0);
			//启用资金
			Long starting_amount = list.getRow(0).getLong("starting_amount", 0);
			//项目扶持
			Long project_donor_amount = list.getRow(0).getLong("project_donor_amount", 0);
			//现金账户转账后启动资金的比例
			int top_up_rate = Integer.parseInt(Var.get("top_up_rate"));
			//项目帮扶启动资金比例
			double project_donor_free = Double.parseDouble(Var.get("project_donor_free"));
			
			//计算后的可用取现额度
			double available_cash_d = 0;
			
			String sqls = "select * from MALL_BASE_SETTING  WHERE menu_code='credit_card_withdraw' order by bname asc";
			
			MapList mlist = db.query(sqls);
			
			VirementManager vir = new VirementManager();
			
			//更新启动金额			
			starting_amount = starting_amount +money;
			
			//更新授信额度
			credit_amount = starting_amount*top_up_rate + consumer_donor_amount;
			
			//更新项目扶持字段
			project_donor_amount = VirementManager.changeY2F(starting_amount*project_donor_free) ;
			
			if(project_donor_amount>0)
			{
				project_donor_amount = project_donor_amount/100;
			}
			
			//更新可用额度
			available_amount = credit_amount - used_amount;
			
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
						float f = mlist.getRow(i).getFloat("bname", 0);
						if(withdraw_num == f)
						{
							available_cash_d = (credit_amount/100)*(mlist.getRow(i).getFloat("bvalue", 0)/100);
						}
					}
					
				}
			}
			
			String sql = "update mall_account_info set "
					   + " available_amount = '"+available_amount+"'"
					   + ",balance = '"+available_amount+"'"
					   + ",starting_amount = '"+starting_amount+"'"
					   + ",credit_amount = '"+credit_amount+"'"
					   + ",project_donor_amount = '"+project_donor_amount+"'"
					   + ",available_cash_amount = '"+VirementManager.changeY2F(String.valueOf(available_cash_d))+"'";
			
			sql += " where id = '"+list.getRow(0).get("id")+"'";
			
			db.execute(sql);
		}
		return null;
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
					+ " left join am_member as am on am.id = mtd.member_id"
					+ " where mai.id = '"+list.getRow(0).get("account_id")+"'"
							+ " and  msac.id = '"+list.getRow(0).get("sa_class_id")+"'"
							+ " and  mtd.id = '"+id+"'";
			mlist = db.query(sql);
		}
		return mlist;
	}
}

