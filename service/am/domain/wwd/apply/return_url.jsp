<%
/* *
 功能：支付宝页面跳转同步通知页面
 日期：2013-12-18
 //********************************
 * */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.wisdeem.wwd.alipay.util.*"%>
<%@ page import="com.wisdeem.wwd.alipay.config.*"%>
<html>
  <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝页面跳转同步通知页面</title>
  </head>
  <body>
<%
	//获取支付宝GET过来反馈信息
	Map<String,String> params = new HashMap<String,String>();
	Map requestParams = request.getParameterMap();
	for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		String name = (String) iter.next();
		String[] values = (String[]) requestParams.get(name);
		String valueStr = "";
		for (int i = 0; i < values.length; i++) {
			valueStr = (i == values.length - 1) ? valueStr + values[i]
					: valueStr + values[i] + ",";
		}
		//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
		valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
		params.put(name, valueStr);
	}
	
	//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
	//商户订单号
	String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

	//交易时间
	String notify_time = new String(request.getParameter("notify_time").getBytes("ISO-8859-1"),"UTF-8");

	//交易状态
	String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
	
	//计算得出通知验证结果
	boolean verify_result = AlipayNotify.verify(params);
	
	if(verify_result){//验证成功
	
		if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
			
		}
		out.println("充值成功,请关闭此页面!<br/>");
		SuccessAction.SuccessPara(params);
		out.println("<script>window.opener.location.reload()</script>");
		
	}else{
		
		out.println("充值失败,请重新充值!");
	}
%>
  </body>
</html>