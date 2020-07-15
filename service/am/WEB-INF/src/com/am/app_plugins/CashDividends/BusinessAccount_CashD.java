
package com.am.app_plugins.CashDividends;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 经营分红账户向现金账户转账后向抗分险自救金账户转账的回调
 * @author xiechao
 * 时间：2016年11月18日10:13:17

 */
public class BusinessAccount_CashD extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
	logger.info("经营分红账户向现金账户转账后向抗风险自救金账户转账的回调 type:"+type+"\t business:"+business);
	
	JSONObject jso = new JSONObject();
	
	//判断业务是否被处理过
	if(checkProcessBuissnes(id,db))
	{
		jso.put("code", "999");
		jso.put("msg", "业务已被处理过");
		
		logger.info("经营分红账户向现金账户转账后向抗风险自救金账户转账的回调 type:999\t msg:业务已被处理过");
		return jso.toString();
	}
	
	MapList accountClassInfo =  getAccountInfos(id,db);
	
	//根据交易记录id查询账号信息
	if(Checker.isEmpty(accountClassInfo))
	{
		jso.put("code", "999");
		jso.put("msg", "账户信息不存在");
		
		logger.info("经营分红账户向现金账户转账后向抗风险自救金账户转账的回调 type:999\t msg:账户信息不存在或交易记录不存在");
		return jso.toString();
		
	}else
	{

		switch (accountClassInfo.getRow(0).get("sa_code")) {
		//当为现金账户时
		case SystemAccountClass.CASH_ACCOUNT:			
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


//经营分红账户向现金账户转账后向抗分险自救金账户转账的方法
public void execCaschAccountOr(MapList list,String id,DB db) throws Exception
{
	
			
			//会员id
			String member_id = list.getRow(0).get("member_id");
			//转账资金的多少
			Long money = Long.parseLong(list.getRow(0).get("trade_total_money"));
			//经营分红账户向现金账户转账后向抗分险自救金账户转账百分比
			float rate = 0;
			//需要向抗分险自救金账户转账
			String fxmoney = null;
			// 会员名称
			String membername = list.getRow(0).get("phone");
				
		
		
				//获取此笔转账需要向抗分险自救金账户转账的金额（总金额*后台配置的变量10%）
				String sql = "SELECT * FROM avar WHERE vid='BusinessAccount_CashD_rate'";		
				MapList slist = db.query(sql);
						
				if(slist.size()>0){
					rate = Float.parseFloat(slist.getRow(0).get("vvalue"));
					fxmoney = String.valueOf(rate*money/10000);			
				}
				
				//向抗分险自救金账户转账
				VirementManager vir = new VirementManager();
				String iremakers = "亲爱的"+membername+"用户"+"在您从经营分红账户向现金账户转账后，需向抗风险自救金账户转账"+rate+"%。";
				String oremakers = "亲爱的"+membername+"用户"+"在您从经营分红账户向现金账户转账后，需向抗风险自救金账户转账"+rate+"%。";
				if(fxmoney != null){
					
					vir.execute(db, member_id, member_id, SystemAccountClass.CASH_ACCOUNT, SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT, fxmoney, iremakers, oremakers, "", false);
					
				}
				

}		
}


//package com.am.app_plugins.CashDividends;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.lang.Float;
//
//import com.am.frame.systemAccount.SystemAccountClass;
//import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
//import com.am.frame.transactions.callback.IBusinessCallBack;
//import com.am.frame.transactions.virement.VirementManager;
//import com.fastunit.MapList;
//import com.fastunit.jdbc.DB;
//import com.fastunit.jdbc.DBFactory;
//import com.fastunit.jdbc.exception.JDBCException;
//import com.fastunit.util.Checker;
//import com.p2p.service.IWebApiService;
//
///**
// * 经营分红账户向现金账户转账后向抗分险自救金账户转账的回调
// * @author xiechao
// * 时间：2016年11月18日10:13:17
//
// */
//public class BusinessAccount_CashD extends AbstraceBusinessCallBack{
//
//	@Override
//	public String callBackExec(String id, String business, DB db, String type)
//			throws Exception {
//	
//		if("2".equals(type))
//		{
//			return null;
//		}
//
//		//转账表id
//		String mtd_id = id;
//		//会员id
//		String member_id = null;
//		//转账资金的多少
//		int money = 0;
//		//经营分红账户向现金账户转账后向抗分险自救金账户转账百分比
//		float rate = 0;
//		//需要向抗分险自救金账户转账
//		String fxmoney = null;
//		// 会员名称
//		String membername = null;
//		
//		try {
//
//		
//			//查询用户member_id以及启动资金的多少
//			String Transfer_sql = "SELECT * FROM mall_trade_detail WHERE ID = '"+mtd_id+"' ";
//			MapList Transfer = db.query(Transfer_sql);
//			
//			if(Transfer.size()>0){
//				member_id = Transfer.getRow(0).get("member_id");
//				money = Integer.parseInt(Transfer.getRow(0).get("trade_total_money"));
//				
//			}
//			
//			//查找会员名称
//			String m_nSQL = "select * from am_member where id = '" + member_id+ "'";
//			MapList m_nsql = db.query(m_nSQL);
//			if(m_nsql.size()>0){
//				membername = m_nsql.getRow(0).get("phone");			
//			}
//			
//	
//	
//			//获取此笔转账需要向抗分险自救金账户转账的金额（总金额*后台配置的变量10%）
//			String sql = "SELECT * FROM avar WHERE vid='BusinessAccount_CashD_rate'";		
//			MapList list = db.query(sql);
//					
//			if(list.size()>0){
//				rate = Float.parseFloat(list.getRow(0).get("vvalue"));
//				fxmoney = String.valueOf(rate*money/10000);			
//			}
//			
//			//向抗分险自救金账户转账
//			VirementManager vir = new VirementManager();
//			String iremakers = "亲爱的"+membername+"用户"+"在您从经营分红账户向现金账户转账后，需向抗分险自救金账户转账"+rate+"%。";
//			String oremakers = "亲爱的"+membername+"用户"+"在您从经营分红账户向现金账户转账后，需向抗分险自救金账户转账"+rate+"%。";
//
//			if(fxmoney != null){
//				
//				vir.execute(db, member_id, member_id, SystemAccountClass.CASH_ACCOUNT, SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT, fxmoney, iremakers, oremakers , "", false);
//				
//			}
//			
//			
//					
//				
//		} catch (JDBCException e) {
//			e.printStackTrace();
//			
//		}finally{
//			if(db!=null){
//				try {
//					db.close();
//				} catch (JDBCException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		return null;
//	}
//}
