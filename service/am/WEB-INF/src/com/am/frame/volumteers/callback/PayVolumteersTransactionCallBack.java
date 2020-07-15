package com.am.frame.volumteers.callback;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 志愿者转账成功后，更新提现额度操作
 * @author mac
 *
 */
public class PayVolumteersTransactionCallBack extends  AbstraceBusinessCallBack  
{

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		JSONObject businessJso = new JSONObject(business);
		
		/**
		 * 志愿者账户向现金账户转账后向抗分险自救金账户转账的回调
		 * @author xiechao
		 * 时间：2016年11月17日18:27:12

		 */
		if("1".equals(type))
		{
			//转账表id
			String mtd_id = id;
			//会员id
			String member_id = null;
			//转账资金的多少
			Long money = 0L;
			//志愿者账户向现金账户转账后向抗分险自救金账户转账百分比
			float rate = 0;
			//需要向抗分险自救金账户转账
			String fxmoney = null;
			// 会员名称
			String membername = null;
			
			try {

			
				//查询用户member_id以及总资金
				String Transfer_sql = "SELECT * FROM mall_trade_detail WHERE ID = '"+mtd_id+"' ";
				MapList Transfer = db.query(Transfer_sql);
				
				if(Transfer.size()>0){
					member_id = Transfer.getRow(0).get("member_id");
					money = Long.parseLong(Transfer.getRow(0).get("trade_total_money"));
					
				}
				
				//查找会员名称
				String m_nSQL = "select * from am_member where id = '" + member_id+ "'";
				MapList m_nsql = db.query(m_nSQL);
				if(m_nsql.size()>0){
					membername = m_nsql.getRow(0).get("membername");			
				}
				
		
		
				//获取此笔转账需要向抗分险自救金账户转账的金额（总金额*后台配置的变量10%）
				String sql = "SELECT * FROM avar WHERE vid='VolunteersAccount_CashD_rate'";		
				MapList list = db.query(sql);
						
				if(list.size()>0){
					rate = Float.parseFloat(list.getRow(0).get("vvalue"));
					fxmoney = String.valueOf(rate*money/10000);			
				}
				
				//向抗分险自救金账户转账
				VirementManager vir = new VirementManager();
				String iremakers = "亲爱的"+membername+"用户"+"在您从志愿者账户向现金账户转账后，需向抗分险自救金账户转账"+rate+"%";
				String oremakers = "亲爱的"+membername+"用户"+"在您从志愿者账户向现金账户转账后，需向抗分险自救金账户转账"+rate+"%";
				if(fxmoney != null){
					
					vir.execute(db, member_id, member_id, SystemAccountClass.CASH_ACCOUNT, SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT, fxmoney, iremakers, oremakers, "", false);
					
				}
				
		
			} catch (JDBCException e) {
				e.printStackTrace();
				
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
			
			
	}
		
		
		String member_id = businessJso.getString("member_id");
		
		String pay_money = businessJso.getString("pay_money");
		
		String out_account_code = businessJso.getString("out_account_code");
		
		VirementManager vir = new VirementManager();
		
		MapList list = vir.getAccountInfo(db, out_account_code, member_id);
		
		Long available_cash_amount = 0L;
		
		Long money = VirementManager.changeY2F(pay_money);
		
		JSONObject jso = new JSONObject();
		jso.put("code", "9999");
		
		int i = 0;
		if(!checkProcessBuissnes(id,db) && "1".equals(type))
		{
			if(!Checker.isEmpty(list))
			{
				available_cash_amount = Long.parseLong(list.getRow(0).get("available_cash_amount"));
				if(available_cash_amount>0 && available_cash_amount>=money)
				{
					String usql = "update mall_account_info set available_cash_amount = '"+(available_cash_amount-money)+"' where id = '"+list.getRow(0).get("id")+"'";
					i = db.execute(usql);
				}
				
			}
			
			if(i>0)
			{
				jso.put("code", "0");
			}
			
		}
		
		return jso.toString();
	}

}
