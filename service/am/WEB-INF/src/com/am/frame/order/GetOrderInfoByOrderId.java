package com.am.frame.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />
 * 根据订单id获取订单之的支付信息
 * 主要信息
 * 1，订单信息
 * 2，订单子表信息
 * 3，商品支付账号信息
 * 
 */
public class GetOrderInfoByOrderId implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		//订单ID
		String orderId=request.getParameter("orderId");
		
		DBManager dbManager=new DBManager();
		
		//1,活动订单ID
		//2，获取订单支付信息
		//3，订单支付信息，
		//4，获取订单支付账号信息，多个账号使用,分隔
		String querOrderInfoSQL="SELECT * FROM mall_memberorder WHERE id='"+orderId+"' ";
		
		//订单信息
		JSONArray ordersInfo=dbManager.mapListToJSon(dbManager.query(querOrderInfoSQL));
		//查询订单支付账号信息
		String queryComdipPayMethodAccountSQL=
				"SELECT sac.sa_code FROM mall_pay_suppor AS ps "+
				"	LEFT JOIN mall_pay_suppor_account_info AS psc ON ps.id=psc.ps_id  "+
				"	LEFT JOIN mall_system_account_class AS sac ON sac.id=psc.account_id "+
				"	WHERE ps.id IN ( "+
				"	SELECT comd.pay_meoth  "+
				"		FROM mall_memberorder AS od  "+
				"	LEFT JOIN mall_commodity AS comd ON comd.id=od.commodityid  "+
				"	WHERE od.id='"+orderId+"' "+
				"	) ORDER BY  psc.create_time ";
		
		JSONArray payAccountInfo=dbManager.mapListToJSon(dbManager.query(queryComdipPayMethodAccountSQL));
		
		try {
			
			result.put("ORDERINFO", ordersInfo);
			result.put("PAY_ACCOUNT_INFO", payAccountInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}

}
