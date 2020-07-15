package com.am.app_plugins.rural_market.server;

import java.util.UUID;

import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;

/**
 *@author wangxi
 *@create 2016年6月13日
 *@version
 *说明：农村大市场server
 */
public class RuralMarketServer {
	
	/**
	 * 扣除购买者现金帐号余额
	 * @param db
	 * @param buyersId 购买者id
	 * @param price  购买金额元
	 * @param outAccountCode   帐号类型
	 * @throws JDBCException 
	 */
	public void deductionBalance(DB db, String buyersId, String price,
			String outAccountCode) throws JDBCException {
		
		String updateBalanceSql = "UPDATE mall_account_info SET balance=balance-(?::FLOAT*100) WHERE member_orgid_id=? "
				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code=?) ";
		
		db.execute(updateBalanceSql,new String[]{
				price,buyersId,outAccountCode
		},new int[]{
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
		});
	}
	/**
	 * 给农村大市场交易记录表中插一条数据
	 * @param db
	 * @param meId 货物ID
	 * @param buyersId 购买者iD
	 * @param price  金额元
	 * @throws JDBCException 
	 */
	public void inserRecord(DB db, String meId, String buyersId,
			String price) throws JDBCException {
		String uuid=UUID.randomUUID().toString();
		String inserSQL="INSERT INTO mall_marketplace_trade_record "
				+ " ( id, me_id, buyers_id,trade_amount,status,create_time )"
				+ "  VALUES "
				+ "(?,?,?,?::FLOAT*100,1,now()) ";
		db.execute(inserSQL,
				new String[]{uuid,meId,buyersId,price},
				new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
		}
	/**
	 * 修改大市场信息表状态
	 * @param db
	 * @param meId  信息id
	 * @param status 状态
	 * @throws JDBCException 
	 */
	public void updateinfo(DB db, String meId, String status) throws JDBCException {
		
		String updateBalanceSql = "UPDATE mall_marketplace_entity "
				+ " SET status='"+status+"' WHERE id='"+meId+"' ";
		
		db.execute(updateBalanceSql);
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
		String tradeType = "1";
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
		
		trdl.afterActions(db, bdb);
		
	}
	/**
	 * 修改农村大市场交易记录表状态
	 * @param db
	 * @param mtrId
	 * @param recordStatus 
	 * @throws JDBCException 
	 */
	public void updateRecord(DB db, String mtrId, String recordStatus) throws JDBCException {
		String updateRecordSql = "UPDATE mall_marketplace_trade_record "
				+ " SET status='"+recordStatus+"' WHERE id='"+mtrId+"' ";
		
		db.execute(updateRecordSql);
		
	}
	
	
	/**
	 * 保存发布大市场
	 * @param title  标题
	 * @param classId 分类
	 * @param memberid  发布人
	 * @param content  内容
	 * @param status  状态
	 * @param imagepaths 
	 * @param recvArea 
	 * @param recvCity 
	 * @param recvProvince 
	 * @param free 
	 * @param db
	 * @throws JDBCException 
	 */
	public void SavePublishMarket(String uuid,String title, String classId,
			String memberid, String content, String status, String imagepaths,String price, String recvProvince, String recvCity, String recvArea, String free, DB db) throws JDBCException {
		String inserPublishSQL=" INSERT INTO mall_marketplace_entity (id,title,class_id,me_content,list_images,price,status,member_id,create_time,free,province,city,zone) "
				+ " VALUES('" +uuid + "','" +title + "','"+classId+"','"+content+"','"+imagepaths+"', "+price+"*100,'2','"+memberid+"','now()',"+free+"*100,'"+recvProvince+"','"+recvCity+"','"+recvArea+"') ";
		
		db.execute(inserPublishSQL);
	}
	/**
	 * 保存文章
	 * @param title  标题
	 * @param classId  分类
	 * @param memberid  发布人
	 * @param content  内容
	 * @param status  状态
	 * @param imagepaths 
	 * @param recvArea 
	 * @param recvCity 
	 * @param recvProvince 
	 * @param db 
	 * @throws JDBCException 
	 */
	public void SaveMarket(String uuid,String title, String classId, String memberid,
			String content, String status, String imagepaths, String price,String recvProvince, String recvCity, String recvArea, DB db) throws JDBCException {
		String inserSQL=" INSERT INTO mall_marketplace_entity (id,title,class_id,me_content,list_images,price,status,member_id,create_time,province,city,zone) "
				+ " VALUES('" +uuid + "','" +title + "','"+classId+"','"+content+"','"+imagepaths+"', "+price+"*100,'1','"+memberid+"','now()','"+recvProvince+"','"+recvCity+"','"+recvArea+"' ) ";
		
		db.execute(inserSQL);
	}
	
	/**
	 * 更新农厂大市场数据
	 * @param uuid
	 * @param title
	 * @param classId
	 * @param memberid
	 * @param content
	 * @param status
	 * @param imagepaths
	 * @param price
	 * @param recvProvince
	 * @param recvCity
	 * @param recvArea
	 * @param db
	 * @throws JDBCException
	 */
	public void UpdateMarket(String uuid,String title, String classId, String memberid,
			String content, String status, String imagepaths, String price,String recvProvince, String recvCity, String recvArea, DB db) throws JDBCException {
		
		String updateSQL = "UPDATE mall_marketplace_entity SET "
				+ "title='"+title+"',class_id='"+classId+"',me_content='"+content+"',list_images='"+imagepaths+"',"
				+ "price="+price+"*100,status='1',member_id='"+memberid+"',province='"+recvProvince+"',city='"+recvCity+"',zone='"+recvArea+"'"
				+ "WHERE id ='"+uuid+"'";
		
		
		db.execute(updateSQL);
	}


}
