package com.am.frame.state.distAction;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 5=接受分配
 * 
 * 说明：分配已经确认，订单分配完成
 * 下一状态值：5
 * 处理过程：
 * 0、设置OrderState=订单状态值，DistState=下一状态值；
 */
public class AcceptAssignmentDistStateAction implements IStateFlow {
	
	
	private final static String ORDER_STATE_KEY="订单状态值";
	
	private final static String NEXT_DIST_STATE="下一状态值";
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			DB db=DBFactory.getDB();
			
			result=StateFlowManager.getInstance().updateStateData(db, ofp);
			
			//订单状态值
			String orderStateValue=ofp.paramList.getValueOfName(ORDER_STATE_KEY);
			//下一个派单状态值
			String distState=ofp.paramList.getValueOfName(NEXT_DIST_STATE);
			
			//订单ID
			String orderId=ofp.orderId;
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			//获取派单ID
			String findDistSQL="SELECT * FROM mall_commoditydistribution WHERE orderid=? ";
			
			MapList map=db.query(findDistSQL,orderId,Type.VARCHAR);
			
			//派单ID
			String distId="";
			
			if(!Checker.isEmpty(map)){
				distId=map.getRow(0).get("id");
			}
			
			Map<String,String> otherParams=new HashMap<String,String>();
			//设置订单状态---
			if(!"5".equals(orderStateValue)){
				StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),orderStateValue,orderId,otherParams);
			}
			
			//设置派单状态---
			StateFlowManager.getInstance().setNextState(commodity.getDistStateID(),distState,distId,otherParams);
			
			
		}catch(Exception e){
			try{
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
				
			}catch(JSONException je){
				je.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return result.toString();
	}

}
