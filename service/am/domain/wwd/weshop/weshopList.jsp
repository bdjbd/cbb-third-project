<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page
	import="com.wisdeem.wwd.WeChat.server.WeShopServer,java.util.Formatter,com.fastunit.MapList;"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<title>商店列表</title>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0 maximum-scale=1.0">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache,must-revalidate">
<meta http-equiv="expires" content="0">
<link rel="stylesheet"
	href="/domain/wwd/weshop/jquery.mobile-1.3.2.min.css" />
<link rel="stylesheet" href="member.css">
<script src="/domain/wwd/weshop/jquery-1.7.1.min.js"></script>
<script src="/domain/wwd/weshop/jquery.mobile-1.3.2.min.js"></script>
<script src="weshop.js"></script>
<script type="text/JavaScript">
var token='<%=request.getParameter("token")%>';
var openid='<%=request.getParameter("openid")%>';
<%
	WeShopServer wss=new WeShopServer();
	request.getSession().setAttribute("openid", request.getParameter("openid"));
	String memberCode=wss.getMemberCodeByOpenid(request.getParameter("openid"));
	request.getSession().setAttribute("membercode", memberCode);
%>
</script>
<style type="text/css">
.unsubrite {
	color:black;
	margin: 0px;
	padding: 0px;
	border-bottom-style: none;
	border-bottom-width: 0px;
}

.unsubrite_btn {
	margin-bottom: 0px;
	margin-top: 0px;
	padding-bottom: 0px;
	padding-top: 0px;
	height: auto;
	text-align:center;
	background:#990000;
}

.li_table {
	width: 100%;
}

.li_table_gropu1 {
	width: 90%;
}

.li_table_gropu2 {
	width: 10%;
}

.li_table_td_span {
	display: norml;
	width: 80%;
}
</style>
</head>
<body>
	<div data-role="page" id="home">
		<script type="application/javascript">
  			$("#home").on("pageshow",function(){
  	  			var startX=0,entX=0;
  				document.getElementById("home_content").addEventListener("touchstart",function(event){
  	  				startX=event.targetTouches[0].screenX;
  					//event.preventDefault();
  				});
  				document.getElementById("home_content").addEventListener("touchmove",function(event){
  					endX=event.targetTouches[0].screenX;
  					console.log("endX:"+endX);
  					event.preventDefault();
  				});
  				document.getElementById("home_content").addEventListener("touchend",function(event){
  	  				if(startX-endX>100){
  	  	  				event.preventDefault();
  	  	  			}else if(startX-endX<-100){
  	  	  	  			event.preventDefault();
  	  	  	  		}
  	  				startX=0,entX=0;
  				});
  				
  				$(".unsubrite_btn").click(function(){
  	  				var curorgid=$(this).data("orgid");
  	  				alert(curorgid);
  	  				$.get("/weshop/unscribtOrg.do",{OPENID:openid,ORGID:curorgid},function(data,textStatus){
  	  				if(textStatus=="success"){
  	  				
  	  				$.mobile.loading('show', {
  						text : '',
  						textVisible :true,
  						theme : 'a',
  						textonly : false,
  						html : '<h1>取下关注完成</h1>'
  					});
  	  				location.reload();
  	  				}else{
  	  					showNetWorkException();
  	  				}
  	  			});
  	  				});
  				$(".li_div").bind("swipeleft",function(){
  					$(this).find("td:eq(1)").show("slow");
  				});
  				$(".li_div").bind("swiperight",function(){
  					$(this).find("td:eq(1)").hide("slow");
  				});
  			});
  		</script>
		<div data-role="content" id="home_content">
			<div data-role="main" class="ui-content">
				<ul data-role="listview" data-insert="true">
<%
WeShopServer wsServer=new WeShopServer();
MapList mapOrg=wsServer.getOrgByOpenid(request.getParameter("openid"));
String listStr=
"<li class=\"listdata\" ><a href=\"weshop.jsp?token="+request.getParameter("token")+"&orgidp=%s&openid="+request.getParameter("openid")+
" data-transition=\"slidefade\" rel=\"external\" \">"+
"<div class='li_div' style=\"background: #red; display: inline;\">"+
"<table class=\"li_table\"  >"+
"<colgroup class=\"li_table_gropu1\" ></colgroup>"+
"<colgroup class=\"li_table_grupu2\"></colgroup>"+
"<tr><td><span class=\"li_table_td_span\">%s</span></td><td style='display:none;' >"+
"<a href=\"#\" data-role=\"button\" class=\"unsubrite_btn\" data-mini=\"true\""+ 
"data-orgid=\"%s\" data-inilne=\"true\">"+
"取消关注</a></td></tr></table>"+
"</div>"+
"</a>"+
"</li>";
String token=(String)request.getSession().getAttribute("token");
//request.getParameter(arg0);
//"/domain/wwd/weshop/weshop.jsp?token="+token+"&openid="userInfo.getString("openid")+"&sceneid="+mapOrg.getRow(0).getInt("care_sceneid",-1)
for(int i=0;i<mapOrg.size();i++){
	String orgname=mapOrg.getRow(i).get("orgname");
	String orgid=mapOrg.getRow(i).get("orgid");
	Formatter f=new Formatter();
	f.format(listStr,orgid,orgname,orgid);
	out.println(f.toString());
}
%>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>
