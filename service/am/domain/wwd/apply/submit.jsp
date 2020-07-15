<%
/* *
 *功能：快捷登录接口接入页
 *日期：2013-12-18
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝快捷登录接口</title>
	</head>
	<body>
	<h3>正在加载数据.....</h3>
	<%
	out.println(session.getAttribute("APPLY_FORM"));
	session.setAttribute("APPLY_FORM","");
	%>
	</body>
</html>
