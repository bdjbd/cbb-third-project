package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.mall.order.OrderDispatcherManager;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月26日
 * @version 
 * 说明:<br />
 * 订单取消接口
 * 9=订单取消	说明：用户取消订单
 * 下一状态值：9
 * 处理过程：更新状态值
 */
public class UserCancelOrderIWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//订单ID
		String orderId=request.getParameter("ORDERID");
		
		OrderDispatcherManager.getInstance().updateDistState(orderId,"9");
		
		String result="{\"CODE\":1,\"MSG\":\"取消订单成功！\"}";
		
		return result;
	}

}
