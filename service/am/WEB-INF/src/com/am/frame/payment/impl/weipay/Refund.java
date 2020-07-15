package com.am.frame.payment.impl.weipay;

import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.impl.weipay.utils.ClientCustomSSL;
import com.am.frame.payment.impl.weipay.utils.RequestHandler;


//微信退款

public class Refund {
	/**
	 * 微信退款方法
	 * @param params  参数{out_refund_no:'订单id',out_trade_no:'订单号',total_fee:'总金额',refund_fee:'退款金额'}
	 */
	public void wechatRefund(JSONObject params) {
		//api地址：http://mch.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
		
		String out_refund_no = "";// 退款单号
		String out_trade_no = "";// 订单号
		String total_fee = "1";// 总金额
		String refund_fee = "1";// 退款金额
		String nonce_str = "4232343765";// 随机字符串
		String appid = "wxcb9da2af0e781b30"; //微信公众号apid
		String appsecret = "1414a395f25d9dbd635656865abd2a5f"; //微信公众号appsecret
		String mch_id = "1364893302";  //微信商户id
		String op_user_id = "1364893302";//就是MCHID
		String partnerkey = "xdg234235gdfsdsfvadgSDFBVXCVSGW1";//商户平台上的那个KEY
		
		if(params.has("out_refund_no") && params.has("out_trade_no"))
		{
			try 
			{
				out_refund_no = params.getString("out_refund_no");
				
				out_trade_no = params.getString("out_trade_no");
				
				total_fee  = params.getString("total_fee");
				
				refund_fee = params.getString("refund_fee");
				
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		
		
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("out_trade_no", out_trade_no);
		packageParams.put("out_refund_no", out_refund_no);
		packageParams.put("total_fee", total_fee);
		packageParams.put("refund_fee", refund_fee);
		packageParams.put("op_user_id", op_user_id);

		RequestHandler reqHandler = new RequestHandler(
				null, null);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
				+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign><![CDATA[" + sign + "]]></sign>"
				+ "<out_trade_no>" + out_trade_no + "</out_trade_no>"
				+ "<out_refund_no>" + out_refund_no + "</out_refund_no>"
				+ "<total_fee>" + total_fee + "</total_fee>"
				+ "<refund_fee>" + refund_fee + "</refund_fee>"
				+ "<op_user_id>" + op_user_id + "</op_user_id>" + "</xml>";
		String createOrderURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
		try {
			String s= ClientCustomSSL.doRefund(createOrderURL, xml);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Refund refund=new Refund();
		JSONObject json  = null;
		refund.wechatRefund(json);
	}
}
