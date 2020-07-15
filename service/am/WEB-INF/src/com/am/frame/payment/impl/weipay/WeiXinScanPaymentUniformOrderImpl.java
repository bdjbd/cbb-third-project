package com.am.frame.payment.impl.weipay;

import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.payment.impl.AbstractPaymentUniformOrder;

/***
 * 微信扫码支付下单接口
 * @author yuebin
 *
 */
public class WeiXinScanPaymentUniformOrderImpl  extends AbstractPaymentUniformOrder {

	@Override
	public JSONObject execute(PaymentRequestEntity request) {
		JSONObject result=new JSONObject();
		try {
			saveRecord(request);
			result.put("code",0);
			result.put(ACCOUNT_TYPE_CODE,WEI_XIN_SANC_PAY);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
