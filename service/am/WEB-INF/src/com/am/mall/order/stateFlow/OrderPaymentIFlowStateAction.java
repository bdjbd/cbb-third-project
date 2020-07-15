package com.am.mall.order.stateFlow;

import org.json.JSONObject;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 
 * 说明:<br />
 * 3 订单已付款未分配 <br />
 * 下一状态值：null <br />
 * 处理过程：<br />
 * 0、设置DistState=4，成功->，失败->1；<br />
 * 1、设置DistState=3;<br />
 * <hr />
 * 调用订单分配接口
 */
public class OrderPaymentIFlowStateAction implements IFlowState {

	@Override
	public String execute(OrderFlowParams param) {
		String result = "";

		try {
			
			OrderDispatcherManager distManager = OrderDispatcherManager.getInstance();
			
			result=distManager.setDistState(param,"4");
			
			JSONObject exeResult=new JSONObject(result);
			//DistState=4，成功->，失败->1
			if(!exeResult.getBoolean("SUCCESS")){
				distManager.setDistState(param,"3");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
