package com.am.frame.payment.impl;

import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;


/**
 * 支付宝web支付统一下单接口
 * 使用账号类型  GROUP_ALIPAY_ACCOUNT_MODE_WEB
 * 
 * @author yuebin
 *
 */
public class AlipayWebPaymentUniformOrderImpl extends AbstractPaymentUniformOrder {

	@Override
	public JSONObject execute(PaymentRequestEntity request) {
		JSONObject result=new JSONObject();
		try {
			saveRecord(request);
			result.put("code",0);
			result.put(ACCOUNT_TYPE_CODE,ALIPAY_WEB_PAY);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
