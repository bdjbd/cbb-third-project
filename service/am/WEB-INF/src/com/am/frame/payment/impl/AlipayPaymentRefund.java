package com.am.frame.payment.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.am.frame.payment.IPaymentRefund;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author YueBin
 * @create 2016年7月1日
 * @version 
 * 说明:<br />
 * 支付宝退单接口 
 */
public class AlipayPaymentRefund implements IPaymentRefund 
{	
	/**开发平台APPID**/
	private  String APPID="2016021601145296";
	/**开放平台 公钥***/
	private String public_RSA=
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLwUl3B5fu3zY3SgTa1ng3nE1Y8mpY1mJbYNvsruH7Z+HnX"
			+ "fXo4rz9B57+0QHYgdC2WlaQmklV3+Y4yCdF8GSn6/46QY2iQDApkEcU1WEnK9WGjgG8LVrSy/AC/YjqzsspR"
			+ "1p8Txt7nMM8ePl/wSsqeWRHM6xeQ0T9bhmItBYJWQIDAQAB";
	/**开发平台 支付宝公钥**/
	private String alipy_public_RAS="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkr"
			+ "IvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVm"
			+ "RGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
	/**开发平台 我方私钥**/
	private String public_private_RAS=
						"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMvBSXcHl+7fNjdK"
						+"BNrWeDecTVjyaljWYltg2+yu4ftn4edd9ejivP0Hnv7RAdiB0LZaVpCaSVXf5jjI"
						+"J0XwZKfr/jpBjaJAMCmQRxTVYScr1YaOAbwtWtLL8AL9iOrOyylHWnxPG3ucwzx4"
						+"+X/BKyp5ZEczrF5DRP1uGYi0FglZAgMBAAECgYEAjgj2YmBLvDujepVBnx+EfBec"
						+"H5YVJkqEE/kINgyPAYcgBf4M70QnxCcs4arnI3sS+BsvXuv+lTtYLFrybaGvN9So"
						+"pxMcuumMzCtwU6f3J3anwfgDEkwbKd6yigBmaVUG0zmpSRw+PSVqb9rC13j93jm7"
						+"WzyOV9efH22ZBEuIgMUCQQDyEyVBeCKCWr3imrB4lvqO0hOYe2q+KkxQJq3Y0dFI"
						+"JqRIAbh1yaZey5ExGHzib4ik37g24fSAzzz5M8S0oP2XAkEA13nVwQFmFjQziON/"
						+"STv5pObvOy7EEOPHWIsOVMk+4fPH6D2CrRr6LWCoCnFhe2P9pEA2NGW/fTdzZzpV"
						+"6YrujwJAQlx5RB9Y5n9Vur9JTWWmPmzcrlp3Ara5wOc5lUy/oJXnR04hcTjcgPQx"
						+"U1IxOin1Pa2O5IzA6GbngjFs5U+U9QJACxwSBFCBJfXtDkrEfk8kUPdDMGb53vEP"
						+"jyYkSATURCwgi33YZfp1Ga/ZGii8YPNH2Zch9FOmqtsYDSN52H+8SwJAGgrBUbJo"
						+"zaqs6clvL07CjODEIXlC5DFi9a9xKRIoaxYigvDhEnQuNF+Iy0oegWt4T10hKlHr"
						+"gCdjwl0stF712w==";
	
	@Override
	public JSONObject execute(HttpServletRequest request,
			HttpServletResponse response, JSONObject params)
	{
		
//		params.put("orderid", ofp.orderId);
//		params.put("stateValue", ofp.stateValue);
//		params.put("failureStateVaule", ofp.failureStateVaule);
//		params.put("pay_id",row.get("pay_id"));
//		params.put("memberId",row.get("memberid"));
//		params.put("commodityid",row.get("commodityid"));
//		params.put("mall_class",row.get("mall_class"));
//		params.put("totalprice",row.get("totalprice"));
//		params.put("reason","");
//		params.put("failureStateVaule", ofp.failureStateVaule);
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			//1，获取订单ID
			String orderId=params.getString("orderid");
			String payId=params.getString("pay_id");
			String totalprice=params.getString("totalprice");
			String reason=params.getString("reason");
			String stateValue=params.getString("stateValue");
			String failureStateVaule=params.getString("failureStateVaule");
			
			JSONObject bizContent=new JSONObject();
		
			
			AlipayClient alipayClient = new DefaultAlipayClient(
				"https://openapi.alipay.com/gateway.do",
				APPID,//2088711368642888   2016021601145296
				public_private_RAS,
				"json",
				"UTF-8",
				alipy_public_RAS);
			AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
			
			//测试阶段，退款金额设置为1分钱 
			totalprice="0.01";
			//TODO 设置退款金额 
			
			//"out_trade_no":"20150320010101001",
			//"trade_no":"2014112611001004680 073956707",
			//"refund_amount":200.12,
			//"refund_reason":"正常退款",
			//"out_request_no":"HZ01RF001",
			//"operator_id":"OP001",
			//"store_id":"NJ_S_001",
			//"terminal_id":"NJ_T_001"
			
			
			bizContent.put("out_trade_no",payId);//订单支付时传入的商户订单号,不能和 trade_no同时为空
			bizContent.put("trade_no", "");//支付宝交易号，和商户订单号不能同时为空
			bizContent.put("refund_amount",totalprice);//需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
			bizContent.put("refund_reason",reason);//退款的原因说明
			bizContent.put("out_request_no",payId);//标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
			bizContent.put("operator_id","");
			bizContent.put("store_id","");
			bizContent.put("terminal_id","");
			
			System.out.println(bizContent.toString());
			
			alipayRequest.setBizContent(bizContent.toString());
			
			AlipayTradeRefundResponse alipayResponse = alipayClient.execute( alipayRequest);
			System.out.println(alipayResponse.getBody());
			
//			//订单信息
//			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
//			//订单对应的商品信息
//			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
//			Map<String,String> otherParam=new HashMap<String,String>();
//			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"1112");
//			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);

			
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

	
}
