package com.ambdp.agricultureProject.server;

import java.util.UUID;

import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月21日
 *@version
 *说明：农业项目退股Server
 */
public class WithdrawProjectStockServer {
	/**
	 * 更新农业项目表中已经认购份数 (减去退去股份)
	 * @param projectId  农业表的ID 
	 * @param buyNumber   退股数量
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateProject(String projectId, String buyNumber, DB db) throws JDBCException {
			
		if(!Checker.isEmpty(projectId)){
			String updateProjectsSql = "UPDATE mall_agriculture_projects SET already_buy_number=already_buy_number-'"+buyNumber+"' WHERE id='"+projectId+"' ";
			
			db.execute(updateProjectsSql);
		}
		
		
	}
	/**
	 * 更新社员/机构项目投资购买股份记录表中状态 (状态改为3--退股)
	 * @param id  社员/机构项目投资购买股份记录表ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateStockRecord(String id, DB db) throws JDBCException {
		if(!Checker.isEmpty(id)){
			String updateBuyStockSql = "UPDATE mall_buy_stock_record SET status=3 WHERE id='"+id+"' ";
			
			db.execute(updateBuyStockSql);
		}
		
	}
	/**
	 * 增加购买用户的身份股金账号余额
	 * @param buyerId  购买者ID
	 * @param returnAmount  退股金额(元)
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateAccountInfo(String code,String buyerId, String returnAmount, DB db) throws JDBCException {
		
		if("GROUP_IDENTITY_STOCK_ACCOUNT".equals(code)){
			String updateAccountInfoSql = "UPDATE mall_account_info SET balance=balance+("+returnAmount+"*100) WHERE member_orgid_id='"+buyerId+"' "
					+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
			db.execute(updateAccountInfoSql);
			
//			String UpdateStockAccountSql  = "UPDATE mall_account_info SET total_amount=total_amount-("+returnAmount+"*100) WHERE member_orgid_id='"+buyerId+"' "
//					+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
//			db.execute(UpdateStockAccountSql);
		}else{
			String updateAccountInfoSql = "UPDATE mall_account_info SET balance=balance+("+returnAmount+") WHERE member_orgid_id='"+buyerId+"' "
					+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
			db.execute(updateAccountInfoSql);	
			
//			String UpdateStockAccountSql  = "UPDATE mall_account_info SET total_amount=total_amount-("+returnAmount+") WHERE member_orgid_id='"+buyerId+"' "
//					+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
//			db.execute(UpdateStockAccountSql);
		}
		
		
		
	}
		/**
	 * 给交易记录表中插一条数据
	 * @param db
	 * @param buyersId  购买者id
	 * @param price  金额
	 * @param iremakers  说明
	 * @param iremakers2 
	 * @param trdl 用户交易插入
	 * @param bdb  用户交易bean类
	 * @throws JDBCException 
	 */
	public void transactionrecord(DB db, String buyersId, String price,
			 String outAccountCode, String iremakers, TransactionDetail trdl, AfterDetailBean bdb) throws JDBCException {
		String uuid=UUID.randomUUID().toString();
		String tradeType = "2";
		Long priceMoney = Long.parseLong(price);
		//账号类型
		String AccountType = BonusJobclassImpl.getInstance().returnAccountTypeId(outAccountCode,db);
		//账号id
		String Accountid = BonusJobclassImpl.getInstance().returnAccountinfoId(AccountType, buyersId, db);
		
		bdb.setTableRow(bdb.getTranTable().addInsertRow());
		bdb.setId(uuid);
		bdb.setAccount_id(Accountid);
		bdb.setMember_id(buyersId);
		bdb.setRmarks(iremakers);
		bdb.setTrade_total_money(priceMoney*100);
		bdb.setCounter_fee(0);
		bdb.setSa_class_id(AccountType);
		bdb.setTrade_type(tradeType);
		bdb.setTrade_state("1");
		
		trdl.earningActions(db, bdb);
		
	}

}
