
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script src="downloadapp/jquery.min.js" type="text/javascript"></script>
    </head>
    <body>
        <button id="send1">请求高德：地理编码</button>
		<button id="send2">请求高德：逆地理编码</button>

    </body>
    <script>
        $("#send1").click(function () {
            $.ajax({
                url:'http://restapi.amap.com/v3/geocode/geo?key=a5bf76c78efebd788d3c59731012c1d5&address=北京市朝阳区阜通东大街6号',
                type:"POST",
                dataType:'json',
                success:function(data){
                    alert(JSON.stringify(data));
                }
        } );
        });
    </script>
	<script>
        $("#send2").click(function () {
            $.ajax({
                url:'http://restapi.amap.com/v3/geocode/regeo?output=json&location=116.310003,39.991957&key=a5bf76c78efebd788d3c59731012c1d5&radius=1000&extensions=all',
                type:"POST",
                dataType:'json',
                success:function(data){
                    alert(JSON.stringify(data));
                }
        } );
        });
    </script>
</html>
