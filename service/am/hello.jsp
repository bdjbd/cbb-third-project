<%--
Document : memory
Created on : 2009-4-9, 1:35:17
Author : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JVM memory</title>
</head>
<body>
<%
double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);
double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);
out.println("Java �������ͼʹ�õ�����ڴ���(��ǰJVM���������ڴ�)maxMemory(): " + max + "MB<br/>");
out.println("Java ������е��ڴ�����(��ǰJVMռ�õ��ڴ�����)totalMemory(): " + total + "MB<br/>");
out.println("Java ������еĿ����ڴ���(��ǰJVM�����ڴ�)freeMemory(): " + free + "MB<br/>");
out.println("��ΪJVMֻ������Ҫ�ڴ�ʱ��ռ�������ڴ�ʹ�ã�����freeMemory()��ֵһ������¶���С��<br/>" +
"��JVMʵ�ʿ����ڴ沢������freeMemory()����Ӧ�õ��� maxMemory()-totalMemory()+freeMemory()��<br/>");
out.println("JVMʵ�ʿ����ڴ�: " + (max - total + free) + "MB<br/>");
out.println("jspcn");
%>
</body>
</html>