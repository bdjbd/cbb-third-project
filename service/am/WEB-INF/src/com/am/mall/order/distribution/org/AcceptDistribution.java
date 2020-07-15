package com.am.mall.order.distribution.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 接受订单分配<br />
 * 接收CommodityDistribution.ID，设置并设置DistState=5，OrderState=4；<br />
 * action:com.am.mall.order.distribution.org.AcceptDistribution  <br />
 * dist_id，订单分配ID  <br />
 * return:JSON格式如下：{ CODE:”0”, MSG:”成功”,CHECK_CODE : “”} <br />
 * CODE=0成功，非0表示失败，同时MSG给出失败原因；
 */
public class AcceptDistribution implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}

}
