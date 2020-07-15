package com.am.app_plugins_common.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.PaymentUniformOrderManager;
import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.transactions.pay.PayManager;
import com.fastunit.jdbc.DB;

/***
 * 支付Service
 * 
 * @author yuebin
 *
 */
public class PaymentService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 平台支付接口
	 * @param platform 平台 平台类型  1 移动端；  2 pc端
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param memberId
	 *            支付社员机构id
	 * @param outAccountCode
	 *            出账账号Code
	 * @param pay_id
	 *            支付ID
	 * @param pay_money
	 *            支付金额，单位元
	 * @param account_type
	 *            账户类型 1:系统账户 ; 2:支付宝 ;3:微信; 4:银联
	 * @param business
	 *            业务参数，必填项：orders，
	 * @param outremakes
	 *            出账备注
	 * @param db
	 * @return
	 */
	/**
	 * @param request   HttpServletRequest
	 * @param response HttpServletResponse
	 * @param memberId memberId
	 * @param outAccountCode 出账账号编号
	 * @param pay_id  支付ID
	 * @param pay_money 支付金额，单位元
	 * @param account_type  支付类型,支付类型  1 支付 2 充值  
	 * @param business   business
	 * @param outremakes outremakes
	 * @param inAccountCode  入账账号编码 转账时有
	 * @param pay_type   支付类型,支付类型  1 支付 2 充值
	 * @param inremakes
	 * @param commodityName 商品名称 request.getPrams("body")
	 * @param attchStr  attchStr  request.getPrams("attchStr")
	 * @param openid    request.getPrams("openid")
	 * @param db DB
	 * @return
	 * @throws Exception
	 */
	public JSONObject amBdpPayment(HttpServletRequest request,
			HttpServletResponse response,
			String memberId,
			String outAccountCode,
			String pay_id, 
			String pay_money,
			String account_type,
			String business,
			String outremakes,
			String inAccountCode,
			String pay_type,
			String inremakes,
			String commodityName,
			String attchStr,
			String openid,
			DB db) throws Exception {

		logger.info("memberId:" + memberId);
		logger.info("outAccountCode:" + outAccountCode);
		logger.info("pay_id:" + pay_id);
		logger.info("pay_money:" + pay_money);
		logger.info("account_type:" + account_type);
		logger.info("business:" + business);

		JSONObject paramsObj;
		//引入支付管理类
		PayManager payManager = new PayManager();

		JSONObject resultJson = new JSONObject();

		// 定义统一下单接口，在系统账户类型表（mall_system_account_class）增加统一下单接口实现类,增加退款实现类
		// 系统账户实现一个
		// 微信实现一个
		// 通用实现一个

		if ("1".equals(account_type)) {
			// 系统账户 支付
			resultJson = payManager.excunte(memberId, outAccountCode, pay_money, pay_id, business, outremakes);
		} else {
			paramsObj = new JSONObject();
			paramsObj.put("member_id", memberId);
			paramsObj.put("out_account_code", outAccountCode + "");
			paramsObj.put("pay_id", pay_id);
			paramsObj.put("pay_money", pay_money);
			paramsObj.put("business", business);
			paramsObj.put("outremakes", outremakes + "");

			PaymentUniformOrderManager paymentUniformOrderManager = new PaymentUniformOrderManager();
			
			PaymentRequestEntity requestEntity=new PaymentRequestEntity();
			
			// 账户类型  1 系统账户 2 支付宝 3 微信 4 银联   request.getParameter("account_type");
			requestEntity.setAccountType(account_type); 
			//业务参数   request.getParameter("business");
			requestEntity.setBusiness(business);
			//支付ID   request.getParameter("pay_id");
			requestEntity.setPayId(pay_id);
			//用户id  //request.getParameter("memberId");
			requestEntity.setMemberId(memberId);
			// 出账账户code request.getParameter("outAccountCode");
			requestEntity.setOutAccountCode(outAccountCode);
			//支付金额  request.getParameter("pay_money");
			requestEntity.setPayMoney(pay_money);
			//出账描述   request.getParameter("outremakes");
			requestEntity.setOutRemakes(outremakes);
			//入账账户code  request.getParameter("inAccountCode");
			requestEntity.setInAccountCode(inAccountCode);
			
			//支付类型  1 支付 2 充值   request.getParameter("pay_type");
			requestEntity.setPayType(pay_type);
			//入账描述 request.getParameter("inremakes");
			requestEntity.setInrRemakes(inremakes);
			
			//1,获取订单信息  request.getParameter("commodityName");
			requestEntity.setCommodityName(commodityName);
			
			//request.getParameter("attchStr");
			requestEntity.setAttchStr(attchStr);
			
			//request.getParameter("openid");
			requestEntity.setOpenid(openid);
			
			requestEntity.setRequest(request);
			requestEntity.setResponse(response); 
			
			//平台 平台类型  1 移动端；  2 pc端
			resultJson = paymentUniformOrderManager.doPaymentUniformOrder(requestEntity);
			
		}

		if (resultJson == null) {
			resultJson=new JSONObject();
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "系统异常,操作失败");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return resultJson;
	}
	
	
}
