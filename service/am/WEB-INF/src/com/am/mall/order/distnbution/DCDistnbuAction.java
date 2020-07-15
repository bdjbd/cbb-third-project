package com.am.mall.order.distnbution;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月26日
 * @version 
 * 说明:<br />
 * 
 * 配送中心订单接单<br />
 * 1，先检查订单分配的接单机构是否为当前机构，如果不是，不能接单，如果是，则接单；
 * DistState=5 接单，DistState=6 拒绝接单；
 * */
public class DCDistnbuAction extends DefaultAction{
	
	@Override
	public void doAction(DB db,ActionContext ac) throws JDBCException{
		
		//接单，拒绝接单
		String distState = ac.getRequestParameter("recv");
		//订单ID
		String orderId=ac.getRequestParameter("id");
		//组织机构
		String orgCode=ac.getVisitor().getUser().getOrgId();
		
		
		String querySQL="SELECT acceptorderid,orderid FROM mall_CommodityDistribution WHERE orderid=? ";
		
		MapList orderDcMap=db.query(querySQL, orderId, Type.VARCHAR);
		
		//接单机构ID
		String acceptOrgId="";
		if(!Checker.isEmpty(orderDcMap)){
			acceptOrgId= orderDcMap.getRow(0).get("acceptorderid");
		}
		
		//检查当前登录所在机构和订单分配的机构
		String getAcceptIdSQL="SELECT id,orderid FROM mall_CommodityDistribution "
				+ " WHERE orderid=? AND acceptorderid=? ";
		
		MapList map=db.query(getAcceptIdSQL,
				new String[]{orderId,orgCode},
				new int[]{Type.VARCHAR,Type.VARCHAR});
		
		if(!Checker.isEmpty(map)&&orgCode.equals(acceptOrgId)){
			String acceptId=map.getRow(0).get("id");
			
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			//获取派单ID
			
			Map<String,String> otherParam=new HashMap<String,String>();
			//判断接单，拒绝接单
			if("5".equals(distState)){
				
				otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"301");
				otherParam.put(OrderFlowParam.ACCEPT_ID, acceptId);
				
				StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"30",order.getId(), otherParam);
			}else{
				
				otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"302");
				otherParam.put(OrderFlowParam.ACCEPT_ID,acceptId);
				
				StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"30",order.getId(), otherParam);
			}
			ac.getActionResult().setSuccessful(true);
			try {
				Ajax ajax = new Ajax(ac);
				ajax.addScript("location.reload()");
				ajax.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}else{
//			ac.getActionResult().setSuccessful(false);
//			ac.getActionResult().addErrorMessage("接单失败，您的用户所在机构不是门店中的用户");
			
			try {
				Ajax ajaxx = new Ajax(ac);
				ajaxx.addScript("alert('操作失败，您所在机构不能处理此订单！');location.reload()");
				ajaxx.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
