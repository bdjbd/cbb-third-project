package com.am.frame.payment.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.IPaymentUniformOrder;
import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.transactions.pay.PayManager;

/**
 * @author YueBin
 * @create 2016年7月4日
 * @version 
 * 说明:<br />
 * 系统账号订单支付接口实现类
 * 
 */
public class SystemAccountPaymentUniformOrder implements IPaymentUniformOrder {

	@Override
	public JSONObject execute(PaymentRequestEntity request) {
		
		JSONObject result=new JSONObject();
		
		PayManager payManager = new PayManager();
		
		try {
//			result = payManager.excunte(
//					params.getString("member_id"), 
//					params.getString("out_account_code"), 
//					params.getString("pay_money"), 
//					params.getString("pay_id"), 
//					params.getString("business"),
//					params.getString("outremakes"));
			result = payManager.excunte(
					request.getMemberId(), 
					request.getOutAccountCode(), 
					request.getPayMoney(), 
					request.getPayId(), 
					request.getBusiness(),
					request.getOutRemakes());
			
			//系统账号支付，返回账号类型编码
			result.put("account_type_code","SYSTEM_ACCOUNT");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		return result;
	}

}
