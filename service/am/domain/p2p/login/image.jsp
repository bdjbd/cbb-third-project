<%@page contentType="image/jpeg"%>
<%@page import="com.p2p.base.MakeCertPirc"%>
<%
	MakeCertPirc mcp=new MakeCertPirc();
	String str=mcp.getCertPic(90,30,response.getOutputStream());
	session.setAttribute("CERTPIRCSTR", str);
	
	out.clear();
	out = pageContext.pushBody();
%>