package com.am.mall.order.stateFlow;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 31 说明：需要手工分配订单界面可操作该条记录<br />
 * 下一状态值：null <br />
 * 处理过程：<br />
 * 0、	设置DistState=3,传参 接单ID
 */
public class OrderDispatcherFailAction implements IFlowState {

	@Override
	public String execute(OrderFlowParams rrp) {
		
		OrderDispatcherManager distManager=OrderDispatcherManager.getInstance();
		
		//设置DistState状态为3；
		String disterResultt="";
		try {
			distManager.updateDistState(rrp.orderId,"3");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return disterResultt;
	}

}
