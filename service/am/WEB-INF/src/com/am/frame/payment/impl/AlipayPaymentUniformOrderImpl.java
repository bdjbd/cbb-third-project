package com.am.frame.payment.impl;

import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;

/**
 * @author YueBin
 * @create 2016年7月1日
 * @version 
 * 说明:<br />
 * 支付宝统一下单接口
 */
public class AlipayPaymentUniformOrderImpl extends AbstractPaymentUniformOrder {

	@Override
	public JSONObject execute(PaymentRequestEntity request) {
		
		JSONObject result=new JSONObject();
		try {
			JSONObject DataSaveresult = saveRecord(request);
			result.put("code",0);
			result.put("account_type_code","aliPay");
			result.put("API_KEY", DataSaveresult.get("API_KEY"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	

}
