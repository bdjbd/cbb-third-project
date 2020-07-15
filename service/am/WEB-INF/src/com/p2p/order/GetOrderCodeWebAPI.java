package com.p2p.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

/**
 * 获取订单编号
 * @author Administrator
 *
 */
public class GetOrderCodeWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		
		String orgid=request.getParameter("orgid");
		
		String orderCode=OrderManager.getInstance().getOrderCode(orgid);
		
		return orderCode;
	}

}
