package com.am.frame.payment.impl.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YueBin
 * @create 2016年7月8日
 * @version 
 * 说明:<br />
 * 支付宝网页支付,此功能主要是为了关闭当前页面,并且让父页面跳转到支付成功界面
 * 业务处理同移动支付相同(com.am.frame.payment.impl.webApi.AlipayPaymenNotifyWebApi)，后续修改需要同时修改
 */
public class WapAlipayPaymenNotifyWebApi extends AlipayPaymenNotifyWebApi{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String reuslt=processAlipayTrans(request, response);
		
		StringBuilder sb=new StringBuilder();
		//微信手机支付，返回为页面
		if("success".equals(reuslt)){
			sb.append("<script>");
			sb.append("window.close();");
			sb.append("</script>");
			
		}
		
		return sb.toString();
	}
	
}
