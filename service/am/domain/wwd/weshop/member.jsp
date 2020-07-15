<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.wisdeem.wwd.WeChat.server.WeShopServer,com.wisdeem.wwd.WeChat.server.WeChatInfaceServer
,com.fastunit.MapList,java.nio.charset.Charset,com.wisdeem.wwd.WeChat.beans.Interviewer,
com.wisdeem.wwd.WeChat.beans.PublicAccount" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<title>会员卡</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, minimum-scale=1.0 maximum-scale=1.0">
<meta http-equiv="pragma" content="no-cache">     
<meta http-equiv="cache-control" content="no-cache,must-revalidate">   
<meta http-equiv="expires" content="0"> 

<link rel="stylesheet"	href="/domain/wwd/weshop/jquery.mobile-1.3.2.min.css" />
<link rel="stylesheet" href="member.css"> 
<script src="/domain/wwd/weshop/jquery-1.7.1.min.js"></script>
<script	src="/domain/wwd/weshop/jquery.mobile-1.3.2.min.js"></script>
<script src="weshop.js"></script>
<script type="text/JavaScript">
	var networkType = "";
	//获取网络类型
	var token="<%=request.getParameter("token")%>";
	var register="<%=request.getParameter("regist")%>";
	var openid="<%=request.getParameter("openid")%>";
	var orgid="<%=request.getParameter("orgidp")%>";
	var orgname="";
	var userStatus="1";
	var memberCode="";
	<%
	WeShopServer wss=new WeShopServer();
	String openidJsp=null;
	String orgid=null;
	if(request.getParameter("openid")!=null){
		openidJsp=request.getParameter("openid");
		orgid=request.getParameter("orgidp");
		session.setAttribute("OPENID",openidJsp);
		session.setAttribute("TOKEN",request.getParameter("token"));
		session.setAttribute("orgid",request.getParameter("orgidp"));
		request.getSession().setAttribute("CURRENT_MEMBER_INFO",wss.getMemberinfo(openidJsp));
		request.getSession().setAttribute("CURRENT_ORG",request.getParameter("orgidp"));
		if(WeChatInfaceServer.USERINFO_CATCH.get(openidJsp)!=null){
			WeChatInfaceServer.USERINFO_CATCH.get(openidJsp).setOrgind(orgid);
			out.print("orgid='"+WeChatInfaceServer.USERINFO_CATCH.get(openidJsp).getOrgind()+"';");
		}
	}else{
		openidJsp=(String)session.getAttribute("OPENID");
		if(session.getAttribute("CURRENT_ORG")!=null){
			out.print("orgid='"+session.getAttribute("CURRENT_ORG")+"';");
		}
	}
	if(request.getParameter("orgidp")!=null&&request.getParameter("orgidp").toString().length()>0){
	request.getSession().setAttribute("CURRENT_ORG",request.getParameter("orgidp"));
	}
	if(openidJsp!=null){
		out.print("userStatus='"+wss.getMemberStatus(orgid,openidJsp)+"';");
		out.print("openid='"+openidJsp+"';");
		out.print("memberCode='"+wss.getMemberCodeByOpenid(openidJsp)+"';");
	}
	MapList map=wss.getOrgidByToken(request.getParameter("token"));
	if(request.getParameter("orgidp")!=null){
		out.print("orgname='"+wss.getOrgnameByOrgid(request.getParameter("orgidp"))+"';");
	}else{
		out.print("orgid='"+map.getRow(0).get("orgid")+"';");
	}
	%>
	var memberInfoSS=<%=session.getAttribute("CURRENT_MEMBER_INFO")%>;
	var w;
	var user = new User();//用户
	//会员地址
	var addressList=null;
	//订单信息
	var orderList=new Object();
	//
	function showTitle(){
		var cs=document.getElementsByClassName("weshop_title_canvas");
		for(var i=0;i<cs.length;i++){
			var ctx=cs[i].getContext("2d");
			ctx.clearRect(0,0,cs[i].width,cs[i].height);
			ctx.font="22px Verdana";
			ctx.fillStyle = 'rgba(0,0,0,1)';
			ctx.fillText(orgname,10,30);
			ctx.save();
		}
	}
	document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
		WeixinJSBridge.invoke('getNetworkType', {}, function(e) {
			WeixinJSBridge.log(e.err_msg);
			networkType = e.err_msg;
			//获取网络类型
		});
	});

	$(document).ready(
			function() {
				$("#myQuery").click(function() {
					alert(networkType);
				});

				$("#myTask").click(function() {

					if (typeof (Worker) !== "undefined") {
						if (typeof (w) == "undefined") {
							w = new Worker("weshopWorker.js");
						}
						w.onmessage = function(event) {
							$("#myTaskCount").text(event.data);
						};
					} else {
						$("#myTaskCount").text("不支持Worker");
					}
					if (window.localStorage) {
						var str = window.location.search.substr(1);
						var arStr = str.split("&");
						//alert(arStr);
						for ( var i = 0; i < arStr.length; i++) {
							//alert(arStr[i].split("="));
						}
					} else {

					}
				});
				$("#testLocalStroage").click(function() {
					for ( var i = 0; i < localStorage.length; i++) {
						localStorage.getItem(localStorage.key(i));
					}
				});
				/**
				 *初始化用户信息 
				 */
				function initUser() {
					var str = window.location.search.substr(1);
					if (str.length > 1) {//向导进入
						var paramsAr = str.split("&");
						for ( var i = 0; i < paramsAr.length; i++) {
							if (paramsAr[i] != "undefined") {
								var ar = paramsAr[i].split("=");
								user[ar[0]] = ar[1];
							}
						}
						user.saveUser();
					} else {
						user.getUser2Local();
					}
				}

				/**
				 *加载用户数据
				 **/
				function loadUser(data) {
					if(location.search.indexOf("openid")>0&&location.search.indexOf("token")>0){
						user.paramsUser(data);
						user.saveUser();
					}else{
						user.getUser2Local();
					}
				}
		});

	 document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
			WeixinJSBridge.call('hideOptionMenu');
		});
</script>

<style>
.leftBar {
	float: left;
	width: 25%;
	display: inline;
	margin: 1em;
	background-color: #900;
}

#memberInfo {
	background-color: blue;
}

#memberInfo div {
	
}

.imgDiv {
	float: left;
	height: 5em;
	width: 100%;
}

#userinfoTable {
	width: 100%;
}

.colgroup1 {
	width: 30%;
	align: right;
	valign: middle;
}

.colgroup2 {
	width: 70%;
}
input[id="recvName"]:-moz-placeholder { color: #369; }
input[id="recvName"]::-webkit-input-placeholder { color:red;}
input[id="recvPhone"]::-webkit-input-placeholder { color:red;}
input[id="edNickName"]::-webkit-input-placeholder { color:red;}
input[id="edMemName"]::-webkit-input-placeholder { color:red;}

</style>
</head>
<body>

<div data-role="page" id="memberInfoS">
<script>
 $("#memberInfoS").live("pageshow",function(){
	 $(document).attr("title","会员卡");
	 <%
	 if(openidJsp==null){
			out.print("closeDialog();");
			out.print("$.mobile.changePage('#userGuide');");
		}
	 %>
	 if(isEmpty(memberInfoSS)){
		 $.mobile.changePage('#userGuide');
	}
	 if(userStatus==2||userStatus==4){//冻结
			closeDialog();
			user.data_status=2;
			user.saveUser();
			$.mobile.changePage("#userFreeze");
			return;
		}
	if(userStatus==3){
		closeDialog();
		user.data_status=2;
		user.saveUser();
		$.mobile.changePage("#userLocked");
		return;
	}
	 function setUserInfo(user){
			if(!isEmpty(user.nickName)){
				$("#nickName").text(user.nickName);
			}
			if(!isEmpty(memberInfoSS)&&!isEmpty(memberInfoSS[0])){
				if(memberInfoSS[0].GENDER==1){
					$("#infoGender").text("男");
				}else if(memberInfoSS[0].GENDER==2){
					$("#infoGender").text("女");
				}else{
					$("#infoGender").text("保密");
				}
			}
			if(!isEmpty(user.phone)){
				$("#infoPhone").text(user.phone);
			}
			if(!isEmpty(user.email)){
				$("#infoEmail").text(user.email);
			}
			for(var i=0;i<$(".title_content").length;i++){
			    $(".title_content:eq("+i+")").text(orgname);
			}
		}
	//用户登录验证
		function loadingViaid(){
			//从本地获取用户数据
			user.getUser2Local();
			if(!isEmpty(register)){//注册
				registerUser(openid,token,function(data){
					console.log("注册成功:"+data[0]);
					user.paramsUser(data[0]);
					user.saveUser();
					setUserInfo(user);
					if(!isEmpty(memberCode)){
						loadOrders(memberCode, function(data) {
							$("#orderCount").text(data.length)
						});
						loadAddress(memberCode, function(data) {
							$("#addressCount").text(data.length);
						});
					}
					closeDialog();
				});
			}else if(isEmpty(memberCode)){//无权限
				$.mobile.changePage("#userGuide");
			}else{
				loadUserInf(user.openid,user.token,
						function(data) {
						if (data == null || data == "undefined"|| data == "") {
							$.mobile.changePage("#userGuide");
							return;
							}
						if(userStatus==2||userStatus==4){//冻结
							closeDialog();
							user.data_status=2;
							user.saveUser();
							$.mobile.changePage("#userFreeze");
							return;
						}
						if(userStatus==3){
							closeDialog();
							user.data_status=2;
							user.saveUser();
							$.mobile.changePage("#userLocked");
							return;
						}
							user.paramsUser(data[0]);
							user.saveUser();
							setUserInfo(user);
							if(!isEmpty(memberCode)){
								loadOrders(user.member_code, function(data) {
									$("#orderCount").text(data.length)
								});
								loadAddress(memberCode, function(data) {
									$("#addressCount").text(data.length);
								});
							}
							closeDialog();
						});
			}
			 showTitle();	
		}
		loadingData(loadDataStr);
		loadingViaid();
		for(var i=0;i<$(".title_content").length;i++){
			eval("$(\".title_content:eq("+i+")\").text(orgname);");
		}
 })
</script>
<div data-role="header" data-theme="c">
<div class="titleDiv">
<table style="width:100%;margin-top:0px;">
	<tr><td><strong text-type="title" class="title_content"></strong></td>
	<td align="right">
	<a href="#EditUserinfo" id="EditUserinfoBtn" style="margin-bottom:-1px;margin-top:-1px;padding:0px;" data-icon="gear" data-role="button"  data-inline="true" data-mini="true" data-theme="b" >帐号设置</a>
	</td></tr>
</table>
</div>
</div>
<div data-role="content">
		<ul data-role="listview" data-inset="false">
			<li>
			<!-- img src="#" style="width:5em;height:5em;" / -->
			<span class="normalSpan">欢迎，</span>
			<span id="nickName"></span><br/>
			</li>
			<li>
			<span class="normalSpan">性别:</span><span class="normalSpan" id="infoGender"></span><br/>
			</li>
			<li data-icon="star">
			<span class="normalSpan">手机:</span><span class="normalSpan" id="infoPhone"></span><br/>
			</li>
			<li>
			<span class="normalSpan" >邮箱:</span><span class="normalSpan" id="infoEmail"></span><br/>
			</li>
			</ul>
			<br />
			<div>
			<ul data-role="listview" id="memberInofListView" data-inset="true">
				<!-- li><a href="#" id="myTask" class="items" data-icon="star"
					data-theme="b">我的任务&nbsp;&nbsp;<span
						class="ui-li-count ui-btn-up-c ui-btn-corner-all"
						id="myTaskCount">12</span>
					</a></li-->
					<li><a href="#myOrders" class="orderss" data-icon="star"
						data-theme="b">我的订单&nbsp;&nbsp;<span	class="ui-li-count ui-btn-up-c ui-btn-corner-all" id="orderCount">0</span>
					</a></li>
					<!-- li id="myQuery"><a href="#" class="items" data-icon="star"
						data-theme="c">我的电子券&nbsp;&nbsp;<span
							class="ui-li-count ui-btn-up-c ui-btn-corner-all">32</span>
					</a></li-->
					<li><a href="#address" class="hidentDialog" data-icon="star"
						data-theme="d">收货地址&nbsp;&nbsp;<span
							class="ui-li-count ui-btn-up-c ui-btn-corner-all"
							id="addressCount">0</span>
					</a></li>
				</ul>
			</div>
		</div>

	</div>
	<!-- /page -->

<div data-role="page" id="EditUserinfo" data-theme="c" data-content-theme="c">
		<!--会员信息编辑开始-->
		<script type="text/javascript">
			function initEditPageData() {
				if(!isEmpty(user.openid)){
					$("#edOpenid").val(user.openid);
				}
				if(!isEmpty(user.token)){
					$("#edToken").val(user.token);;
				}
				if(!isEmpty(user.member_code)){
					$("#edMemberCode").val(user.member_code);
				}
				if(!isEmpty(user.nickName)){
					$("#edNickName").val(user.nickName);
				}
				if(!isEmpty(user.memName)){
					$("#edMemName").val(user.memName);
				}
				if(!isEmpty(user.age)){
					$("#edAge").val(user.age);
				}
				if(!isEmpty(user.phone)){
					$("#edPhone").val(user.phone);
				}
				if(!isEmpty(user.email)){
					$("#edEmail").val(user.email);
				}
				if(!isEmpty(user.postalCode)){
					$("#edPostalCode").val(user.postalCode);
				}
				var genderSele="";
				if(!isEmpty(memberInfoSS)&&!isEmpty(memberInfoSS[0])&&!isEmpty(memberInfoSS[0].GENDER)){
					$("#edGender").val(memberInfoSS[0].GENDER).selectmenu("refresh");
				}else{
					$("#edGender").val("3").selectmenu("refresh");
				}
				if(!isEmpty(user.hobby)){
					for ( var i = 0; i < user.hobby.split(",").length; i++) {
						var hobSele = "#checkbox-" + user.hobby.split(",")[i];
						$(hobSele).attr("checked", true).checkboxradio("refresh");
					}
					//$("#edGender").selectmenu("refresh");
				}
			}
			$("#EditUserinfo").live("pagecreate", function() {
			});
			$("#EditUserinfo").live("pageshow", function() {
				loadingData(loadDataStr);
				$(document).attr("title","帐号设置");
				user.getUser2Local();
				initEditPageData();
				closeDialog();
				$("#addrForm").submit(
						function(){
							if(isEmpty($("#edNickName").val())){
								alertDialog("昵称必须输入");
								return false;
							}
							if(isEmpty($("#edMemName").val())){
								alertDialog("姓名必须输入");
								return false;
							}
							if(!isEmpty($("#edPhone").val())&&!isPhone($("#edPhone").val())){
								alertDialog("只能输入手机号码，请检查你的输入。");
								return false;
							}else if(!isEmpty($("#edEmail").val())&&!isEmail($("#edEmail").val())){
								alertDialog(" 请输入正确的邮箱。");
								return false;
							}else if(!isEmpty($("#edPostalCode").val())&&!isPostCode($("#edPostalCode").val())){
								alertDialog(" 请输入正确的邮政编码。");
								return false;
							}else if(!isEmpty($("#edAge").val())){
								if($("#edAge").val()>200||!isNumber($("#edAge").val())){
									alertDialog("请你输入正确的年龄。");
									return false
								}
							}
							return true;
						}
				);
				for(var i=0;i<$(".title_content").length;i++){
					eval("$(\".title_content:eq("+i+")\").text(orgname);");
				}
			});
		</script>
		<div data-role="header" data-theme="c">
			<div class="titleDiv">
				<table style="width:100%;margin-bottom:-0.1em;margin-top:-0.2em;">
				<tr><td><strong text-type="title" class="title_content"></strong></td>
				<td align="right">
				<a href="#EditUserinfo" data-icon="gear" data-role="button"  data-inline="true" data-mini="true" data-theme="b" style="display: none;">帐号设置</a>
				</td></tr>
			</table>
			</div>
		</div>
		<div data-role="content">
			<form id="addrForm" action="/weshop/editMember.do?orgidp=<%=session.getAttribute("CURRENT_ORG")%>>" method="post" data-transition="pop" data-ajax="false">
			<ul data-role="listview">
			<li>
			  <label for="edNickName">昵称:&nbsp;</label>
			  <input type="text" style="display:inline;" data-role="none" input-type="input" name="nickname" placeholder="请输入昵称" data-mini="true"	data-inline="true" id="edNickName" value="" />
			</li>
			<li>
			  <label for="edMemName">姓名:&nbsp;</label>
			  <input type="text" data-role="none" input-type="input" name="memname" placeholder="请输入姓名" data-mini="true"	data-inline="true" id="edMemName" value="" />
			</li>
			<li>
			<label for="edAge">年龄:&nbsp;</label>
			<input type="text" name="age" min="1" max="200" data-role="none" input-type="input"
							data-mini="true" data-inline="true"  value="18" id="edAge"
							placeholder="请输入年龄" />
			</li>
			<li>
			<table style="margin-bottom:-10px;margin-top: -10px;">
			<colgroup width="20%"></colgroup><colgroup width="75%"></colgroup>
			<tr>
			<td>性别:</td>
			<td>
			<select name="gender" id="edGender" data-mini="true" data-inline="true" >
								<option value="1" >男</option>
								<option value="2" >女</option>
								<option value="3" >保密</option>
				</select>
			</td>
			</tr>
			</table>
			</li>
			<li>
				<label for="edPhone">手机:&nbsp;</label>
				<input type="text" name="phone" data-role="none" input-type="input" data-mini="true" data-inline="true" value="" placeholder="请输入手机号码" id="edPhone" />
			</li>
			<li>
				<label for="edEmail">邮箱:&nbsp;</label>
				<input type="text" name="email" data-mini="true" data-role="none" input-type="input" 
					data-inline="true" value="" placeholder="请输入邮箱" id="edEmail" />
			</li>
			<li>
				<label for="edPostalCode">邮编:&nbsp;</label>
				<input type="text" name="postal_code" data-mini="true"  data-role="none" input-type="input" 
							data-inline="true" value="" placeholder="请输入邮编"
							id="edPostalCode" />
			</li>
			</ul>
				<table id="userinfoTable">
					<colgroup class="colgroup1"></colgroup>
					<colgroup class="colgroup2"></colgroup>
					<tr>
						<td></td>
						<td>
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
					</tr>
					<!-- tr>
						<td>积分:</td>
						<td><input type="text" name="integration" data-mini="true"
							data-inline="true" value="235" disabled placeholder="" /></td>
					</tr>
					<tr>
						<td>等级:</td>
						<td><input type="text" name="grade" data-mini="true"
							data-inline="true" value="12" disabled placeholder="" /></td>
					</tr-->
				</table>
				<div data-role="fieldcontain">
					<fieldset data-role="controlgroup">
						<legend style="font-weight:bold;" > 爱好: </legend>
						<input type="checkbox" name="hobby1" id="checkbox-1"
							class="custom" /> <label for="checkbox-1">收集邮票</label> <input
							type="checkbox" name="hobby2" id="checkbox-2" class="custom" />
						<label for="checkbox-2">种植花木</label> <input type="checkbox"
							name="hobby3" id="checkbox-3" class="custom" /> <label
							for="checkbox-3">阅读书籍</label> <input type="checkbox"
							name="hobby4" id="checkbox-4" class="custom" /> <label
							for="checkbox-4">旅行</label> <input type="checkbox" name="hobby5"
							id="checkbox-5" class="custom" /> <label for="checkbox-5">电影</label>
						<input type="checkbox" name="hobby6" id="checkbox-6"
							class="custom" /> <label for="checkbox-6">书法</label> <input
							type="checkbox" name="hobby7" id="checkbox-7" class="custom" />
						<label for="checkbox-7">画画</label> <input type="checkbox"
							name="hobby8" id="checkbox-8" class="custom" /> <label
							for="checkbox-8">欣赏音乐</label>
					</fieldset>
				</div>
				<input type="hidden" name="openid" value="" id="edOpenid" />
				<input type="hidden" name="token" value="" id="edToken" /> 
				<input	type="hidden" name="member_code" value="" id="edMemberCode" />
				<div style="text-align:center;">
				</div>
				<div data-role="controlgroup" data-type="horizontal" style="text-align: center;" >
					<button type="submit" data-theme="b" value="submit-value" data-mini="false" data-inline="true">提交</button>
					<a href="#memberInfoS" data-role="button" data-mini="false"	data-inline="true">返回</a>
				</div>
			</form>
		</div>
	</div>
	<!--会员信息编辑结束-->

	<div data-role="page" id="address">
		<script type="text/javascript">
			$("#address").live("pageshow",function(){
				loadingData(loadDataStr);
				$(document).attr("title","会员地址管理");
					var lis = "";
					user.getUser2Local();
					loadAddress(user.member_code,function(data) {
						addressList=data;
						$("#addrListView li").remove();
						for ( var i = 0; i < data.length; i++) {
							var addr = getAddrForJSON(data[i]);
							$("#addrListView").append(
							"<li>"+
							"<a href=\"#editAddr&index="+i+"\">"+
							"<p><strong>收货人：</strong>"+addr.recvName+"</p>"+
							"<p><strong>手机：</strong>"+addr.phone+"</p>"+
							"<p><strong>邮编：</strong>"+addr.postCode+"</p>"+
							"<p><strong>收货地址：</strong>"+data[i].PRONAME+" "+data[i].CITYNAME+" "+data[i].ZONENAME+" "+addr.detail+"</p>"+
							"<p class=\"ui-li-aside\"><strong></strong></p>"+
							"</a>"+
							"</li>"
							).listview("refresh");
						}
						closeDialog();
						//$("#addrListView").listview("refresh");
					});
					for(var i=0;i<$(".title_content").length;i++){
						eval("$(\".title_content:eq("+i+")\").text(orgname);");
					}
			});
			$("#address").bind("pagecreate", function() {
				/**$("#addAddress").on("click",function(){
					$("#addrListView").append('<li><a href=\'#\'>addInsert</li>');
					$("#addrListView").listview("refresh");
				});**/
			});
		</script>
		<div data-role="header" data-theme="c">
				<div class="titleDiv">
				<table style="width:100%;margin-bottom:-0.1em;margin-top:-0.2em;">
				<tr><td><strong text-type="title" class="title_content"></strong></td>
				<td align="right">
				<a href="#editAddr"data-role="button" data-theme="e" data-icon="plus" data-inline="true" data-mini="true" id="addAddress">添加</a>
				</td></tr>
			</table>
			</div>
		</div>
		<div data-role="content">
			<ul data-role="listview" id="addrListView">
			</ul>
		</div>
	</div>
	<!--page end-->

<!--我的订单开始-->
<div data-role="page" id="myOrders">
<script type="text/javascript">
	$("#myOrders").live("pageshow",function(){
		$(document).attr("title","我的订单");
		loadingData(loadDataStr);
		loadOrders(memberCode,function(data){
			$("#myOrdersListView li").remove();
			orderList=data;
			for(var i=0;i<data.length;i++){
				$("#myOrdersListView").append(
						"<li><a href=\"#orderDetail&index="+i+"\">"+
						"<strong class=\"orderTitle\">"+data[i].ORDER_CODE+"</strong><br/><strong>&nbsp;</strong>"+
						"<p>"+data[i].ORGNAME+"</p>"+
						"<p><strong class=\"normalSpan\">"+data[i].PRONAME+data[i].CITYNAME+data[i].ZONENAME+data[i].RECV_DETAIL+"</strong></p>"+
						"<p>"+data[i].DESCRIPTION_TEXT+"</p>"+
						"<p class=\"ui-li-aside\"><strong>"+data[i].ORDERSTATUS+"</strong></p>"+
				"</a></li>").listview("refresh");
			}
			closeDialog();
		});
		for(var i=0;i<$(".title_content").length;i++){
			eval("$(\".title_content:eq("+i+")\").text(orgname);");
		}
	});
</script>
<div data-role="header" data-theme="c">
	<div class="titleDiv">
		<table style="width:100%;margin-bottom:-0.1em;margin-top:-0.2em;">
			<tr><td><strong text-type="title" class="title_content"></strong></td>
			<td align="right">
			</td></tr>
			</table>
	</div>
</div>
	<div data-role="content">
			<!--content start-->
			<div class="content-primary">
				<ul data-role="listview" data-filter="true"	data-filter-placeholder="查找订单"
				  id="myOrdersListView">
				</ul>
			</div>
	</div>
		<!--content end-->
</div>
	<!--我的订单结束-->

<!-- 订单详细信息开始 -->
	<div data-role="page" id="orderDetail">
	<script type="text/javascript">
	$("#orderDetail").live("pageshow",function(){
		//orderDetail&orderid=org201311210009
		//alert($.mobile.path.get($(this).attr("value")));
		$(document).attr("title","订单信息");
		var url=$.mobile.path.get($(this).attr("value"));
		var index=0;
		if(url!=""&&url.length>0){
			var params=url.split("&");
			index=params[1].substr(params[1].indexOf("=")+1);
		}
		//orderList
		var deatilAddr=orderList[index].PRONAME+" "+
			orderList[index].CITYNAME+" "+
			orderList[index].ZONENAME+" "+
			orderList[index].RECV_DETAIL;
		$("#odRecvDeatilAddr").text(deatilAddr);
		$("#odRecvPostCode").text(orderList[index].RECV_POSTAL_CODE);
		$("#odRecvPhone").text(orderList[index].RECV_PHONE);
		$("#odRecvName").text(orderList[index].RECV_NAME);
		$("#odOrderStatus").text(orderList[index].ORDERSTATUS);
		$("#odOrderCountPrice").text(orderList[index].TOTAL);
		$("#odOrderPrice").text(orderList[index].PRICE);
		$("#odOrderComdityName").text(orderList[index].NAME);
		$("#odOrderComdityDesc").text(orderList[index].DESCRIPTION_TEXT);
		$("#odOrderCode").text(orderList[index].ORDER_CODE);
		$("#odOrderAmount").text(orderList[index].AMOUNT);
		$("#odExplain").text(orderList[index].EXPLAIN);
		
		for(var i=0;i<$(".title_content").length;i++){
			eval("$(\".title_content:eq("+i+")\").text(orgname);");
		}
	});
	</script>
<div data-role="header" data-theme="c">
	<div class="titleDiv">
		<table style="width:100%;margin-bottom:-0.1em;margin-top:-0.2em;">
			<tr><td><strong text-type="title" class="title_content"></strong></td>
			<td align="right">
			</td></tr>
			</table>
	</div>
</div>
		<div data-role="content">
			<div class="content-primary">
				<ul data-role="listview">
					<li  data-role="fieldcontaion">
						<h4>订单号:</h4>
						<p id="odOrderCode"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>物品信息:</h4>
					<p id="odOrderComdityName"></p>
					<span id="odOrderComdityDesc" class="normalSpan"></span>
					</li>
					<li data-role="fieldcontaion">
					<h4>单价:</h4>
					<p>￥<span id="odOrderPrice"></span></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>数量:</h4>
					<p><span id="odOrderAmount"></span>件</p>
					</li>
					<li data-role="fieldcontaion">
					<h4>总价:</h4>
					<p>￥<span id="odOrderCountPrice"></span></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>订单状态:</h4>
					<p id="odOrderStatus"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>收货人:</h4>
					<p id="odRecvName"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>手机号码:</h4>
					<p id="odRecvPhone"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>邮编:</h4>
					<p id="odRecvPostCode"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>详细地址:</h4>
	        		 <p id="odRecvDeatilAddr"></p>
					</li>
					<li data-role="fieldcontaion">
					<h4>备注:</h4>
	        		 <p id="odExplain"></p>
					</li>
				</ul>
			</div>
		</div>
	</div>
	<!-- 订单详细信息结束 -->


	<!--保存地址确认开始-->
	<div data-role="page" id="query">
		<div data-role="header">
			<h1>确认保存</h1>
			<strong>你确认要修改保存数据吗！</strong>
			<div class="ui-body ui-body-a">
				<a href="#address" data-role="button" data-inline="true">取消</a><a
					href="#address" data-role="button" data-inline="true"
					data-theme="b">保存</a>
			</div>
		</div>
	</div>
	<!--保存地址确认结束-->
	<!--保存用户信息确认开始-->
	<div data-role="page" id="editUserinfoQuery">
		<div data-role="header">
			<h1>确认保存</h1>
			<strong>你确认要修改保存数据吗！</strong>
			<div class="ui-body ui-body-a">
				<a href="#EditUserinfo" data-role="button" data-inline="true">取消</a><a
					href="#memberInfoS" data-role="button" data-inline="true"
					data-theme="b">保存</a>
			</div>
		</div>
	</div>
	<!--保存用户信息确认结束-->

	<!--商城引导界面开始-->
	<div data-role="page" id="userGuide">
		<div data-role="content" data-theme="d" >
			<ul data-role="listview" data-theme="c" data-inset="true">
				<li data-role="list-divider" style="margin-top:3em;">
				<a href="#" data-role="button" data-theme="e" data-mini="true" data-inline="true" data-icon="alert">提示:</a></h3>
				</li>
				<li>您的身份已过期，请回复<strong>yz</strong>点击登录链接进行登录</li>
			</ul>
		</div>
	</div>
	<!--商城引导界面结束-->
	
	<div data-role="page" id="editAddr">
		<script type="text/javascript">
		var type="insert";
		function clearData(){
			$("#recvPhone").val("");
			$("#recvPostCode").val("");
			$("#recvName").val("");
			$("#addrDetailId").val("");
			$("#provinceid").val("-1").selectmenu("refresh");
			$("#cityid").val("-1").selectmenu("refresh");
			$("#zoneid").val("-1").selectmenu("refresh");
		}
			$("#editAddr").live("pagecreate", function() {
				$("#addrSave").click(function() {
					var addr = new Address();
					addr.provinec = $("#provinceid option:selected").val();
					addr.city = $("#cityid option:selected").val();
					addr.area = $("#zoneid option:selected").val();
					if(isEmpty($("#recvName").val())){
						alertDialog("收货人姓名不可为空");
						return;
					}
					if($("#recvName").val().length>15){
						alertDialog("为了您和他人的方便，请您使用简短点的名称");
						return ;
					}
					if(!isPhone($("#recvPhone").val())){
						if(isEmpty($("#recvPhone").val())){
							alertDialog("手机号码不可以为空");
							return ;
						}
						alertDialog("请检查输入手机号码");
						return ;
					}
					if($("#provinceid").val()==-1||$("#cityid").val()==-1||$("#zoneid").val()==-1){
						alertDialog("请选择正确的地址");
						return;
					}
					
					addr.phone = $("#recvPhone").val();
					if(!isEmpty($("#recvPostCode").val())&&!isPostCode($("#recvPostCode").val())){
						alertDialog("请检查您输入的邮编");
						return ;
					}
					if(!isEmpty($("#addrDetailId").val())&&$("#addrDetailId").val().length>60){
						alertDialog("详细地址信息太长");
						return ;
					}
					addr.postCode = $("#recvPostCode").val();
					addr.memberCode = user.member_code;
					//alert(user.member_code);
					var recvName=$("#recvName").val();
					if(!isEmpty(recvName)){
						recvName=recvName.trim();
					}
					
					addr.recvName = encodeURIComponent(recvName);
					addr.detail = encodeURIComponent($("#addrDetailId").val());
					if(type=="update"){
						addr.addresId=$("#edAddrId").val();
					}
					loadingData("正在保存数据");
					saveAddress(addr,type,function(data) {
						closeDialog();
						$.mobile.changePage("#address", "slideup");
					});
				});
			});
	$("#editAddr").live("pageshow",function() {
		function initAddr(addr){
			var privaceID=addr.provinec;
			loadProvinces(function(data) {
				var options = "";
				$("#provinceid option:gt(0)").remove();
				console.log("$(\"#provinceid option:gt(0)\").remove();");
				//$("#provinceid option").remove();
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].PROSORT+"' >"+ data[i].PRONAME + "</option>";
				}
				$("#provinceid").append(options);
				//$("#provinceid option:eq(22)").attr("selected", true);
				$("#provinceid").selectmenu("refresh");
				//$("#provinceid").trigger("change");
				$("#provinceid").val(privaceID).selectmenu("refresh");
				loadCityByProvinec(privaceID,function(data){
					var cityId=addr.city;
					$("#cityid option:gt(0)").remove();
					var options="";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].CITYSORT+"' >"+ data[i].CITYNAME + "</option>";
					}
					$("#cityid").append(options);
					$("#cityid").val(cityId).selectmenu("refresh");
					loadZoneByCityId(cityId,function(data){
						$("#orderRecvZone option:gt(0)").remove();
						var options="";
						for ( var i = 0; i < data.length; i++) {
							options += "<option value='"+data[i].ZONEID+"' >"+ data[i].ZONENAME + "</option>";
						}
						$("#zoneid").append(options);
						$("#zoneid").val(addr.area).selectmenu("refresh");
					});
				});
			});
		}
		
		$("#provinceid").change(function() {
			loadCityByProvinec($("#provinceid option:selected").val(),function(data) {
			var options = "";
			$("#cityid option:gt(0)").remove();
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].CITYSORT+"' >"+ data[i].CITYNAME+ "</option>";
			}
			$("#cityid").append(options);
			$("#cityid").trigger("change");
		});
	});
	$("#cityid").change(function() {
		loadZoneByCityId($("#cityid option:selected").val(),function(data) {
		var options = "";
		$("#zoneid option:gt(0)").remove();
		for ( var i = 0; i < data.length; i++) {
			options += "<option value='"+data[i].ZONEID+"' >"+ data[i].ZONENAME+ "</option>";
		}
		$("#zoneid").append(options);
		$("#zoneid").selectmenu("refresh");
	});
	});
	var url=$.mobile.path.get($(this).attr("value"));
	var index=0;
	if(url.indexOf("index=")>0){
		type="update";
		//clearData();
		index=url.substr(url.indexOf("=")+1,url.length);
		var addr=getAddrForJSON(addressList[index]);
		//$("#showProvince").show();
		//$("#showProvince").val(addr.pcz);
		$("#recvPhone").val(addr.phone);
		$("#recvPostCode").val(addr.postCode);
		$("#recvName").val(addr.recvName);
		$("#addrDetailId").val(addr.detail);
		$("#edAddrId").val(addr.addresId);
		$("#provinceid").val(addr.provinec);
		//$("#cityid").val(addr.city);
		//$("#zoneid").val(addr.area);
		initAddr(addr);
		$("#provinceid").selectmenu("refresh");
		$("#cityid").selectmenu("refresh");
		$("#zoneid").selectmenu("refresh");
	}else{
		type="insert";
		//$("#showProvince").hide();
		clearData();
		
		loadProvinces(function(data) {
			var options = "";
			$("#provinceid option:gt(0)").remove();
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].PROSORT+"' >"+ data[i].PRONAME + "</option>";
			}
			$("#provinceid").append(options);
			$("#provinceid").selectmenu("refresh");
			$("#provinceid").trigger("change");
		});
	}
	
	for(var i=0;i<$(".title_content").length;i++){
		eval("$(\".title_content:eq("+i+")\").text(orgname);");
	}
});
		</script>
		<!-- 我的地址编辑页面 开始-->
		<div data-role="content">
			<!--content开始-->
			<div data-role="collapsible" data-collapsed="false" data-content-theme="c" data-theme="c" data-collapsed-icon="arrow-r"  >
				<h3>地址管理</h3>
				<div>
				</div>
				<!--  input type="text" id="showProvince" value="" disabled="disabled"-->
				<form action="#" method="get" data-theme="c" data-mini="true">
					<fieldset data-role="controlgroup" data-type="horizontal">
						<div data-role="fieldcontain" data-mini="true">
							<select name="province" id="provinceid" data-mini="true" >
								<option value="-1" >请选择</option>
							</select> <select name="city" data-mini="true" id="cityid">
								<option value="-1" >请选择</option>
							</select> <select name="zone" id="zoneid" data-mini="true">
								<option value="-1" >请选择</option>
							</select>
						</div>
					</fieldset>
					<input type="text" id="recvName"  placeholder="请输入收货人姓名" /> 
					<input type="text" id="recvPhone"  placeholder="请输入收货人手机号码" /> 
					<input type="text" id="recvPostCode" placeholder='请输入邮政编码' />
					<input type="hidden" id="edAddrId"/>
					<textarea name="addrDetail" id="addrDetailId"  placeholder="请输入收货人详细地址"></textarea>
					<a href="#" data-rel="dialog" data-role="button" data-theme="b"	data-mini="true" data-inline="true" id="addrSave">保存</a>
				</form>
			</div>
		</div>
		<!--content结束-->
	</div>
	<!-- 我的地址编辑页面 结束-->
	<div data-role="page" id="test">
	<div data-role="content">
	<form action="" data-role="none">
	<ul  data-role="listview">
	<li>
	<label for="email" data-role="none" >Email:</label>
		<input type="text" name="email" id="email" value="" input-type="input" data-role="none" />
	</li>
	<li>
	<label for="email" data-role="none" >Email:</label>
		<input type="text" name="email" id="email" value="" input-type="input" data-role="none" />
	</li>
		
	</ul>
	</form>
	</div>
	</div>
	<!--会员冻结-->
	<div data-role="page" id="userFreeze">
		<div data-role="content" data-theme="d" >
			<ul data-role="listview" data-theme="c" data-inset="true">
				<li data-role="list-divider" style="margin-top:3em;">
				<a href="#" data-role="button" data-theme="e" data-mini="true" data-inline="true" data-icon="alert">提示:</a></h3>
				</li>
				<li>您的账号已经冻结，请联系管理员。</li>
			</ul>
		</div>
	</div>
	<!--会员冻结结束-->
	<!--会员锁定-->
	<div data-role="page" id="userLocked">
		<div data-role="content" data-theme="d" >
			<ul data-role="listview" data-theme="c" data-inset="true">
				<li data-role="list-divider" style="margin-top:3em;">
				<a href="#" data-role="button" data-theme="e" data-mini="true" data-inline="true" data-icon="alert">提示:</a></h3>
				</li>
				<li>您的账号已经锁定，请联系管理员。</li>
			</ul>
		</div>
	</div>
	<!--会员冻结锁定-->
</body>
</html>