<%@page import="java.io.File"%>
<%@page import="java.util.UUID"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>JSP�ϴ��ļ�</title>
</head>
<body>
<%
String path = request.getContextPath() + "/";
if(ServletFileUpload.isMultipartContent(request)){
	String type = "";
	if(request.getParameter("type") != null)//��ȡ�ļ�����
		type = request.getParameter("type") + "/";
	String callback = request.getParameter("CKEditorFuncNum");//��ȡ�ص�JS�ĺ���Num
	FileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
	servletFileUpload.setHeaderEncoding("UTF-8");//����ļ������������
	List<FileItem> fileItemsList = servletFileUpload.parseRequest(request);
	for (FileItem item : fileItemsList) {
		if (!item.isFormField()) {
			String fileName = item.getName();
			fileName = System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
			String filePath = "files/" + type + fileName;
			File file = new File(request.getSession().getServletContext().getRealPath(filePath));
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			item.write(file);
			out.println("<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction("+callback+",'"+path+filePath+"')</script>");
			break;
		}
	}
}
%>
</body>
</html>