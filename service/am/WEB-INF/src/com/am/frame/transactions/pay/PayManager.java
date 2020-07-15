package com.am.frame.transactions.pay;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.transactions.callback.BusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 支付管理类
 * @author mac
 *
 */
public class PayManager {
	
	/**
	 * 系统现金账户  支付操作
	 * @param memberId 用户id
	 * @param accountCode 支付账户id
	 * @param pay_money 支付金额 单位元
	 * @param pay_id 支付单号
	 * @param busines 业务参数
	 * @param outRemakes 支付描述
	 * @return 
	 */
	public JSONObject excunte(String memberId,String accountCode,String pay_money,String pay_id,String business,String outRemakes){
		
		JSONObject json = null;
		//引入转账操作管理类
		VirementManager virementManager = new VirementManager();
		
		DB db = null;
		try {
			db = DBFactory.newDB();
			
			if(Checker.isEmpty(pay_id)){
				pay_id = UUID.randomUUID().toString();
			}
			//执行转账操作
			json = virementManager.execute(db, memberId, accountCode, pay_money,outRemakes, pay_id,business);
			
			
		} catch (Exception e) {
			json = new JSONObject();
			try {
				json.put("code", "999");
				json.put("msg", "系统异常,执行失败");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
		
	}
	
	/**
	 * 第三方 移动充值
	 * @param memberId 用户id
	 * @param inAccountCode 充值账号
	 * @param outAccountCode 支付账户
	 * @param pay_money 支付金额
	 * @param pay_id 支付单号
	 * @param busines 业务参数
	 * @param outRemakes 支付描述
	 * @return
	 */
	public JSONObject excunte(String memberId,String outAccountCode,String inAccountCode,String pay_money,String pay_id,String business,String outRemakes){
		
		JSONObject json = null;
		
		VirementManager virementManager = new VirementManager();
		
		DB db = null;
		
		try {
			
			db = DBFactory.newDB();
			

			//执行支付完成后操作，业务操作
			BusinessCallBack businessCallBack = new BusinessCallBack();
			
			businessCallBack.callBack(pay_id, db,"1");
//			json = virementManager.execute(db, memberId, accountCode, pay_money,outRemakes, pay_id,business);
			
		} catch (Exception e) {
			json = new JSONObject();
			try {
				json.put("code", "999");
				json.put("msg", "系统异常,执行失败");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
		
	}
	
	
	/**
	 * 第三方 pc端支付
	 * @param memberId 用户id
	 * @param inAccountCode 充值账号
	 * @param outAccountCode 支付账户
	 * @param pay_money 支付金额
	 * @param pay_id 支付单号
	 * @param busines 业务参数
	 * @param outRemakes 支付描述
	 * @return
	 */
	public JSONObject excuntePc(String memberId,String accountCode,String pay_money,String pay_id,String business,String outRemakes){
		
		JSONObject json = null;
		
		VirementManager virementManager = new VirementManager();
		
		DB db = null;
		try {
			db = DBFactory.newDB();
			
			json = virementManager.execute(db, memberId, accountCode, pay_money,outRemakes, pay_id,business);
			
		} catch (Exception e) {
			json = new JSONObject();
			try {
				json.put("code", "999");
				json.put("msg", "系统异常,执行失败");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return json;
		
	}
	
	/**
	 * 第三方pc端充值
	 * @param memberId
	 * @param accountCode
	 * @param pay_money
	 * @param pay_id
	 * @param business
	 * @param outRemakes
	 * @return
	 */
//	public JSONObject excunte(String memberId,String accountCode,String pay_money,String pay_id,String business,String outRemakes){
//		
//		JSONObject json = null;
//		
//		VirementManager virementManager = new VirementManager();
//		
//		DB db = null;
//		try {
//			db = DBFactory.newDB();
//			
//			json = virementManager.execute(db, memberId, accountCode, pay_money,outRemakes, pay_id,business);
//			
//		} catch (Exception e) {
//			json = new JSONObject();
//			try {
//				json.put("code", "999");
//				json.put("msg", "系统异常,执行失败");
//			} catch (JSONException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		}
//		return json;
//		
//	}

}
