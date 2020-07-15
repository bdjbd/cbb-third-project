package com.am.frame.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;

/**
 * 退单
 * @author mac
 *
 */
public class RefundManager 
{

	DBManager db = new DBManager();

	/**
	 * 统一退单接口   发起退款请求
	 * @param request
	 * @param response
	 * @param params 参数集合  {memberId, outAccountCode, pay_money, pay_id, business, outremakes}
	 * @return
	 */
	public JSONObject doRefundManager (HttpServletRequest request,HttpServletResponse response,JSONObject params)
	{
		JSONObject result = null;
		
		MapList list = null;
		
		if(params.has("pay_id") && params.has("memberId"))
		{
			
			try 
			{
				list = getAccountInfo(db,params.get("pay_id").toString());
				
				IPaymentRefund iPaymentRefund = classNameToObject(list.getRow(0).get("refund_order_class"));
				
				result  = iPaymentRefund.execute(request, response, params);
				
				
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
		}
		
		return result;
	}
	
	
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DBManager db,String payid){
		
		MapList list = null;
		
		String sql = "";	
		
			sql = "select mtrd.*,msac.refund_order_class from mall_trade_detail as mtrd "
					+ "left join mall_system_account_class as msac on mtrd.sa_class_id = msac.id "
					+ "where mtrd.id = '"+payid+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 调用退款处理功能
	 * @param db  DB
	 * @param orderId  退款订单ID
	 * @param params 扩展参数
	 * @return
	 */
	public JSONObject processRefundCallBack(DB db,String payId,JSONObject params){
		JSONObject result = null;
		try 
		{
			//更新交易时间
			String updateSQL="UPDATE mall_trade_detail SET trade_time=now() WHERE id=? ";
			db.execute(updateSQL, payId, Type.VARCHAR);
			
			MapList list = getAccountInfo(db,payId);
			
			IPaymentRefund iPaymentRefund = classNameToObject(list.getRow(0).get("refund_order_class"));
			
			result  = iPaymentRefund.processRefundNotify(db,payId,params);
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	//依据类名反射出对象
	private IPaymentRefund classNameToObject(String className)
	{
		IPaymentRefund result=null;

		try 
		{
			result=(IPaymentRefund)Class.forName(className).newInstance();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	

	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DB db,String payid){
		
		MapList list = null;
		
		String sql = "";	
		
			sql = "select mtrd.*,msac.refund_order_class from mall_trade_detail as mtrd "
					+ "left join mall_system_account_class as msac on mtrd.sa_class_id = msac.id "
					+ "where mtrd.id = '"+payid+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	

}
