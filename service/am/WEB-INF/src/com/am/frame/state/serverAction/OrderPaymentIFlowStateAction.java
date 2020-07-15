package com.am.frame.state.serverAction;

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
 * 3 订单已付款未分配 <br />
 * 下一状态值：null <br />
 * 处理过程：<br />
 * 0、设置DistState=4，成功->，失败->1；<br />
 * 1、设置DistState=3;<br />
 * <hr />
 * 调用订单分配接口
 */
public class OrderPaymentIFlowStateAction implements IStateFlow {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			DB db=DBFactory.getDB();
			
			//订单ID
			String orderId=ofp.orderId;
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			//获取流程ID
			String distStateId=commodity.getDistStateID();
			//获取派单ID
			String findDistSQL="SELECT * FROM mall_commoditydistribution WHERE orderid=? ";
			
			MapList map=db.query(findDistSQL,orderId,Type.VARCHAR);
			
			//派单ID
			String distId="";
			
			if(!Checker.isEmpty(map)){
				distId=map.getRow(0).get("id");
			}
			
			Map<String,String> otherParam=new HashMap<String,String>();
			
			//设置DISTstate=4
			String res=StateFlowManager.getInstance().setNextState(distStateId,"4",distId,otherParam);
			
			result=new JSONObject(res);
			
			if(!result.getBoolean("SUCCESS")){
				//设置DISTstate=4失败，则设置DISTSTate=3
				res=StateFlowManager.getInstance().setNextState(distStateId,"3",distId,otherParam);
				result=new JSONObject(res);
			}
			
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
