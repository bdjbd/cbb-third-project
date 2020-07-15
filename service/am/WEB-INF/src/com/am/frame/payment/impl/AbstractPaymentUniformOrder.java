package com.am.frame.payment.impl;

import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.IPaymentUniformOrder;
import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.webapi.db.DBManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月2日
 * @version 
 * 说明:<br />
 * 统一下单 抽象接口，完成提交数据保存到数据库，调用回调
 * 
 */
public abstract class AbstractPaymentUniformOrder implements
		IPaymentUniformOrder {

	
	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 * 参数集合
	 * memberId:f193be7d-d9af-4fec-b3d7-4227915e18e6 会员ID
	 * inAccountCode:  入账账号编码 转账时有
	 * 	outAccountCode:UNIONPAY_ACCOUNT_MODE  出帐账号编码
	 * 	pay_id:F7200D38-42EE-4BB0-A343-4D78E38D7EE7 支付ID
	 * pay_money:44.8  支付金额，单元
	 * pay_type:1           支付类型,支付类型  1 支付 2 充值
	 * account_type:  账户类型  1 系统账户 2 支付宝 3 微信 4 银联
	 * business:{
	 * 						"payment_id":"F7200D38-42EE-4BB0-A343-4D78E38D7EE7",
	 * 						"memberid":"f193be7d-d9af-4fec-b3d7-4227915e18e6",
	 * 						"orders":"7DB39221647A452FB3606F3A0D4B79ED,",
	 * 						"paymoney":44.8,
	 * 						"success_call_back":"com.am.frame.order.process.OrderBusinessCallBack"
	 * 			}
	 * inremakes:,502胶   入账描述
	 * outremakes:,502胶   出账描述
	 * platform:1    平台类型  1 移动端  2 pc端
	 * @param request
	 * @param response
	 * @return
	 */
	public JSONObject saveRecord(PaymentRequestEntity request){
		JSONObject result=new JSONObject();
		/**
		 * 用户id
		 */
		String memberId =request.getMemberId() ;//request.getParameter("memberId");
		/**
		 * 出账账户code
		 */
		String outAccountCode =request.getOutAccountCode() ;//request.getParameter("outAccountCode");
		/**
		 * 入账账户code
		 */
		String inAccountCode =request.getInAccountCode() ;//request.getParameter("inAccountCode");
		/**
		 * 支付ID
		 */
		String pay_id =request.getPayId() ;//request.getParameter("pay_id");
		/**
		 * 支付金额
		 */
		String pay_money =request.getPayMoney() ;//request.getParameter("pay_money");
		/**
		 * 支付类型  1 支付 2 充值
		 */
		String pay_type =request.getPayType() ;//request.getParameter("pay_type");
		/**
		 * 账户类型  1 系统账户 2 支付宝 3 微信 4 银联
		 */
		String account_type =request.getAccountType() ;//request.getParameter("account_type");
		/**
		 * 业务参数 
		 */
		String business =request.getBusiness();// request.getParameter("business");
		/**
		 * 入账描述
		 */
		String inremakes =request.getInrRemakes() ;//request.getParameter("inremakes");
		/**
		 * 出账描述
		 */
		String outremakes =request.getOutRemakes() ;//request.getParameter("outremakes");
		/**
		 * 平台类型  1 移动端  2 pc端
		 */
		String platform =request.getPlatform() ;//request.getParameter("platform");
		
		result=processData(memberId, outAccountCode, pay_id, pay_money, business, outremakes);
		
		return result;
	}
	
	
	/**
	 * 
	 * 参数集合
	 * memberId:f193be7d-d9af-4fec-b3d7-4227915e18e6 会员ID
	 * inAccountCode:  入账账号编码 转账时有
	 * 	outAccountCode:UNIONPAY_ACCOUNT_MODE  出帐账号编码
	 * 	pay_id:F7200D38-42EE-4BB0-A343-4D78E38D7EE7 支付ID
	 * pay_money:44.8  支付金额，单元
	 * pay_type:1           支付类型,支付类型  1 支付 2 充值
	 * account_type:  账户类型  1 系统账户 2 支付宝 3 微信 4 银联
	 * business:{
	 * 						"payment_id":"F7200D38-42EE-4BB0-A343-4D78E38D7EE7",
	 * 						"memberid":"f193be7d-d9af-4fec-b3d7-4227915e18e6",
	 * 						"orders":"7DB39221647A452FB3606F3A0D4B79ED,",
	 * 						"paymoney":44.8,
	 * 						"success_call_back":"com.am.frame.order.process.OrderBusinessCallBack"
	 * 			}
	 * inremakes:,502胶   入账描述
	 * outremakes:,502胶   出账描述
	 * platform:1    平台类型  1 移动端  2 pc端
	 * @param request
	 * @param response
	 * @return
	 */
	public JSONObject saveRecord(Map<String,String> params){
		JSONObject result=new JSONObject();
		/**
		 * 用户id
		 */
		String memberId = params.get("memberId");
		/**
		 * 出账账户code
		 */
		String outAccountCode = params.get("outAccountCode");
		/**
		 * 入账账户code
		 */
		String inAccountCode = params.get("inAccountCode");
		/**
		 * 支付ID
		 */
		String pay_id =params.get("pay_id");
		/**
		 * 支付金额
		 */
		String pay_money = params.get("pay_money");
		/**
		 * 支付类型  1 支付 2 充值
		 */
		String pay_type =params.get("pay_type");
		/**
		 * 账户类型  1 系统账户 2 支付宝 3 微信 4 银联
		 */
		String account_type =params.get("account_type");
		/**
		 * 业务参数 
		 */
		String business =params.get("business");
		/**
		 * 入账描述
		 */
		String inremakes =params.get("inremakes");
		/**
		 * 出账描述
		 */
		String outremakes =params.get("outremakes");
		/**
		 * 平台类型  1 移动端  2 pc端
		 */
		String platform =params.get("platform");
		
		result=processData(memberId, outAccountCode, pay_id, pay_money, business, outremakes);
		
		return result;
	}
	
	
	
	/**
	 * 处理支付信息数据
	 * @param memberId
	 * @param outAccountCode
	 * @param pay_id
	 * @param pay_money
	 * @param business
	 * @param outremakes
	 */
	private JSONObject processData(String memberId, String outAccountCode, String pay_id, String pay_money, String business,
			String outremakes) {
		
		JSONObject result=new JSONObject();
		
		String payment_callback_verification_id = UUID.randomUUID().toString();
		
		DBManager dbManager=new DBManager();
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("SELECT acInfo.id AS account_id, sac.id AS sacid                      ");
		querySQL.append("	FROM mall_system_account_class AS sac                             ");
		querySQL.append("	LEFT JOIN mall_account_info AS acInfo ON sac.id=acInfo.a_class_id ");
		querySQL.append("	WHERE sac.sa_code='"+outAccountCode+"'                          ");
		querySQL.append("	AND acInfo.member_orgid_id='"+memberId+"' ");
		
		MapList memberAccountInfo=dbManager.query(querySQL.toString());
		
		if(!Checker.isEmpty(memberAccountInfo)){
			Row row=memberAccountInfo.getRow(0);
			
			
			StringBuilder insertSQL=new StringBuilder();
			
			insertSQL.append("INSERT INTO mall_trade_detail(                                                     ");
			insertSQL.append("            id, member_id, account_id, sa_class_id, trade_total_money,             ");
			insertSQL.append("            rmarks, create_time, trade_type, trade_state,"); 
			insertSQL.append("            business_json, is_process_buissnes,payment_callback_verification)                                    ");
			insertSQL.append("    VALUES ('"+pay_id+"','"+memberId+"','"+row.get("account_id")+"','"+row.get("sacid")+"',cast("+pay_money+"*100 as integer ),                                                        ");
			insertSQL.append("          '"+outremakes+"',now(),1, 1,                                                      ");
			insertSQL.append("            '"+business+"',0,'"+payment_callback_verification_id+"');                                                                  ");
			
			dbManager.execute(insertSQL.toString());
			
		}else{
			logger.info("会员帐号不存在 memberId："+memberId+"\toutAccountCode:"+outAccountCode);
		}
		
		//将支付ID保存到订单信息中
		
		if(business!=null){
			try{
				JSONObject busJson=new JSONObject(business);
				if(busJson.has("orders")){
					//如果包含订单字段
					String orders=busJson.getString("orders");
					OrderManager orderManager=new OrderManager();
					for(String orderId:orders.split(",")){
						orderManager.updatePayId(orderId,pay_id);
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try {
			result.put("API_KEY",payment_callback_verification_id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
				
	}
	
	
	
	

}
