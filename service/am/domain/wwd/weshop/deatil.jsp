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
<title>商品介绍</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, minimum-scale=1.0 maximum-scale=1.0">
<meta http-equiv="pragma" content="no-cache">     
<meta http-equiv="cache-control" content="no-cache,must-revalidate">   
<meta http-equiv="expires" content="0">   

<link rel="stylesheet"	href="/domain/wwd/weshop/jquery.mobile-1.3.2.min.css"/>
<link rel="stylesheet"  href="/domain/wwd/weshop/weshop.css"/>
<script src="/domain/wwd/weshop/jquery-1.7.1.min.js"></script>
<script	src="/domain/wwd/weshop/jquery.mobile-1.3.2.min.js"></script>
<script src="weshop.js"></script>
<script type="text/javascript">
	var comdity=new Object();
	var orgid='<%=session.getAttribute("CURRENT_ORG")%>';
	var user=new User();
	var address=new Address();
	var addressList=new Object();
	var userInfo=<%=session.getAttribute("JSON_USER_INFO")%>;
	var cid="";
	var pid="";
	var orgname="";
	var userStatus="1";
	<%
	if(request.getParameter("orgidp")!=null&&request.getParameter("orgidp").toString().length()>0){
		WeShopServer wss=new WeShopServer();
		out.print("orgname='"+wss.getOrgnameByOrgid(request.getParameter("orgidp"))+"';");
	}
	%>
	var memberInfoSS=<%=session.getAttribute("CURRENT_MEMBER_INFO")%>;
	function showTitle(){
		var cs=document.getElementsByClassName("weshop_title_canvas");
		for(var i=0;i<cs.length;i++){
			var ctx=cs[i].getContext("2d");
			ctx.clearRect(0,0,cs[i].width,cs[i].height);
			ctx.font="22px Verdana";
			ctx.shadowBlur=1.5;
			ctx.fillText(orgname,10,30);
		}
	}
</script>
<style>
.showCmdityImags {
	width:100%;
	height:auto;
}
.price {
	color:#f4f4f4;
	font-size: +1;
	font-style: oblique;
}
.colgroup1{
	width:40%;
}
.colgroup2{
	width:60%;
}
.ui-content{
	background:white;
}
.liImg{
	padding-left:7px;
	padding-right:7px;
}

input[id="orderRecvName"]::-webkit-input-placeholder { color:red;}
input[id="orderRecvPhone"]::-webkit-input-placeholder { color:red;}
textarea[id="orderRecvDetail"]::-webkit-input-placeholder { color:red;}
</style>
</head>
<body>
<div data-role="page" id="commidtyDetail" >
<script type="text/javascript">
	$("#commidtyDetail").live("pageshow",function(){
		$(document).attr("title", "商品介绍");
		loadingData(loadDataStr)
		var url=location.search;
		cid='<%=request.getParameter("cid")%>';
		pid='<%=request.getParameter("pid")%>';
		if(isEmpty(memberInfoSS)||isEmpty(memberInfoSS[0].OPENID)){
			$("#buy_step1").attr("href","#notLogin");
		}else{
			getUserStatus(orgid,memberInfoSS[0].OPENID,function(data){
				userStatus=data;
				 if(data==2||data==4){
						$("#buy_step1").attr("href","#userFreeze");
				 }else if(data==3){
							closeDialog();
							user.data_status=2;
							user.saveUser();
							$.mobile.changePage("#userLocked");
							return;
				 }else{
					 $("#buy_step1").attr("href","#buy_step1?cid="+cid);
				 }
			});		
		}
		getComdityById(cid,function(data){
			comdity=data;
			$("#deComditName").text(data[0][0].NAME);
			$("#dePrice").text(data[0][0].PRICE);
			$("#deDiscriptionText").text(data[0][0].DESCRIPTION_TEXT);
			$("#deComdityNorm").text(data[0][0].COMDITY_NORM);
			$("#orders").text(data[0][0].ORDERS);
			$("#deComditImages li").remove();
			for(var i=1;i<7;i++){
				var p=""+i;
				var src=data[1][p];
				if(src!=null&&src!=""&&src.substr(src.length-4).indexOf("jpg")>0){
					if(i===1){
						$("#first_img").attr("src",src+"?t="+Math.random());
					}else{
						$("#deComditImages").append(
						<%-- "<li><div>"+--%>
								"<p class='liImg'><img class=\"showCmdityImags\" src=\""+src+"\" /></p>"
						<%--	+"</div></li>"--%>
							).listview("refresh");
					}
				}
			}
			closeDialog();
		});
		user.getUser2Local();
		showTitle();
	});
	
	<%--
	$("#commidtyDetail").live("pagecreate",function(){
		$("#graphicA").click(function(){
			if($("#graphicDiv").css("display")=="none"){
				$("#graphicDiv").show();
			}else{
				$("#graphicDiv").hide();
			}
		});
		});
	--%>
</script>
<div data-role="head" >
	<div id="weshop_log" class="weshop_title">
		<canvas class="weshop_title_canvas" >
			<h1 class="title_content"> </h1>
		</canvas>
	</div>
</div>
<div data-role="content">
<div >
	<span id="deComditName"></span>
	<div class="ui-grid-solo">
		<span >订单量：<samp id="orders"></samp></span>
	</div>
	<div style="width:100%;height: auto;">
		<img class="showCmdityImags" id="first_img" src="">
	</div>
	<div class="shop_operator">
	<p style="display:inline;"><span style="font-weight: bold;">单价:<span></span><div class="ui-li-pric-p-p"><span>￥<span id="dePrice"></span></span></div></p>
	<a data-role="button" href="#"  id="buy_step1"  data-theme="e" data-transition="slidefade" >立即购买</a>
	</div>
	<div>
	<br>
		<%-- <a href="#" id="graphicA">图文详情>></a>--%>
		<p>图文详情</p>
	</div>
</div>
<div id="graphicDiv" ><%--style="display:none;" --%>
		<ul data-role="listview" data-inset="true" data-divider-theme="b">
			<li>
			<span>商品描述</span><br>
				<span id="deDiscriptionText" style="font-weight: normal;word-warp:break-word;word-break:break-all"></span>
			</li>
			<li>
			<span>商品规格</span><br>
			<span id="deComdityNorm" style="font-weight: normal;word-warp:break-word;word-break:break-all"></span>
			</li>
		</ul>
		<br>
		<ul data-role="listview" data-divider-theme="b" data-inset="false" id="deComditImages">
			<li data-role="list-divider" data-icon="delete">图片</li>
		</ul>
</div>
<br>
<br>
</div>
	<!--content end-->
</div>
<!-- /page -->

<!--商品购买界面1-->
<div data-role="page" id="buy_step1" data-theme="d" data-content-theme="c">
<script type="text/javascript">
$("#buy_step1").live("pagebeforeshow",function(event){
		var isSubmitOrder=false;
		$(document).attr("title", "下订单");
		//地址初始化方法
		function initRecvAddr(id){
			var privaceID=addressList[id].RECV_PROVINCE;
			$("#orderRecvProvince").val(privaceID).selectmenu("refresh");
			loadCityByProvinec(privaceID,function(data){
				var cityId=addressList[id].RECV_CITY;
				$("#orderRecvCity option:gt(0)").remove();
				var options="";
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].CITYSORT+"' >"+ data[i].CITYNAME + "</option>";
				}
				$("#orderRecvCity").append(options);
				$("#orderRecvCity").val(cityId).selectmenu("refresh");
				loadZoneByCityId(cityId,function(data){
					$("#orderRecvZone option:gt(0)").remove();
					var options="";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].ZONEID+"' >"+ data[i].ZONENAME + "</option>";
					}
					$("#orderRecvZone").append(options);
					$("#orderRecvZone").val(addressList[id].RECV_AREA).selectmenu("refresh");
				});
			})
		}
		
		$("#step1Name").text(comdity[0][0].NAME);
		$("#step1ComditNorm").text(comdity[0][0].COMDITY_NORM);
		$("#step1Price").text(comdity[0][0].PRICE);
		user.getUser2Local();
		if(user.member_code==null||user.member_code==""||user.member_code=="undefined"){
			//location.href="member.html?#userGuide";
		}
		$("#selectAddr").click(function(){
			$("#addrsPanel").panel("open");
		});
		
		$("#submitOrder").hide();
		$("#orderCancle").hide();
		$("#orderConfrim").show();
		
		loadAddress(memberInfoSS[0].MEMBER_CODE,function(data) {
			addressList=data;
			$("#addrListView li:gt(0)").remove();
			for ( var i = 0; i < data.length; i++) {
			var addr = getAddrForJSON(data[i]);
			$("#addrListView").append(
			"<li>"+
			"<a class=\"selecteda\" addId=\""+i+"\" href=\"#\">"+
			"<p><strong>收货人：</strong>"+addr.recvName+"</p>"+
			"<p><strong>手机：</strong>"+addr.phone+"</p>"+
			"<p><strong>邮编：</strong>"+addr.postCode+"</p>"+
			"<p><strong>收货地址：</strong>"+addr.pcz+addr.detail+"</p>"+
			"<p class=\"ui-li-aside\"><strong></strong></p>"+
			"</a>"+
			"</li>"
			).listview("refresh");
		}
		$("#addAddress").click(function(){
			$("#addrsPanel").panel("close");
		});
		$(".selecteda").click(function(){
			$("#orderRecvName").val(addressList[$(this).attr("addId")].RECV_NAME);
			$("#orderRecvPhone").val(addressList[$(this).attr("addId")].RECV_PHONE);
			$("#orderRecvPostCode").val(addressList[$(this).attr("addId")].RECV_POSTAL_CODE);
			$("#orderRecvDetail").text(addressList[$(this).attr("addId")].RECV_DETAIL);
			var id=$(this).attr("addId");
			initRecvAddr(id);
			$("#addrsPanel").panel("close");
		});
	});
		loadProvinces(function(data) {
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].PROSORT+"' >"+ data[i].PRONAME + "</option>";
			}
			$("#orderRecvProvince").append(options);
			$("#orderRecvProvince").selectmenu("refresh");
		});
		$("#orderRecvProvince").change(function() {
			loadCityByProvinec($("#orderRecvProvince option:selected").val(),function(data) {
			var options = "";
			$("#orderRecvCity option:gt(0)").remove();
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].CITYSORT+"' >"+ data[i].CITYNAME+ "</option>";
			}
			$("#orderRecvCity").append(options);
			$("#orderRecvCity").selectmenu("refresh");
			$("#orderRecvCity").trigger("change");
		});
	});
	$("#orderRecvCity").change(function() {
		loadZoneByCityId($("#orderRecvCity option:selected").val(),function(data) {
		var options = "";
		$("#orderRecvZone option:gt(0)").remove();
		for ( var i = 0; i < data.length; i++) {
			options += "<option value='"+data[i].ZONEID+"' >"+ data[i].ZONENAME+ "</option>";
		}
		$("#orderRecvZone").append(options);
		$("#orderRecvZone").selectmenu("refresh");
	});
	});
	$("#orderConfrim").click(function(){
		if(isEmpty($("#orderAmount").val())||!isNumber($("#orderAmount").val())||$("#orderAmount").val()<1||$("#orderAmount").val()>1000){
			alertDialog("商品数量在1到1000之间的数字");
			return;
		}else if(isEmpty($("#orderRecvName").val())){
			alertDialog("收货人不可以为空");
			return;
		}else if(isEmpty($("#orderRecvPhone").val())){
			alertDialog("收货人手机号码不可以空");
			return;
		}else if(isEmpty($("#orderRecvProvince").val())){
			alertDialog("收货人省,市,区不可以为空");
			return;
		}else if(!isPhone($("#orderRecvPhone").val())){
			alertDialog("请输入正确的手机号码");
			return;
		}else if(!isEmpty($("#orderRecvPostCode").val())&&!isPostCode($("#orderRecvPostCode").val())){
			alertDialog("请输入正确的邮编");
			return;
		}else if($("#orderRecvProvince option:selected").val()==-1) {
			alertDialog("请选择正确的地址");
			return ;
		}else if($("#orderRecvCity option:selected").val()==-1){
			alertDialog("请选择正确的地址");
			return;
		}else if($("#orderRecvZone option:selected").val()==-1){
			alertDialog("请选择正确的地址");
			return;
		}else if(isEmpty($("#orderRecvDetail").val())){
				alertDialog("详细地址信息不能为空");
				return;
		}else if(new Number($("#orderAmount").val())*new Number($("#step1Price").text())>9999999.99){
			alertDialog("交易金额太大，为保证你的财产安全，请您直接联系商家。");
			return ;
		}else if(!isEmpty($("#orderRecvDetail").val())&&$("#orderRecvDetail").val().length>60){
			alertDialog("详细地址信息太长");
		}
		$("#orderRecvName").attr("disabled","disabled");	
		$("#orderRecvPhone").attr("disabled","disabled");	
		$("#orderRecvPostCode").attr("disabled","disabled");	
		$("#orderRecvDetail").attr("disabled","disabled");	
		$("#orderRecvProvince").attr("disabled","disabled");	
		$("#orderRecvCity").attr("disabled","disabled");	
		$("#orderRecvZone").attr("disabled","disabled");
		$("#orderAmount").attr("disabled","disabled");
		$("#orderExplain").attr("disabled","disabled");
		$("#orderInfoListView li:eq(1)").hide();
		$("#orderConfrim").hide();
		$("#submitOrder").show();
		$("#orderCancle").show();
	});
	
	$("#orderCancle").click(function(){
		$("#submitOrder").hide();
		$("#orderCancle").hide();
		$("#orderConfrim").show();
		$("#orderRecvName").removeAttr("disabled");	
		$("#orderRecvPhone").removeAttr("disabled");	
		$("#orderRecvPostCode").removeAttr("disabled");	
		$("#orderRecvDetail").removeAttr("disabled");	
		$("#orderRecvProvince").removeAttr("disabled");	
		$("#orderRecvCity").removeAttr("disabled");	
		$("#orderRecvZone").removeAttr("disabled");
		$("#orderExplain").removeAttr("disabled");
		$("#orderAmount").removeAttr("disabled");
		$("#orderInfoListView li:eq(1)").show();
		$("#orderInfoListView").listview("refresh");
	});
	
	$("#submitOrder").click(function(){
		if(isSubmitOrder){
			loadingData("订单已提交，请等待.");
			setTimeout("closeAndOrders()",2000);
		}else{
			isSubmitOrder=true;
			var order=new Object();
			order.MEMBER_CODE=memberInfoSS[0].MEMBER_CODE;
			order.COMDITY_ID=cid;
			var recvName=$("#orderRecvName").val();
			if(!isEmpty(recvName)){
				recvName=recvName.trim();
			}
			order.RECVNAME=encodeURIComponent(recvName);
			order.RECVPHONE=$("#orderRecvPhone").val();
			order.RECVPOSTCODE=$("#orderRecvPostCode").val();
			order.RECVDETAIL=encodeURIComponent($("#orderRecvDetail").val());
			order.RECVPROVINCE=$("#orderRecvProvince option:selected").val();
			order.RECVCITY=$("#orderRecvCity option:selected").val();
			order.RECVZONE=$("#orderRecvZone option:selected").val();
			order.AMOUNT=$("#orderAmount").val();
			order.EXPALIN=encodeURIComponent($("#orderExplain").val());
			order.ORGIDP='<%=session.getAttribute("CURRENT_ORG")%>';
			submitOrders(order,function(data){
				loadingData("订单提交成功，您的订单号是:"+data);
				setTimeout("closeAndOrders()",2000);
			});
		}
	});
	user.getUser2Local();
	showTitle();
	});
	
	function closeAndOrders(){
		closeDialog();
		location.href="member.jsp?token="+user.token+"&mid="+user.member_code+"&orgidp="+orgid+"&openid="+memberInfoSS[0].OPENID+"#myOrders";
	}
	document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
		
		WeixinJSBridge.call('hideOptionMenu');
	});
	
</script>
	<div data-role="panel" id="addrsPanel" data-display="overlay" data-position="right">
		<ul data-role="listview" id="addrListView">
				<li >
				<div>
					<a href="#" data-role="button" data-theme="a" data-icon="delete" data-inline="true"	data-mini="true" id="addAddress">返回</a>
				</div>
				</li>
		</ul>
	</div>
<div data-role="head" >
	<div id="weshop_log" class="weshop_title">
		<canvas class="weshop_title_canvas" >
			<h1 class="title_content"> </h1>
		</canvas>
	</div>
</div>
	<div data-role="content" style="background: white;">
		<ul data-role="listview" >
			<li style="background: white;">
			<span id="step1Name" style="white-space:normal; font-weight:bold;font-size:1em;"></span><br>
			<div style="font-family: inherit;font-size:1em;font-weight:normal;font-size:0.8em;" id="step1ComditNorm"></div>
			<br>
			<p class="ui-li-aside" style="color:#ab2b2b;font-size:1em;">￥<span id="step1Price"></span></p>
			</li>
		</ul>
		<div data-role="ui-block-a">
			<strong> </strong>
		</div>
		<ul data-role="listview" id="orderInfoListView">
			<li>
				<table>
				<colgroup class="colgroup1"></colgroup>
				<colgroup class="colgroup2"></colgroup>
				<tr>
				<td><label for="name"><strong>数量:</strong>	</label> 
				</td>
				<td>
				<input type="text"  name="orderAmount" id="orderAmount" data-mini="true" data-inline="true" value="1"  />
				</td>
				</tr>
				</table>
			</li>
			<li>
				<a href="#" id="selectAddr"  >选择常用地址</a>
			</li>
			<li data-role="fieldcontaion">
			<table>
			<colgroup class="colgroup1"></colgroup>
			<colgroup class="colgroup2"></colgroup>
			<tr>
			<td><label for="name"><strong>收货人:</strong>
			</label> </td>
			<td>
			<input type="text"  name="orderRecvName" id="orderRecvName" data-mini="true" data-inline="true" value="" placeholder="请输入收货人姓名"  />
			</td>
			</tr>
			</table>
			</li>
			<li data-role="fieldcontaion">
			<table>
			<colgroup  class="colgroup1"></colgroup>
			<colgroup  class="colgroup2"></colgroup>
			<tr>
			<td><label for="name"><strong>手机号码:</strong>
			</label> </td>
			<td>
			<input type="text" name="text"  id="orderRecvPhone" data-mini="true" data-inline="true" value="" placeholder="请输入收货人手机"  />
			</td>
			</tr>
			</table>
			</li>
			<li data-role="fieldcontaion">
			<table>
			<colgroup class="colgroup1"></colgroup>
			<colgroup class="colgroup2"></colgroup>
			<tr>
			<td><label for="name"><strong>邮编:</strong>
			</label> </td>
			<td>
			<input type="text" name="postCode" id="orderRecvPostCode" data-mini="true" data-inline="true" value=""  />
			</td>
			</tr>
			</table>
			</li>
			<li>
			<!--从会员信息里面带数据过来  DivStart-->
			<fieldset data-role="controlgroup" data-type="horizontal">
				<div data-role="fieldcontain" data-mini="true" data-inset="true" data-theme="c">
					<select name="province" id="orderRecvProvince" data-mini="true" data-theme="c">
							<option value="-1" >请选择</option>
							</select> 
					<select name="city" data-mini="true" id="orderRecvCity" data-theme="c">
								<option value="-1" >请选择</option>
							</select> 
					<select name="zone" id="orderRecvZone" data-mini="true" data-theme="c">
								<option value="-1" >请选择</option>
							</select>
						</div>
					</fieldset>
			</li>
			<li> 
				<h4>详细地址:</h4>
				<textarea name="data_address" placeholder="请输入收货人详细地址" id="orderRecvDetail"></textarea>
			</li>
			<li> 
				<h4>备注:</h4>
				<textarea name="explain" placeholder="请输入备注" id="orderExplain"></textarea>
			</li>
			<li id="orderConfrimLi">
				<div data-role="controlgroup" ><!--015f5a  d94f04 -->
					<a href="#"  id="orderConfrim" data-role="button" data-theme="b" data-mini="false" style="background:#015f5a;font-family: ">确认订单</a>
				</div>
				<!--  div data-role="controlgroup" data-type="horizontal" style="text-align: center;" -->
				<div>
					<a href="#"  id="submitOrder" data-icon="check"  data-role="button"  data-theme="b" data-mini="false" style="background:#015f5a;">提交</a>
				</div>
				<div>
					<a href="#"  id="orderCancle" data-icon="delete" data-role="button"  data-theme="c" data-mini="false">取消</a>				 
				</div>
			</li>
		</ul>
	</div>
</div>
	<!--商品购买界面1 结束-->
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
<!--会员没有登录提示-->
<div data-role="page" id="notLogin">
	<div data-role="content" data-theme="d" >
		<ul data-role="listview" data-theme="c" data-inset="true">
			<li data-role="list-divider" style="margin-top:3em;">
			<a href="#" data-role="button" data-theme="e" data-mini="true" data-inline="true" data-icon="alert">提示:</a></h3>
			</li>
			<li>您未登录，请先返回微信回复yz验证登录后再购买商品。</li>
		</ul>
	</div>
</div>

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