package com.am.frame.payment.impl.weipay;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.impl.weipay.utils.GetWxOrderno;
import com.am.frame.payment.impl.weipay.utils.RequestHandler;
import com.am.frame.payment.impl.weipay.utils.Sha1Util;
import com.am.frame.payment.impl.weipay.utils.TenpayUtil;
import com.am.frame.payment.impl.weipay.utils.http.HttpConnect;
import com.am.frame.transactions.virement.VirementManager;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.Var;

/**
 * 微信支付 调用请求类
 * 
 * @author mac
 *
 */
public class WePayRequest 
{
	
	private Logger log = Logger.getLogger(WePayRequest.class); 

	private String appid = "";
	private String appsecret = "";
	private String partner = "";
	private String partnerkey = "";
	private String notify_url ="";
	
	public JSONObject doWeinXinRequest(HttpServletRequest request,HttpServletResponse response,String reparam,String API_key) throws Exception {  
		
		appid = Var.get("wepay_mobile_appid");
		appsecret = Var.get("wepay_mobile_appsecret");
		partner = Var.get("wepay_mobile_partner");
		partnerkey = Var.get("wepay_mobile_key");
		notify_url = Var.get("wepay_mobile_notify");
		
		JSONObject resouls = new JSONObject();
		//商户参数接收
        JSONObject reobj = new JSONObject(reparam);
        
        String out_trade_no = reobj.getString("pay_id");
//        String out_trade_no ="221C03BF342544D9931F0FF8396DACB6";
        String total_fee = reobj.getString("total_fee");
        
        
		String userId = API_key; 
		
		String orderNo = out_trade_no;
		
		String money = total_fee;
		
		String code = "";
		
		//金额转化为分为单位
//		float sessionmoney = Float.parseFloat(money);
//		
//		String finalmoney = String.format("%.2f", sessionmoney);
//		
//		finalmoney = finalmoney.replace(".", "");
		
		String openId ="";
		String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
		Map<String, Object> dataMap = new HashMap<String, Object>();
		com.am.frame.payment.impl.weipay.utils.http.HttpResponse temp = HttpConnect.getInstance().doGetStr(URL);		
		String tempValue="";
			if( temp == null){
				try {
					tempValue = temp.getStringResult();
				} catch (Exception e) {
					e.printStackTrace();
				}
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(tempValue);
					if(jsonObj.has("errcode")){
						System.out.println(tempValue);
						openId = jsonObj.getString("openid");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else
			{
				
			}
	
			//获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
			String currTime = TenpayUtil.getCurrTime();
			//8位日期
			String strTime = currTime.substring(8, currTime.length());
			//四位随机数
			String strRandom = TenpayUtil.buildRandom(4) + "";
			//10位序列号,可以自行调整。
			String strReq = strTime + strRandom;
			
			
			//商户号
			String mch_id = partner;
			//子商户号  非必输
			//String sub_mch_id="";
			//设备号   非必输
			String device_info="";
			//随机数 
			String nonce_str = strReq;
			//商品描述
			//String body = describe;
			
			//商品描述根据情况修改
			String body = Var.get("WechatPayMentTitle");
			//附加数据
			String attach = userId;
			//商户订单号
		
//			int intMoney = Integer.parseInt(money);
			long intMoney = VirementManager.changeY2F(money);
			//总金额以分为单位，不带小数点
			
			//订单生成的机器 IP
			String spbill_create_ip = request.getRemoteAddr();
			//订 单 生 成 时 间   非必输
//			String time_start ="";
			//订单失效时间      非必输
//			String time_expire = "";
			//商品标记   非必输
//			String goods_tag = "";
			
			//这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
			
//			String notify_url = "http://yuebin616.iask.in/AmRes/com.am.frame.payment.impl.webApi.WePaymentNotifyWebApi.do";
			
			String trade_type = "APP";
			String openid = openId;
			//非必输
//			String product_id = "";
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appid);  
			packageParams.put("mch_id", mch_id);  
			packageParams.put("nonce_str", nonce_str);  
			packageParams.put("body", body);  
			packageParams.put("attach", attach);  
			packageParams.put("out_trade_no", out_trade_no);  
			
			
			//这里写的金额为1 分到时修改
//			packageParams.put("total_fee", "1");  
			packageParams.put("total_fee", intMoney+"");  
			packageParams.put("spbill_create_ip", spbill_create_ip);  
			packageParams.put("notify_url", notify_url);  
			
			packageParams.put("trade_type", trade_type);  
			packageParams.put("openid", openid);  

			RequestHandler reqHandler = new RequestHandler(request, response);
			reqHandler.init(appid, appsecret, partnerkey);
			
			String sign = reqHandler.createSign(packageParams);
			String xml="<xml>"+
					"<appid>"+appid+"</appid>"+
					"<mch_id>"+mch_id+"</mch_id>"+
					"<nonce_str>"+nonce_str+"</nonce_str>"+
					"<sign>"+sign+"</sign>"+
					"<body><![CDATA["+body+"]]></body>"+
					"<attach>"+attach+"</attach>"+
					"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
					"<attach>"+attach+"</attach>"+
					//TODO 金额，这里写的1 分到时修改
//					"<total_fee>"+1+"</total_fee>"+
					"<total_fee>"+intMoney+"</total_fee>"+
					"<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>"+
					"<notify_url>"+notify_url+"</notify_url>"+
					"<trade_type>"+trade_type+"</trade_type>"+
					"<openid>"+openid+"</openid>"+
					"</xml>";
			
			log.info("微信支付，支付数据信息："+xml);
			String allParameters = "";
			try {
				allParameters =  reqHandler.genPackage(packageParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
			Map<String, Object> dataMap2 = new HashMap<String, Object>();
			String prepay_id="";
			try {
				new GetWxOrderno();
				prepay_id = GetWxOrderno.getPayNo(createOrderURL, xml);
				if(prepay_id.equals("")){
					request.setAttribute("ErrorMsg", "统一支付接口获取预支付订单出错");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			SortedMap<String, String> finalpackage = new TreeMap<String, String>();
			String appid2 = appid;
			String timestamp = Sha1Util.getTimeStamp();
			String nonceStr2 = nonce_str;
			String prepay_id2 = "prepay_id="+prepay_id;
			String packages = prepay_id2;
			finalpackage.put("appId", appid2);  
			finalpackage.put("timeStamp", timestamp);  
			finalpackage.put("nonceStr", nonceStr2);  
			finalpackage.put("package", packages);  
			finalpackage.put("signType", "MD5");
			String finalsign = reqHandler.createSign(finalpackage);
			if(prepay_id!="")
			{
				resouls.put("prepay_id", prepay_id);
				resouls.put("appid", appid);
				resouls.put("finalsign", finalsign);
				resouls.put("msg","获取统一支付单号成功");
				resouls.put("retcode","0");
				String usql ="update mall_trade_detail set ty_pay_id='"+prepay_id+"' where id = '"+out_trade_no+"'";
				DBManager db = new DBManager();
				db.execute(usql);
			}else
			{
				resouls.put("msg","获取统一支付单号失败");
				resouls.put("retcode","9999");
			}	
        return resouls;  
    }  
}
