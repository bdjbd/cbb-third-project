package com.am.frame.payment;

import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;


/**
 * 统一支付下单
 * @author mac
 *
 */
public interface IPaymentUniformOrder 
{
	
	public static final String ACCOUNT_TYPE_CODE="ACCOUNT_TYPE_CODE";
	
	/**支付宝支付宝web支付**/
	public static final String ALIPAY_WEB_PAY="ALIPAY_WEB_PAY";
	
	/**微信web扫码**/
	public static final String WEI_XIN_SANC_PAY="WEI_XIN_SANC_PAY";
	
	/**
	 * 适用于移动端
	 * @param request PaymentRequestEntity {@link com.am.frame.payment.entity.PaymentRequestEntity}
	 * @param params  params
	 * @return
	 */
	public JSONObject execute(PaymentRequestEntity request);


}
