<%-- 
    Document   : index
    Created on : 2016-7-18, 14:36:45
    Author     : YueBin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>

<%
request.getSession().setAttribute("API_secretKey", UUID.randomUUID().toString());
%>

<!DOCTYPE html>
<html>
    <head>

<script type="text/javascript">
    var AOCS0203049574831 = "<%=request.getSession().getAttribute("platform_secretKey")%>";
</script>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
