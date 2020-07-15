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
 * 用户删除订单
 * 10=用户已删除	说明：用户删除订单
 * 下一状态值：10
 * 处理过程：更新状态值
 */
public class UserDeleteOrderIWebAPI implements IWebApiService {

	
		@Override
		public String execute(HttpServletRequest request,
				HttpServletResponse response) {
			
			//订单ID
			String orderId=request.getParameter("ORDERID");
			
			OrderDispatcherManager.getInstance().updateDistState(orderId,"10");
			
			String result="{\"CODE\":1,\"MSG\":\"删除订单成功！\"}";
			
			return result;
		}

}
