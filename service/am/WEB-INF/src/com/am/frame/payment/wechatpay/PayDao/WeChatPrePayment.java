package com.am.frame.payment.wechatpay.PayDao;

import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONObject;

import com.am.frame.payment.util.proputil.PropertiesUtil;
import com.am.frame.payment.wechatpay.entity.WxPayDto;
import com.am.frame.payment.wechatpay.utils.GetWxOrderno;
import com.am.frame.payment.wechatpay.utils.RequestHandler;
import com.am.frame.payment.wechatpay.utils.Utils;

/**
 * 微信预支付统一下单实现类 
 * @author xianlin
 *
 */

public class WeChatPrePayment {


	  private static WeChatPrePayment instance = null; 
	    
	  public static WeChatPrePayment getInstance() { 
	    synchronized (WeChatPrePayment.class) { 
	      if(instance == null) { 
	        instance = new WeChatPrePayment(); 
	      } 
	    }     
	    return instance; 
	  } 
	    
	  private WeChatPrePayment() { 
	     
	  } 
	  
  	//微信支付商户开通后 微信会提供appid和appsecret和商户号partner
	private static String appid = PropertiesUtil.getPropertiesUtil().getValue("wxappid");
	private static String appsecret = PropertiesUtil.getPropertiesUtil().getValue("wxappsecret");
	private static String partner = PropertiesUtil.getPropertiesUtil().getValue("wxpartner");
	//这个参数partnerkey是在商户后台配置的一个32位的key,微信商户平台-账户设置-安全设置-api安全
	private static String partnerkey = PropertiesUtil.getPropertiesUtil().getValue("wxpartnerkey");
	//公众账号appid，及微信分配的公众账号id(企业号corpid即为此appid)
	private static String mchappid = PropertiesUtil.getPropertiesUtil().getValue("wxpartner");
	
	/**
	 * 获取请求预支付id报文
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getPackage(WxPayDto tpWxPayDto) {
		
		String openId = tpWxPayDto.getOpenId();
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

		// ---必须参数
		// 随机字符串
		String nonce_str = tpWxPayDto.getNonce();
		// 商品描述根据情况修改
		String body = tpWxPayDto.getBody();
		// 商户订单号
		String out_trade_no = orderId;

		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", partner);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", orderId);

		// 这里写的金额为1 分到时修改
		packageParams.put("total_fee", totalFee);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);

		packageParams.put("trade_type", trade_type);
		packageParams.put("openid", openId);

		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
				+ partner + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>"
				+ "<body><![CDATA[" + body + "]]></body>" 
				+ "<out_trade_no>" + out_trade_no
				+ "</out_trade_no>" + "<attach>" + attach + "</attach>"
				+ "<total_fee>" + totalFee + "</total_fee>"
				+ "<spbill_create_ip>" + spbill_create_ip
				+ "</spbill_create_ip>" + "<notify_url>" + notify_url
				+ "</notify_url>" + "<trade_type>" + trade_type
				+ "</trade_type>" + "<openid>" + openId + "</openid>"
				+ "</xml>";
		String prepay_id = "";
		String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		
		
		new GetWxOrderno();
		prepay_id = GetWxOrderno.getPayNo(createOrderURL, xml);

		System.out.println("获取到的预支付ID：" + prepay_id);
		
		JSONObject jsonobj = new JSONObject();
		try{
			jsonobj.put("PREPAYID", prepay_id);		//预订单ID
			jsonobj.put("ORDERID", orderId);	//商户订单ID
			jsonobj.put("PARTNERID", partner);	//商户号
			jsonobj.put("NONCESTR", nonce_str);	//随机字符串
			jsonobj.put("TIMESTAMP", System.currentTimeMillis());
			jsonobj.put("SIGN", sign);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		
		return jsonobj.toString();
	}
}
