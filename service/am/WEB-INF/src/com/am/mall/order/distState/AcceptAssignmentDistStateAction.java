package com.am.mall.order.distState;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.mall.beans.order.StateFlowStepSetup;
import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.order.OrderManager;
import com.am.mall.order.ServerFlowManager;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月17日
 * @version 
 * 说明:<br />
 * 5=接受分配
 * 
 * 说明：分配已经确认，订单分配完成
 * 下一状态值：5
 * 处理过程：
 * 0、设置OrderState=订单状态值，DistState=下一状态值；
 */
public class AcceptAssignmentDistStateAction implements IFlowState{

	
	private String distState="5";
	
	private final static String ORDER_STATE_KEY="订单状态值";
	
	//private final static String NEXT_DIST_STATE="下一状态值";
	
	/**
	 * return 0=失败；1=成功；当前的状态值 {CODE:value,MSG:valu,STATE:value}
	 */
	@Override
	public String execute(OrderFlowParams rderfp) {
		
		JSONObject result=new JSONObject();
		
		try{
			StateFlowStepSetup sfss=ServerFlowManager.getInstance().getStateFlowStepSetup(
					 distState, rderfp.currentFlowId);
			 
			 //获取订单状态值
			 String orderStateValue=sfss.getParams().getValueOfName(ORDER_STATE_KEY);
			 
			 if(Checker.isEmpty(orderStateValue)||"null".equalsIgnoreCase(orderStateValue)){
				 orderStateValue="4";
			 }
			 
			 //设置订单状态值
			 new OrderManager().updateOrderState(rderfp.keyValue, orderStateValue);
			 
			 //设置订单分配状态值为下一个状态值  下一个订单状态为本状态，防止死循环，不调用修改状态功能。
			//String nextStateValue=sfss.getNextStateValue();
			//OrderDispatcherManager.getInstance().setDistState(nextStateValue, rderfp);
			
			
		}catch(Exception e){
			e.printStackTrace();
			try {
				
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return result.toString();
	}

}
