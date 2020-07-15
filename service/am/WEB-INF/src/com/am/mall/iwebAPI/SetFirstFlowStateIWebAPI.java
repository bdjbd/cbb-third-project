package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.mall.beans.commodity.Commodity;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.ServerFlowManager;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 设置首流程状态值，并不调用状态值实现类,
 * 
 * URL:http://127.0.0.1/p2p/com.am.mall.iwebAPI.SetFirstFlowStateIWebAPI.do?
 * COMDITYID=商品ID&ORDERID=订单ID
 */
public class SetFirstFlowStateIWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//商品ID
		String comdityID=request.getParameter("COMDITYID");
		//订单ID
		String orderId=request.getParameter("ORDERID");
		
		//1,根据商品id，获取订单流程ID和订单分配流程ID
		Commodity comdity=CommodityManager.getInstance().getCommodityByID(comdityID);
		
		//2,初始化订单的订单流程ID
		String result=ServerFlowManager.getInstance().setFirstState(comdity.getOrderStateID(), orderId);
		
		return result;
	}

}
