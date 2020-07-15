package com.am.frame.systemAccount.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;


/**
 * 信誉卡 充值 还款 提现
 * @author mac
 *
 */
public class CreditCardAccountUpdate implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		//订单id
		String pay_id = request.getParameter("pay_id");
		
		String pay_type = request.getParameter("pay_type");
		
		String memberid = request.getParameter("memberid");
		
		String account_code = "CREDIT_CARD_ACCOUNT";
		
		DB db = null;
		
		String ssql = "SELECT mai.*"
				+ " FROM mall_account_info as mai "
				+ "LEFT JOIN mall_system_account_class as msac ON msac.id = mai.a_class_id "
				+ "WHERE msac.status_valid='1' AND mai.member_orgid_id='"+memberid+"' AND  msac.sa_code='"+account_code+"'";
		
		
	
		
		String sql = "SELECT mtd.business_json,mtd.is_process_buissnes,mtd.mid,mtd.trade_state,mtd.trade_total_money"
				+ ",mai.*"
				+ "	FROM mall_trade_detail as mtd "
				+ " LEFT JOIN mall_account_info AS mai ON mtd.account_id = mai.id "
				+ " LEFT JOIN mall_system_account_class AS msac ON msac.id = mai.a_class_id  "
				+ " WHERE msac.status_valid='1' "
				+ " AND msac.sa_code = 'CREDIT_CARD_ACCOUNT'"
				+ " AND mtd.trade_state = '1'"
				+ " AND mtd.is_process_buissness = '0'"
				+ " AND mai.member_orgid_id='"+memberid+"'";
		try {
			
			db = DBFactory.newDB();
			
			MapList alist = db.query(ssql);
			
			if(!Checker.isEmpty(alist)){
				
				String usql  = "update  mall_account_info set starting_amount ='"+alist.getRow(0).get("balance")+"'";
				
				db.execute(usql);
				
			}
			
			MapList list = db.query(sql);
			
			if(!Checker.isEmpty(list)){
				
//				for (int i = 0; i < list.size(); i++) {
//					
//					
//				}
				
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
		
		
		return null;
	}

}
