package com.am.frame.payment;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;


/**
 * 支付统一订单管理
 * @author mac
 */
public class PaymentUniformOrderManager 
{
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	DBManager db = new DBManager();

	/**
	 * 调用统一下单执行方法
	 * @param request
	 * @param response
	 * @param params 参数集合  {member_id, out_account_code, pay_money, pay_id, business, outremakes}
	 * @return
	 */
	public JSONObject doPaymentUniformOrder(PaymentRequestEntity request)
	{
		JSONObject result = null;
		
		MapList list = null;
		
		if(!Checker.isEmpty(request.getOutAccountCode()) && !Checker.isEmpty(request.getMemberId()))
		{
			
			try 
			{
				list = getAccountInfo(db,request.getOutAccountCode(),request.getMemberId());
				
				String payOrderClass=list.getRow(0).get("pay_order_class");
				
				IPaymentUniformOrder iPaymentUniformOrder = classNameToObject(payOrderClass);
				
//				params.put("account_list", list);
				request.setAccountList(list);
				
				//参数 返回 带有账户信息
				result  = iPaymentUniformOrder.execute(request);
				
				
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}else{
			logger.info("支付参数不正确 getOutAccountCode："+request.getOutAccountCode()+"\t getMemberId:"+request.getMemberId());
		}
		
		return result;
	}
	
	
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DBManager db,String outAccountCode,String outMemberId){
		
		MapList list = null;
		
		String sql = "";	
		//后台
			sql = "select mai.*"
					+ " ,myac.sa_code"
					+ " ,myac.pay_order_class"
					+ " ,myac.refund_order_class"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE mai.member_orgid_id ='"+outMemberId+"'"
					+ " and myac.status_valid='1' and myac.sa_code = '"+outAccountCode+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	//依据类名反射出对象
	private IPaymentUniformOrder classNameToObject(String className)
	{
		IPaymentUniformOrder result=null;

		try 
		{
			result=(IPaymentUniformOrder)Class.forName(className).newInstance();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
}
