package com.ambdp.cro.cro_rent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/**
 *@author 张少飞
 *@create 2017/7/10
 *@version
 *说明：修车厂缴费界面Server
 */
public class Cro_rentServer {
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	/**
	 * 更新aorg表中租期到期日期
	 * @param 修车厂的orgcode  续费天数leasedays
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateLeaseDays(String orgcode, String leasedays, DB db) throws JDBCException {
		
		String updateSql = "UPDATE aorg SET LeaseExpireDate=(LeaseExpireDate + interval '"+leasedays+" D') WHERE orgid='"+orgcode+"' ";
		System.err.println("修改租期到期日期SQL》》"+updateSql);
		db.execute(updateSql);
	}
	
	
	
	/**
	 * 扣除购买用户的现金账号余额
	 * @param stockPrice 每股金额
	 * @param buyNumber  购买的股份数量
	 * @param orgid 购买者的ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateAccountInfo(String code,String stockCode,String stockPrice, int buyNumber,
			String orgid, DB db) throws JDBCException {
		
//		String updateAccountInfoSql = "UPDATE mall_account_info SET balance=balance-("+stockPrice+"*"+buyNumber+") WHERE member_orgid_id='"+orgid+"' "
//				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
//		
//		db.execute(updateAccountInfoSql);	
//		String UpdateStockAccountSql  = "UPDATE mall_account_info SET total_amount=total_amount+("+stockPrice+"*"+buyNumber+") WHERE member_orgid_id='"+orgid+"' "
//				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+stockCode+"') ";
//		db.execute(UpdateStockAccountSql);
		
	}

}
