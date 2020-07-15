package com.am.frame.transactions.callback;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 账号交易回调抽象类，主要功能：
 * <p>
 * 1，完成检查是否完成交易  
 * 	   checkTradeState(String id,String business,DB db,String type):
 * 	   返回true，表示交易成功，返回false，表示交易失败
 * </p>
 * <br>
 * 2，检查业务是否已经处理
 *    checkProcessBuissnes(String id,DB db)
 *    返回false，表示业务未处理；返回true，表示业务已经处理
 * <br>
 * <p>
 * 3，更新业务是否处理
 *    updateProcessBuissnes(String id,DB db,String state)
 *    更新业务状态，state0：未处理；1：处理
 *</p>
 * @author yuebin
 *
 */
public abstract class AbstraceBusinessCallBack implements IBusinessCallBack {
	
	
	/**
	 * 检查交易是否完成
	 * @param id  交易ID
	 * @param business  业务参数
	 * @param db  DB
	 * @param type  类型
	 * @return  true=交易成功；false=交易失败
	 */
	public boolean checkTradeState(String id,String business,DB db,String type){
		boolean result=false;
		
		//检查是否交易完成  trade_state 1=交易成功；2=交易失败
		String checkSQL="SELECT trade_state FROM mall_trade_detail WHERE id=? ";
		
		try {
			MapList map= db.query(checkSQL,id, Type.VARCHAR);
			if(!Checker.isEmpty(map)&&"1".equals(map.getRow(0).get("trade_state"))){
				result=true;
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 检查业务是否已经处理
	 * @param id  交易id
	 * @param db  db
	 * @return 返回true，表示业务已经处理；返回false，表示业务还为处理
	 */
	public boolean checkProcessBuissnes(String id,DB db){
		boolean result=true;
		
		//检查是否交易完成  is_process_buissnes 0：未处理; 1：已处理
		String checkSQL="SELECT is_process_buissnes FROM mall_trade_detail WHERE id=? ";
		
		try {
			MapList map= db.query(checkSQL,id, Type.VARCHAR);
			if(!Checker.isEmpty(map)&&"0".equals(map.getRow(0).get("is_process_buissnes"))){
				result=false;
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 更新业务状态是否处理
	 * @param id 交易明细id
	 * @param db  DB
	 * @param state   0：未处理；1：处理
	 */
	public void updateProcessBuissnes(String id,DB db,String state){
		String updateSQL="UPDATE mall_trade_detail SET is_process_buissnes=?  WHERE id=? ";
		
		try {
			db.execute(updateSQL,new String[]{
					state,id
			}, new int[]{
					Type.INTEGER,Type.VARCHAR
			});
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
	}
	

}
