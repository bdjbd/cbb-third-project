package com.am.frame.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

/**
 * 我的支付处理接口
 * @author Administrator
 */

public class MyPaymentIWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
	
		String memberCode=request.getParameter("member_code");
		
		PayManager payManage=new PayManager();
		String result=payManage.getMyPaymentToJSON(memberCode);
		
		return result;
	}

}
