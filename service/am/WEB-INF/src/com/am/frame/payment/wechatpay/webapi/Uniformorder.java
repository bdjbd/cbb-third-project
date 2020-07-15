package com.am.frame.payment.wechatpay.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.payment.wechatpay.PayDao.QrcodePay;
import com.am.frame.payment.wechatpay.PayDao.WeChatPrePayment;
import com.am.frame.payment.wechatpay.entity.WxPayDto;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class Uniformorder implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject jsonObj = new JSONObject();
		
		try{
			//接受支付数据
			String payJson = request.getParameter("DATA");
			//支付类型1=扫码支付，2=app调用支付
			String PayType = request.getParameter("PAYTYPE");
			//返回参数
			String returnCode ="";
			
			if(!Checker.isEmpty(payJson))
			{
				//转换参数
				JSONObject PayObj = new JSONObject(payJson);

				String notifyUrl = PayObj.getString("NOTIFYURL"); //回调地址
				String totalFee = PayObj.getString("TOTALFEE");	//支付金额
				String spbillCreateIp=PayObj.getString("SPBILLCREATEIP");		//支付Ip
				String body = PayObj.getString("BODY");	//商品描述
				String nonce = PayObj.getString("NONCE");
				String TradeType = PayObj.getString("TRADETYPE");	//支付类型 APP,NATIVE,JSAPI
				String OpenID = PayObj.getString("OPENID");		//如果为NATIVE可为空
				
				if("2".equals(PayType))
				{
					WxPayDto wxentity = new WxPayDto();
					wxentity.setNotifyUrl(notifyUrl);
					wxentity.setTotalFee(totalFee);
					wxentity.setSpbillCreateIp(spbillCreateIp);
					wxentity.setBody(body);
					wxentity.setNonce(nonce);
					wxentity.setOpenId(OpenID);
					wxentity.setTrade_type(TradeType);
					//执行预支付请求
					returnCode = WeChatPrePayment.getInstance().getPackage(wxentity);
					
				}else
				{
					//扫码支付
					WxPayDto wxentity = new WxPayDto();
					wxentity.setNotifyUrl(notifyUrl);
					wxentity.setTotalFee(totalFee);
					wxentity.setSpbillCreateIp(spbillCreateIp);
					wxentity.setBody(body);
					wxentity.setNonce(nonce);
					wxentity.setOpenId(OpenID);
					wxentity.setTrade_type(TradeType);
				    QrcodePay.getInstance();
					//获取二维码内容
					returnCode = QrcodePay.getCodeurl(wxentity);
				}
			
			}
		    
		    jsonObj.put("wxcode", returnCode);
		}catch(Exception e){
			e.printStackTrace();
		}
	    
		return jsonObj.toString();
	}
	// 将 s 进行 BASE64 编码 
	public static String getBASE64(String s) { 
	if (s == null) return null; 
	return (new sun.misc.BASE64Encoder()).encode( s.getBytes() ); 
	} 

}
