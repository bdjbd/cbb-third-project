package com.am.frame.payment.impl.webApi;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.impl.weipay.utils.RequestHandler;
import com.am.frame.payment.impl.weipay.utils.Sha1Util;
import com.am.frame.transactions.callback.BusinessCallBack;
import com.am.tools.Utils;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;



/**
 * 微信支付回调接口
 * @author mac
 *
 */
public class WePaymentNotifyWebApi implements IWebApiService
{
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	private String appid = "wx476afb93aca1b0cf";
	private String appsecret = "13c59148d96b839bff3df0f35f01d54b";
	private String partner = "1487235752";
	private String key = "67FE56A17CAC4B4D4F65E2D506B3F205";

	private boolean isQrcodePay=false;
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{ 
		
		appid = Var.get("wepay_mobile_appid");
		appsecret = Var.get("wepay_mobile_appsecret");
		partner = Var.get("wepay_mobile_partner");
		key = Var.get("wepay_mobile_key");
		
		//SUCCESS 成功  失败 FAIL
		String rcode =  request.getParameter("return_code");
		Map map =null;
		try {

			map = Utils.parseXMLMsg(request);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for(Object e: map.keySet()){
			logger.info("微信回调接口参数:"+e.toString()+"="+map.get(e));
		}
		
		
		String transaction_id = map.get("transaction_id").toString();  //微信支付订单号
		String key="67FE56A17CAC4B4D4F65E2D506B3F205"; //商户key
		String nonce_str = map.get("nonce_str").toString(); //随机字符串
		String bank_type = map.get("bank_type").toString(); //付款银行
		String openid = map.get("openid").toString();  //用户标识
		String sign = map.get("sign").toString();
		String fee_type = map.get("fee_type").toString();
		String mch_id = map.get("mch_id").toString();
		
		//交易金额
		//交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。
		String cash_fee = map.get("cash_fee").toString();
		
		String out_trade_no = map.get("out_trade_no").toString();
		String appid = map.get("appid").toString();
		String total_fee = map.get("total_fee").toString();
		String trade_type = map.get("trade_type").toString();
		String result_code = map.get("result_code").toString();
		String time_end = map.get("time_end").toString();
		String is_subscribe = map.get("is_subscribe").toString();
		 
		String ty_pay_id = "";
		DB db=null;
		MapList list  = null;
		try {
			db = DBFactory.newDB();
			list= db.query("select *  from mall_trade_detail where id='"+out_trade_no+"'");
			
			if(Checker.isEmpty(list)){
				//微信扫描支付，订单编号为nonce_str字段
				out_trade_no=nonce_str;
				list= db.query("select *  from mall_trade_detail where id='"+nonce_str+"'");
				isQrcodePay=true;
			}
			
			if(Checker.isEmpty(list)){
				logger.info("未找到对应的订单");
				//微信扫描支付，订单编号为nonce_str字段
				
				return "";
			}
			
			ty_pay_id = list.getRow(0).get("ty_pay_id");
		
			RequestHandler reqHandler = new RequestHandler(request, response);
			reqHandler.init(appid, appsecret, key);
	
			SortedMap<String, String> finalpackage = new TreeMap<String, String>();
			String appid2 = appid;
			String timestamp = Sha1Util.getTimeStamp();
			String nonceStr2 = nonce_str;
			String prepay_id2 = "prepay_id="+ty_pay_id;
			String packages = prepay_id2;
			finalpackage.put("appId", appid2);  
			finalpackage.put("timeStamp", timestamp);  
			finalpackage.put("nonceStr", nonceStr2);  
			finalpackage.put("package", packages);  
			finalpackage.put("signType", "MD5");
			String finalsign = reqHandler.createSign(finalpackage);
			
			
			logger.info("微信支付，参数签名：finalsign="+finalsign+"\n sign:"+sign);
			logger.info("继续执行，接下来判断交易是否成功，如果成功执行回调类");
//			logger.info("微信验证签名:"+map.get("attach").toString());
//			if(finalsign.equals(sign)||isQrcodePay)
//			{
//			if(list.getRow(0).get("payment_callback_verification").equals(map.get("attach").toString()))
//			{	  
				//处理数据库逻辑
				//注意交易单不要重复处理
				//注意判断返回金额
				
				if(result_code.equals("SUCCESS")){
					//交易成功 ，交易成功，且可对该交易做操作，如：多级分润、退款等。
					// 交易成功且结束，即不可再做任何操作。
					//，判断退款还是支付
						//支付成功
						BusinessCallBack businessCallBack = new BusinessCallBack();
	
	//					businessCallBack.callBack(out_trade_no, db,"1");
						Map<String,String> params=new HashMap<String,String>();
						params.put(BusinessCallBack.CASH_FREE, cash_fee);
						params.put(BusinessCallBack.OTHER_ORDER_CODE, transaction_id);
						
						businessCallBack.callBack(out_trade_no, db,params,"1");
//					}
				}else{
					logger.info("签名验证失败!");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally
			{
				if(db!=null)
				{
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}	
			}
			
		return "SUCCESS";
	}
}
