package com.am.frame.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;


/***
 *  现金支付
 * @author Administrator
 *
 */
public class CashPaymentWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		
		String result="";
		
		String memberCode=request.getParameter("MEMBERID");
		String payMoney=request.getParameter("PAYMONEY");
		String payId=request.getParameter("PID");
		String accountId = request.getParameter("accountId");
		
		PayManager payManager=new PayManager();
		
		result=payManager.cashPay(memberCode,Double.parseDouble(payMoney), payId,accountId);
		
		return result;
	}

}
