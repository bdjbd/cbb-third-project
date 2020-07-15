package com.am.mall.order.stateFlow;


import org.json.JSONObject;

import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.beans.order.StateFlowStepSetup;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.order.OrderManager;
import com.am.mall.order.ServerFlowManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 
 * 说明:<br />
 * 4=订单分配
 * 说明：订单已分配未配送
 * 下一状态值：5
 * 处理过程：更新状态值
 * 
 */
public class OrderDispatcherFlowAction implements IFlowState {

	private static final String ACCEPT_STEP_KEY="确认已经分配状态值";
	
	
	@Override
	public String execute(OrderFlowParams param) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			DB db=DBFactory.getDB();
			
			MemberOrder order=new OrderManager().getMemberOrderById(param.orderId, db);
			Commodity commdity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			
			//1，检查订单是否有服务流程
			if(checHaveServerFlow(commdity,db)){
				
				//2，如果有，检查服务流程是否符合向下一级跳转
				if(checkConformStepping(order,commdity,db)){
					//21, 如果符合调整条件，跳转，
					DefaultFlowStateAction defaultAction=new DefaultFlowStateAction();
					
					result=defaultAction.stepping(param);
				}else{
					//22,不符合调整条件，输出不符合原因。
				}
				
			}else{
				//3，如果没有，使用默认，更新到下一个状态即可。
			}
			
		}catch(Exception e){
			
			try{
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("STATE",param.currentKeyValue);
				result.put("ORDERID",param.orderId);
				
			}catch(Exception je){
				je.printStackTrace();
			}
		}
		return result.toString();
	}

	

	
	/**
	 * 检查服务流程是否符合向下一级跳转
	 * @param MemberOrder 订单
	 * @param Commodity 订单对应的商品
	 * @param db  DB
	 * @return 如果符合，返回true,不符合返回fasle
	 * @throws Exception 
	 */
	private boolean checkConformStepping(MemberOrder order, Commodity commdity,DB db) throws Exception{
		
		boolean result=false;
		
		//订单流程分配状态
		String distState=OrderDispatcherManager.getInstance().getOrderidStateValueById(order.getId(),db);
		
		//当前订单流程状态参数
		StateFlowStepSetup sfss=ServerFlowManager.getInstance().getStateFlowStepSetup(
				order.getOrderState(),commdity.getOrderStateID());
		
		String acceptStepValue=sfss.getParams().getValueOfName(ACCEPT_STEP_KEY);
		
		if(!Checker.isEmpty(distState)&&distState.equals(acceptStepValue)){
			//符合调整条件
			result=true;
		}else{
			throw new Exception("不符合调整条件,当前订单分配状态是"+distState+",跳转条件订单分配状态是"+acceptStepValue);
		}
		
		return result;
	}




	/**
	 * 检查订单是否有服务流程
	 * @param Commodity 订单对应的商品
	 * @param db DB
	 * @return 有流程服务，返回true，无返回false
	 * @throws JDBCException 
	 */
	private boolean checHaveServerFlow(Commodity commdity, DB db) throws JDBCException {
		
		boolean result=false;
		
		if(!Checker.isEmpty(commdity.getDistStateID())){
			result=true;
		}
		
		return result;
	}
	
	
}
