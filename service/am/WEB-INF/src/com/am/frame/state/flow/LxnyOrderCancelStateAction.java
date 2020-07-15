package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 理性农业订单取消订单业务
 * @author xianlin
 *2017年03月25日
 *1、需要判断订单是否在已付款、待分配时可以取消订单
 *2、取消后，执行删除订单操作，并且将付款金额退还至原有支付账户
 */

public class LxnyOrderCancelStateAction extends DefaultOrderStateAction {

	private static final Logger log = LoggerFactory.getLogger(LxnyOrderCancelStateAction.class);
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try {
			//获取当前订单状态
			String currentStateValue=ofp.stateValue;
			
			//3=已付款，31=待分配，2=已下单,4=已分配，未配送
			if("3".equals(currentStateValue)
			|| "31".equals(currentStateValue)
			|| "4".equals(currentStateValue))
			{
				//执行后续流程
				result=new JSONObject(super.execute(ofp));
				
				//订单id
				String orderId = ofp.orderId;
				//执行退款操作
				refund(orderId);
				
			}else if("2".equals(currentStateValue))
			{
				result=new JSONObject(super.execute(ofp));
			}else
			{
				log.info("只有订单状态为3,31,4的订单才可以取消订单，目前订单状态为 "+ofp.stateValue);
				throw new Exception("只有订单状态为3,31,4的订单才可以取消订单，目前订单状态为 "+ofp.stateValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/**
	 * 根据订单id查询相应交易记录，将支付金额退换至原账户
	 * @param orderid
	 * @throws JSONException 
	 */
	public void refund(String orderid) throws JSONException
	{
		
		DB db = null;
		try {
			db = DBFactory.getDB();
			
			
			//查询交易记录信息
			String queryTradeSQL ="SELECT * FROM mall_trade_detail WHERE business_json like '%"+orderid+"%';";
			MapList tradeMaplist = db.query(queryTradeSQL);
			
			//查询订单信息
			String queryOrderSQL = "SELECT * FROM mall_memberorder WHERE id='"+orderid+"'";
			MapList orderMaplist = db.query(queryOrderSQL);
			
			
			if(tradeMaplist.size()>0 && orderMaplist.size()>0)
			{
				//交易记录会员id
				String trade_member_id = tradeMaplist.getRow(0).get("member_id");
				//订单会员id
				String order_member_id = orderMaplist.getRow(0).get("memberid");
				
				if(trade_member_id.equals(order_member_id))
				{
					//支付金额
					Double total_money = tradeMaplist.getRow(0).getDouble("trade_total_money",0)/100;
					//支付账户id
					String pay_account_id = tradeMaplist.getRow(0).get("account_id");
					//支付账户类型id
					String pay_sa_class_id = tradeMaplist.getRow(0).get("sa_class_id");
					//商品名称
					String commdity_name = orderMaplist.getRow(0).get("commodityname");
					
					VirementManager vm = new VirementManager();
					
					String queryInAccountSQL = "SELECT * FROM mall_system_account_class WHERE id = '"+pay_sa_class_id+"'";
					
					//入账账户code
					String inAccountCode = db.query(queryInAccountSQL).getRow(0).get("sa_code");
					//入账描述
					String iremakers = "购买商品退款金额"+total_money+"元，退款商品名称："+commdity_name;
					
					vm.execute(db, "", trade_member_id, ""
							, inAccountCode, total_money+""
							, iremakers, "", "", false);
					
					log.info("退款用户id:"+trade_member_id+",退款商品名称:"+commdity_name+",退款金额:"+total_money+"元。");
					
				}
			}
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				db.close();
			} catch (JDBCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
