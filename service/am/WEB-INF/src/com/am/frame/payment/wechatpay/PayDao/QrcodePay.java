package com.am.frame.payment.wechatpay.PayDao;

import java.util.SortedMap;
import java.util.TreeMap;

import com.am.frame.payment.wechatpay.entity.WxPayDto;
import com.am.frame.payment.wechatpay.utils.GetWxOrderno;
import com.am.frame.payment.wechatpay.utils.RequestHandler;
import com.am.frame.payment.wechatpay.utils.Utils;
import com.fastunit.Var;

/**
 * 获取微信扫码支付
 * @author xianlin
 *2016年5月28日
 */

public class QrcodePay {

	  private static QrcodePay instance = null; 
	    
	  public static QrcodePay getInstance() { 
	    synchronized (QrcodePay.class) { 
	      if(instance == null) { 
	        instance = new QrcodePay(); 
	      } 
	    }     
	    return instance; 
	  } 
	    
	  private QrcodePay() { 
	     
	  } 
	  
  	//微信支付商户开通后 微信会提供appid和appsecret和商户号partner
	private static String appid =Var.get("weiChartAppId") ;//PropertiesUtil.getPropertiesUtil().getValue("wxappid");
	private static String appsecret =Var.get("weiChartAppSecret") ;//PropertiesUtil.getPropertiesUtil().getValue("wxappsecret");
	private static String partner =Var.get("mch_id");//PropertiesUtil.getPropertiesUtil().getValue("wxpartner");
	//这个参数partnerkey是在商户后台配置的一个32位的key,微信商户平台-账户设置-安全设置-api安全
	private static String partnerkey =Var.get("MpWeiPayAPIKey") ;//PropertiesUtil.getPropertiesUtil().getValue("wxpartnerkey");
	//公众账号appid，及微信分配的公众账号id(企业号corpid即为此appid)
	private static String mchappid =Var.get("mch_id");//PropertiesUtil.getPropertiesUtil().getValue("wxpartner");
	
	
	/**
	 * 获取微信扫码支付二维码连接
	 */
	public static String getCodeurl(WxPayDto tpWxPayDto){
		
		// 1 参数
		// 订单号
		String orderId = Utils.getInstance().getNonceStr();
		// 附加数据 原样返回
		String attach = "";
		// 总金额以分为单位，不带小数点
		String totalFee = Utils.getInstance().getMoney(tpWxPayDto.getTotalFee());
		
		// 订单生成的机器 IP
		String spbill_create_ip = tpWxPayDto.getSpbillCreateIp();
		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notify_url = tpWxPayDto.getNotifyUrl();
		String trade_type = tpWxPayDto.getTrade_type();

		// 商户号
		String mch_id = partner;
		// 随机字符串
		String nonce_str =tpWxPayDto.getNonce();

		// 商品描述根据情况修改
		String body = tpWxPayDto.getBody();

		// 商户订单号
		String out_trade_no = orderId;

		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", out_trade_no);

		// 这里写的金额为1 分到时修改
		packageParams.put("total_fee", totalFee);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);

		packageParams.put("trade_type", trade_type);

		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
				+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>"
				+ "<body><![CDATA[" + body + "]]></body>" 
				+ "<out_trade_no>" + out_trade_no
				+ "</out_trade_no>" + "<attach>" + attach + "</attach>"
				+ "<total_fee>" + totalFee + "</total_fee>"
				+ "<spbill_create_ip>" + spbill_create_ip
				+ "</spbill_create_ip>" + "<notify_url>" + notify_url
				+ "</notify_url>" + "<trade_type>" + trade_type
				+ "</trade_type>" + "</xml>";
		String code_url = "";
		String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		
		
		new GetWxOrderno();
		code_url = GetWxOrderno.getCodeUrl(createOrderURL, xml);
		System.out.println("code_url----------------"+code_url);
		
		return code_url;
	}
}
