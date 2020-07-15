<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="com.alipay.config.*"%>
<%@ page import="com.am.frame.pay.*"%>
<html>
  <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付完成</title>
		<style>
		.paySuccess{
			font-size:30px;
			color:#4bda41;
		}
		.paySuccessDiv{

		}
		.imgCs{
		    width: 48px;
			line-height: 41px;
			vertical-align: bottom;
		}
		.content{
			baseline-shift: baseline;
			width: 800px;
			margin-right: auto;
			margin-left: auto;
			height: 200px;
			border: solid 4px #655c5c;
			padding-top: 100px;
		}
		</style>
  </head>
  <body style="margin-right: auto;
    margin-left: auto;
    text-align: center;
    padding-top: 80px;">
<%
	//获取支付宝GET过来反馈信息
	//?buyer_email=yuebin616%40126.com&buyer_id=2088302428817113&exterface=create_direct_pay_by_user&is_success=T&notify_id=RqPnCoPT3K9%252Fvwbh3InZcvzxeemM6pm2QrIDW0FxsrjAUMjfxSoYL4CGJNQhakju1BPU&notify_time=2016-12-07+15%3A29%3A13&notify_type=trade_status_sync&out_trade_no=d0cee26387f74fd2976c59c69e366f22&payment_type=1&seller_email=lixingnongye948%40163.com&seller_id=2088421377935311&subject=充值&total_fee=0.01&trade_no=2016120721001004110299024267&trade_status=TRADE_SUCCESS&sign=DAllleFoO3HX94ftnu1IFQk9pRajQRXYFhz3bZS1%2F%2BGbLfjnzzpvQin5VHhgnHcqnZp1OhSEFQcKXEtzazlFAcB3P9Vn%2Bi2Z9eaky5C0FEdO1v0hgKArt2Qqna4Zsfmb6qEs1iTnAc8vMucLAC3ReAfJY77K5hxLc5kVV5THvdg%3D&sign_type=RSA

	//商户订单号	
    String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

	//支付宝交易号	
    String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

    out.println("<div class='content'><img class='imgCs' src='success.jpg' style='width:40px' /><span class=\"paySuccess\">恭喜您！支付成功。</span><div id=\"paySuccessDiv\"><br />"
				+"支付单号:<span class=\"orderCode\">"
				+out_trade_no
				+"</span><br /><br /><br /></div></div>");

        
	
%>
  </body>
</html>