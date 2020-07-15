package com.am.frame.payment.impl.weipay;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.IPaymentRefund;
import com.am.frame.payment.impl.weipay.utils.RequestHandler;
import com.am.frame.payment.impl.weipay.utils.Sha1Util;
import com.am.frame.payment.impl.weipay.utils.TenpayUtil;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author 岳斌
 * @create 2016年7月1日
 * @version 
 * 说明:<br />
 * 微信公众账号支付退款接口
 */
public class MPWeiChartPaymentRefund implements IPaymentRefund 
{	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public JSONObject execute(HttpServletRequest request,
			HttpServletResponse response, JSONObject params)
	{

		
		DB db=null;
		
		String appid, appsecret, partnerkey;
		appid=Var.get("weiChartAppId");//微信公众账号APPID
		appsecret=Var.get("weiChartAppSecret");//微信公众账号appsecret
		partnerkey=Var.get("MpWeiPayAPIKey");//微信公众账号partnerkey
		
		try{
			db=DBFactory.newDB();
			
			//1，获取订单ID
			String orderId=params.getString("orderid");
			String payId=params.getString("pay_id");
			String totalprice=params.getString("totalprice");
			String reason=params.getString("reason");
			String stateValue=params.getString("stateValue");
			String failureStateVaule=params.getString("failureStateVaule");
	
	        
	        String out_trade_no = payId;

	        String total_fee = totalprice;
	        
	        
			String userId = ""; 
			
			String money = total_fee;
			
			String code = "";
			
			//金额转化为分为单位
			float sessionmoney = Float.parseFloat(money);
			
			String finalmoney = String.format("%.2f", sessionmoney);
			
			finalmoney = finalmoney.replace(".", "");
			
			
				String currTime = TenpayUtil.getCurrTime();
				//8位日期
				String strTime = currTime.substring(8, currTime.length());
				//四位随机数
				String strRandom = TenpayUtil.buildRandom(4) + "";
				//10位序列号,可以自行调整。
				String strReq = strTime + strRandom;
				
				
				//微信公众账号对应商户号
				String mch_id =Var.get("mch_id");
				//子商户号  非必输
				//String sub_mch_id="";
				//设备号   非必输
				String device_info="";
				//随机数 
				String nonce_str = strReq;
				//商品描述
				//String body = describe;
				
				//商品描述根据情况修改
				String body = "商品退款";
				//附加数据
				String attach = userId;
				//商户订单号
			
				int intMoney = Integer.parseInt(finalmoney);
				
				//总金额以分为单位，不带小数点
				//订单生成的机器 IP
				//订 单 生 成 时 间   非必输
//				String time_start ="";
				//订单失效时间      非必输
//				String time_expire = "";
				//商品标记   非必输
//				String goods_tag = "";
				//非必输
//				String product_id = "";
				SortedMap<String, String> packageParams = new TreeMap<String, String>();
				packageParams.put("appid",Var.get("weiChartAppId")); //微信公众账号APPID
				packageParams.put("mch_id", mch_id);  
				packageParams.put("nonce_str", nonce_str);  
				packageParams.put("out_trade_no", out_trade_no);  
				
				//这里写的金额为1 分到时修改
				packageParams.put("total_fee", "1");
				packageParams.put("refund_fee", "1");
				packageParams.put("op_user_id",mch_id);
				packageParams.put("out_refund_no", out_trade_no);

				RequestHandler reqHandler = new RequestHandler(request, response);
				reqHandler.init(appid, appsecret, partnerkey);
				
				String sign = reqHandler.createSign(packageParams);
				String xml="<xml>"+
						"<appid>"+appid+"</appid>"+
						"<mch_id>"+mch_id+"</mch_id>"+
						"<nonce_str>"+nonce_str+"</nonce_str>"+
						"<sign>"+sign+"</sign>"+
						"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
						"<out_refund_no>"+out_trade_no+"</out_refund_no>"+
						"<op_user_id>"+mch_id+"</op_user_id>"+
						//金额，这里写的1 分到时修改
						"<total_fee>"+1+"</total_fee>"+
						"<refund_fee>"+1+"</refund_fee>"+
						"</xml>";
				
				System.out.println(xml);
				
				logger.info("微信公众账号支付退款 ,退款信息XML:"+xml);
				
				String allParameters = "";
				try {
					allParameters =  reqHandler.genPackage(packageParams);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String createOrderURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
				Map<String, Object> dataMap2 = new HashMap<String, Object>();
				String date="";
				try {
					date=doRefund(createOrderURL, xml);
//					date = new GetWxOrderno().getPayNo(createOrderURL, xml);
					if(date.equals(""))
					{
						request.setAttribute("ErrorMsg", "统一我想退单接口获取预支付订单出错");
					}else{
						processRefundNotify(db,payId,params);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				SortedMap<String, String> finalpackage = new TreeMap<String, String>();
				String appid2 = appid;
				String timestamp = Sha1Util.getTimeStamp();
				String nonceStr2 = nonce_str;
				finalpackage.put("appId", appid2);  
				finalpackage.put("timeStamp", timestamp);  
				finalpackage.put("nonceStr", nonceStr2);  
				finalpackage.put("signType", "MD5");
				String finalsign = reqHandler.createSign(finalpackage);
	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	
	
	/**
	 * 订单处理成功后，回调 
	 */
	@Override
	public JSONObject processRefundNotify(DB db, String payId,
			JSONObject params) {
		//1，订单支付超时和订单退款成功都会调用此接口 
		//2，如果订单当状态为2，订单确实待支付，则不处理，如果是其他状态，则设置订单状态为退款成功
		
		try {
				//订单信息
				MemberOrder order=new OrderManager().getMemberOrderPayId(payId, db);
				
				String orderstate=order.getOrderState();
				
				if("11".equals(orderstate)){
					//如果是订单退款状态，则调用到下一个状态 成功状态
					//订单对应的商品信息
					Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
					Map<String,String> otherParam=new HashMap<String,String>();
					otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"1112");
					StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);
				}
				
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	public static String doRefund(String url,String data) throws Exception {
		 String jsonStr =null;
		/**
    	 * 注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的
    	 */
		String apiClientCertPath=Var.get("MPWeiChartApiClientCertPath");
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(apiClientCertPath));//P12文件目录
        try {
        	/**
        	 * 此处要改
        	 * */
            keyStore.load(instream, "1366147002".toCharArray());//这里写密码..默认是你的MCHID
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        /**
    	 * 此处要改
    	 * */
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, "1366147002".toCharArray())//这里也是写密码的  
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        try {
        	HttpPost httpost = new HttpPost(url); // 设置响应头信息
        	httpost.addHeader("Connection", "keep-alive");
        	httpost.addHeader("Accept", "*/*");
        	httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        	httpost.addHeader("Host", "api.mch.weixin.qq.com");
        	httpost.addHeader("X-Requested-With", "XMLHttpRequest");
        	httpost.addHeader("Cache-Control", "max-age=0");
        	httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
    		httpost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
                HttpEntity entity = response.getEntity();

                jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                EntityUtils.consume(entity);
            }catch(Exception e){
            	e.printStackTrace();
            } 
            finally {
                httpclient.close();
            }
            return jsonStr;
    }
	
	
}