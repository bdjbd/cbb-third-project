package com.am.frame.other.taskInterface.impl.zyb.servlet.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
//import com.am.qmyx.orders.OrdersCommissionCalculationImpl;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * 智游宝订单完成通知
 
 我们系统会以订单完结时，回调下订单方系统。
当取消订单时：
回调
url?order_code={orderCode}&status=cancel&checkNum=0&returnNum=5&total=5&sign=md5(order_code={orderCode}{privateKey})
检票完成：
url?order_code={orderCode}&status=success&checkNum=1&returnNum=5&total=10&sign=md5(order_code={orderCode}{privateKey})
你们提供url，我们配置到系统里
privateKey :企业码
如果你们参数名叫abc 则例如 
url?abc={orderCode}&status={success/cancel}&checkNum=1&returnNum=5&total=10&sign=md5(abc={orderCode}{privateKey})
状态	status	String	cancel/取消:success/完成
检票数量	checkNum	String	
退票数量	returnNum	String	
总数量	total	String	
第三方订单号	order_code	String	
私钥	privateKey 	String	
签名	sign	String	

订单完结的取消通知，是整个订单全部取消，没有检票我们通知你们
退票通知是你们发起退票，景区最终审核通过我们通知你们
 * 
 */
public class ZybOrderFinishWebAPi extends ZybAbstraceWebApiService {

	@Override
	protected String processBusess(DB db, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		String status=request.getParameter("status");//状态	status	String	cancel/取消:success/完成
		String checkNum=request.getParameter("checkNum");//检票数量	checkNum	String	
		String returnNum=request.getParameter("returnNum");//退票数量	returnNum	String	
		String total=request.getParameter("total");//总数量	total	String	
		String orderCode=request.getParameter("order_code");//第三方订单号	order_code	String	
		String privateKey=request.getParameter("privateKey");//私钥	privateKey 	String	
		String sign=request.getParameter("sign");//签名	sign	String	
		
		//判断第三方订单号是否有，如果不存在则获取order_no
		if(Checker.isEmpty(orderCode))
		{
			orderCode = request.getParameter("order_no");
		}
		
		
		//更新订单使用情况
		String updateSQL="UPDATE mall_MemberOrder SET used_number=?,effective_num=COALESCE(effective_num,0)-?,return_num=?"
				+ ",b2b_last_check_in_time=now() WHERE id=?  ";
		
		db.execute(updateSQL,new String[]{
				checkNum,checkNum,returnNum,orderCode
		},new int[]{
				Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR
		});
		
		
		if("cancel".equalsIgnoreCase(status)){
			//订单取消，整个订单全部取消
			
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderCode, db);
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			Map<String,String> otherParam=new HashMap<String,String>();
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"32011");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),order.getOrderState(),order.getId(),otherParam);
			
			
		}else if("success".equalsIgnoreCase(status)){
			//订单完成
			
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderCode, db);
			
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			Map<String,String> otherParam=new HashMap<String,String>();
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"3208");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),order.getOrderState(),order.getId(),otherParam);
			
			//执行佣金比例分配（汽车公社无此功能，已注释掉）
			//OrdersCommissionCalculationImpl.getIstance().execute(db, order.getId());
			
		}else if(Integer.parseInt(checkNum)+Integer.parseInt(returnNum) == Integer.parseInt(total))
		{
			logger.info("执行修改状态数据");
			MemberOrder order=new OrderManager().getMemberOrderById(orderCode, db);
			
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			Map<String,String> otherParam=new HashMap<String,String>();
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"3208");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),order.getOrderState(),order.getId(),otherParam);
			
			//执行佣金比例分配（汽车公社无此功能，已注释掉）
			//OrdersCommissionCalculationImpl.getIstance().execute(db, order.getId());
		}
		
		return "success";
	}

}
