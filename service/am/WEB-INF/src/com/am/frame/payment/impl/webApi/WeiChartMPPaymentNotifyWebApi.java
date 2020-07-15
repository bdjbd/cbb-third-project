package com.am.frame.payment.impl.webApi;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.payment.impl.weipay.utils.RequestHandler;
import com.am.frame.payment.impl.weipay.utils.Sha1Util;
import com.am.frame.transactions.callback.BusinessCallBack;
import com.am.tools.Utils;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;



/**
 * 微信公众平台支付回调接口
 * @author yuebin
 *
 *微信公众平台支付回调接口。
 *
 */
public class WeiChartMPPaymentNotifyWebApi implements IWebApiService
{
	

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{ 
		//SUCCESS 成功  失败 FAIL
		String rcode =  request.getParameter("return_code");
		Map map = null;
		try {

			map = Utils.parseXMLMsg(request);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String transaction_id = map.get("transaction_id").toString();  //微信支付订单号
		String key=Var.get("MpWeiPayAPIKey"); //商户key
		String nonce_str = map.get("nonce_str").toString(); //随机字符串
		String bank_type = map.get("bank_type").toString(); //付款银行
		String openid = map.get("openid").toString();  //用户标识
		String sign = map.get("sign").toString();
		String fee_type = map.get("fee_type").toString();
		String mch_id = map.get("mch_id").toString();
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
			ty_pay_id = list.getRow(0).get("ty_pay_id");
		
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(appid, Var.get("weiChartAppSecret"), key);

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
		
//		if(finalsign.equals(sign))
//		{
			
			//处理数据库逻辑
			//注意交易单不要重复处理
			//注意判断返回金额
			
			if("SUCCESS".equals(result_code)){
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

	
				}
			
//			}
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
