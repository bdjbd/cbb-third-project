package com.am.more.kopp_train.server;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月18日
 *@version
 *说明：
 */
public class UpdatePlayersServer {
	/**
	 * 给报名表中添加数据
	 * @param db
	 * @param memberid 会员id
	 * @param pcid  内容ID
	 * @param count  报名人数
	 * @throws JDBCException 
	 */
	public void updatePlayer(DB db, String memberid, String pcid,
			String count) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		String sqlinsert = "INSERT INTO mall_players( id, pc_id, member_id, count,create_time) "
				+ "values ('"+uuid+"','"+pcid+ "','"+memberid+ "','"+count+ "',now()) ";
		db.execute(sqlinsert);
	}

	/**
	 * 修改交易表的状态
	 * @param db
	 * @param payId  支付ID
	 * @throws JDBCException 
	 */
	public void updateTransaction(DB db, String payId) throws JDBCException {
		
		String updateStoreAllocInfoSql = "UPDATE mall_trade_detail SET is_process_buissnes='1' WHERE id='"+payId+"' ";
		db.execute(updateStoreAllocInfoSql);
	}

	/**
	 * 给文章购买表添加数据
	 * @param db
	 * @param memberid 会员id
	 * @param id  内容id
	 * @param count 次数
	 * @throws JDBCException 
	 */
	public void updatePurchaseRecords(DB db, String memberid, String id,
			String count) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		String selSql="SELECT * FROM mall_purchase_records WHERE pc_id='"+id+"' AND member_id='"+memberid+"'";
		
			MapList selMap = db.query(selSql);
			//如果不为空代表有数据
			if(!Checker.isEmpty(selMap)){
				if("0".equals(count)){
					String updatePurchaseRecordsSql = "UPDATE mall_purchase_records SET access=(-1) WHERE pc_id='"+id+"' AND member_id='"+memberid+"' ";
					db.execute(updatePurchaseRecordsSql);
				}else{
					String updatePurchaseRecordsSql = "UPDATE mall_purchase_records SET access=(access+"+count+") WHERE pc_id='"+id+"' AND member_id='"+memberid+"' ";
					db.execute(updatePurchaseRecordsSql);
				}
			}else{
				if("0".equals(count)){
					String insertPurchaseRecordsSql = "INSERT INTO mall_purchase_records( id, member_id, pc_id, access,create_time) "
							+ "values ('"+uuid+"','"+memberid+ "','"+id+ "','-1',now()) ";
					db.execute(insertPurchaseRecordsSql);
				}else{
					String insertPurchaseRecordsSql = "INSERT INTO mall_purchase_records( id, member_id, pc_id, access,create_time) "
							+ "values ('"+uuid+"','"+memberid+ "','"+id+ "','"+count+ "',now()) ";
					db.execute(insertPurchaseRecordsSql);
				}
				
			}
	}
	/**
	 * 每月1日0点更新购买文章金额=0，提现任务为未完成，每月阅读量为未完成，提现状态为否
	 * @param db
	 * 阅读量未完成：am_member中的journal_fee（分）小于常量中的monthly_journal_fee（元）
	 * @throws JDBCException 
	 */
	public void updateJournal_Fee(DB db) throws JDBCException {
		
		String updateStoreAllocInfoSql = "UPDATE AM_MEMBER SET journal_fee='0'";
		
		//TODO提现任务更新为未完成
		//TODO提现状态更新为未完成
		db.execute(updateStoreAllocInfoSql);
	}
	
	
}
