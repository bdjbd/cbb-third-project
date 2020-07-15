<%
/* *
 *功能：批量付款到支付宝账户有密接口接入页
 *版本：3.3
 *日期：2012-08-14
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *************************注意*****************
 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
 *1、商户服务中心（https://b.alipay.com/support/helperApply.htm?action=consultationApply），提交申请集成协助，我们会有专业的技术工程师主动联系您协助解决
 *2、商户帮助中心（http://help.alipay.com/support/232511-16307/0-16307.htm?sh=Y&info_type=9）
 *3、支付宝论坛（http://club.alipay.com/read-htm-tid-8681712.html）
 *如果不想使用扩展功能请把扩展功能参数赋空值。
 **********************************************
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alipay.config.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.am.frame.transactions.withdraw.WithdrawService"%>
<%@ page import="com.fastunit.jdbc.DB"%>
<%@ page import="com.fastunit.jdbc.DBFactory"%>
<%@ page import="com.fastunit.jdbc.Type"%>
<%@ page import="com.fastunit.jdbc.exception.JDBCException"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝批量付款到支付宝账户有密接口</title>
	</head>
	<%
		////////////////////////////////////请求参数//////////////////////////////////////

		String widsStr =request.getParameter("widsStr");
		DB db=null;
		try {

			db=DBFactory.newDB();

			WithdrawService ws=new WithdrawService();
			String sHtmlText=ws.alipayPaymentWithdarw(db,widsStr);
		
			out.println(sHtmlText);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
	%>
	<body>
	</body>
</html>
