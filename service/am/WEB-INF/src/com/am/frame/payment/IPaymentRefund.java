package com.am.frame.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;

/**
 * 退单接口
 * @author mac
 *
 */
public interface IPaymentRefund 
{
	/**
	 * 退款处理，此功能更主要是发起请求
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
	public JSONObject execute(HttpServletRequest request,HttpServletResponse response,JSONObject params);

	
	/**
	 *业务处理完成后，回调接口
	 * @param db
	 * @param payId 支付ID
	 * @param params
	 * @return
	 */
	public JSONObject processRefundNotify(DB db, String payId,JSONObject params);

}
