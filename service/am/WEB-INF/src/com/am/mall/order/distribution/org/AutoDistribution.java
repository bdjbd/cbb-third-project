package com.am.mall.order.distribution.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 分配订单<br />
 * 依据订单坐标，寻找10公里（Var中定义）范围内最近的门店进行派单，<br />
 * 派单成功并通知接单ID的设备（手机短信、推送通知）；如未找到门店则DistState=6，OrderState=31；
 * action:com.am.mall.order.distribution.org.AutoDistribution
 * 
 */
public class AutoDistribution implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String orderId="";
		
		return null;
	}

}
