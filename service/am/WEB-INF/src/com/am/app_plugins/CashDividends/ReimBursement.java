package com.am.app_plugins.CashDividends;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 组织机构还款回调
 * @author xiechao
 * 时间：2016年11月25日10:44:42

 */
public class ReimBursement extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
	logger.info("组织机构还款回调 type:"+type+"\t business:"+business);
	
	JSONObject jso = new JSONObject();
	
	//判断业务是否被处理过
	if(checkProcessBuissnes(id,db))
	{
		jso.put("code", "999");
		jso.put("msg", "业务已被处理过");
		
		logger.info("组织机构还款回调type:999\t msg:业务已被处理过");
		return jso.toString();
	}
	
	MapList accountClassInfo =  getAccountInfos(id,db);
	
	//根据交易记录id查询账号信息
	if(Checker.isEmpty(accountClassInfo))
	{
		jso.put("code", "999");
		jso.put("msg", "账户信息不存在");
		
		logger.info("组织机构还款回调 type:999\t msg:账户信息不存在或交易记录不存在");
		return jso.toString();
		
	}else
	{

		switch (accountClassInfo.getRow(0).get("sa_code")) {
		//当为组织机构借款账户时
		case SystemAccountClass.GROUP_LOAN_ACCOUNT:			
			execCaschAccountOr(accountClassInfo,id,db);
	
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
		sql = "select mai.*,msac.sa_code,mtd.trade_total_money,mtd.member_id,am.phone"
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


//身份股金账户向现金账户转账后向抗分险自救金账户转账的方法
public void execCaschAccountOr(MapList list,String id,DB db) throws Exception
{
	
			
			//组织机构id
			String ordid = list.getRow(0).get("member_id");
			//转账金额
			Long money = Long.parseLong(list.getRow(0).get("trade_total_money"));
			
			//更新还款余额
			String UpdateRepayment_amountSql  = "UPDATE mall_account_info "
					+ " SET repayment_amount = repayment_amount '"+money+"' "
					+ " where id = "
					+ " ( "
					+ " SELECT mai.id "
					+ " FROM mall_account_info as mai "
					+ " LEFT JOIN mall_system_account_class as msac on msac.id = mai.a_class_id "
					+ " WHERE mai.member_orgid_id= '"+ordid+"' and msac.sa_code= 'GROUP_LOAN_ACCOUNT' "
					+ " ) "; 
			db.execute(UpdateRepayment_amountSql);
					
							
}
		
}
