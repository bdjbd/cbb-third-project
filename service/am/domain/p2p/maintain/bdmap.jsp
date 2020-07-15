<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="com.p2p.Utils,com.fastunit.jdbc.DBFactory,com.fastunit.jdbc.exception.JDBCException,com.google.gson.JsonObject,com.google.gson.JsonArray,java.sql.SQLException"%>
<%@ page import="com.p2p.service.DataService" %>
<%@ page import="com.p2p.order.*" %>
<%!
String getOrderStatus(String OrderCode)
{
	String tValue="";
	try 
	{
		String tSql="select data_status from  ws_order where order_code='" + OrderCode + "'";

		tValue=DataService.getDBTableTopRowField(tSql,DBFactory.getDB());

	} 
	catch (JDBCException e) 
	{
		e.printStackTrace();
	}

	return tValue;
}

%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
	<style type="text/css">
		body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;font-family:"微软雅黑";}
	</style>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=M6VBk7BLei5ySc1Ni7pA4NQZ"></script>
	<script type="text/javascript" src="/P2P_WEB/js/jquery-1.8.3.js"></script>
	<title>手工派单</title>
</head>
<body>
	<div id="allmap"></div>
	
	<script type="text/javascript">
	var members=new Array();
	/**派单**/
	function dispatcheOrder(orderCode,memberCode)
	{
		$.get("/p2p/manpowerDispatcherOrder.do?orderCoder="+orderCode+"&memberCode="+memberCode,function(data){
			alert('派单成功！');
			window.close();
		});
	}
	
	var comdityId=<%=request.getParameter("comdity_id")%>;
	var orderCode=<%=request.getParameter("ws_orderw.form.order_code")%>;
	////=request.getParameter("odstatus")//订单状态
	var odstatus=<%=getOrderStatus(request.getParameter("ws_orderw.form.order_code"))%>;

	<%
	try 
	{
		
		String sql="";
		if("2".equals(request.getParameter("odstatus"))){
		  sql="SELECT * FROM ws_member WHERE member_code IN (SELECT"+   
				" member_code FROM p2p_MemberAbility "+  
				" WHERE serbver_code =( "+
				" SELECT comdity_id FROM ws_order "+ 
				" WHERE order_code='"+request.getParameter("ws_orderw.form.order_code")+"'))"+
				" AND orgid='ekx' AND type=1  AND recvstatus=2 ";
		}else{
			sql="SELECT * FROM ws_member WHERE member_code=("+
			" SELECT member_code FROM  p2p_DispatchRecod WHERE order_code='"+request.getParameter("ws_orderw.form.order_code")+"' )";
			}
	
		System.out.print(sql);
		%>
		var result='<%out.print(Utils.ResultSetToJsonObject(DBFactory.getDB().getResultSet(sql)));%>';
		<%
		String orderSQL="SELECT * FROM ws_order WHERE order_code='"+request.getParameter("ws_orderw.form.order_code")+"'";
		out.print("var order='"+Utils.ResultSetToJsonObject(DBFactory.getDB().getResultSet(orderSQL))+"';");
	} 
	catch (JDBCException e) 
	{
		e.printStackTrace();
	}
	%>
	
	//解析订单信息
	var orderData=JSON.parse(order);
	
	
	// 百度地图API功能
	var map = new BMap.Map("allmap");// 创建Map实例
	if(orderData.result.length>0){
		//longitud,order.latitude
		map.centerAndZoom(new BMap.Point(orderData.result[0].longitud,orderData.result[0].latitude),13);
	}else{
		map.centerAndZoom("西安",13);  // 初始化地图,用城市名设置地图中心点
	}
	map.enableScrollWheelZoom();
	
	var sContent=
		"<h4 style='margin:0 0 5px 0;padding:0.2em 0'>会员信息</h4>" + 
		"<table><tr>"+
		"<td rowspan='3'><img style='margin:-4px' id='memberImg' src='/P2P_WEB/img/member/men.jpg'/></td>"+
		"<td><span style='font-size:14px;'>名称：</span></td>"+
		"</tr><tr><td><span style='font-size:14px;'>电话</span></td></tr>"+
		"<tr><td><span style='font-size:14px;'>工作状态</span></td></tr>"+
		"</table>";
	
	//绘制会员图标
	var memberIcon=new BMap.Icon("member.png",new BMap.Size(27,27),{anchor:new BMap.Size(13,27)});

	function addMarker(member)
	{
		var p= new BMap.Point(member.last_logintube,member.last_lautitube);
		
		
		var mk=new BMap.Marker(p,{icon:memberIcon});
		
		
		
		
		var showDispatcherStr="";
		
		//订单状态==2并且会员工作状态==2，显示派单
		var showDispatcherOrderBtn=odstatus==2&&member.recvstatus==2;
		//alert("showDispatcherOrderBtn=" + showDispatcherOrderBtn);
		if(!showDispatcherOrderBtn)
		{
			showDispatcherStr=" style='display:none'";
		}
		
		var infoWindow=new BMap.InfoWindow("<h4 id='"+member.member_code+"' style='margin:0 0 5px 0;padding:0.2em 0'>维修人员信息</h4>" + 
				" <table><tr>"+
				" <td rowspan='3'><img style='margin:0px;width:80px;height:auto;' id='mark_img_"+member.member_code+"' src='/P2P_WEB/img/member/men.jpg'/></td>"+
				" <td><span>姓名："+member.mem_name+"</span></td>"+
				" </tr><tr><td><span>电话:"+member.phone+"</span></td></tr>"+
				" <tr><td><span>状态:"+(member.recvstatus==2?"待命中":"工作中")+"</span></td></tr>"+
				" </table>"+
				" <button  onclick='dispatcheOrder("+orderCode+","+member.member_code+")' "+showDispatcherStr+">派单</button>"
				,{enableMessage:false});
		mk.addEventListener('click',function(){
			this.openInfoWindow(infoWindow);
			var id=infoWindow.getContent().substring(8,infoWindow.getContent().indexOf('style=\'marg')-2);
			id="mark_img_"+id;
			document.getElementById(id).onload=function(){infoWindow.redraw();};
		},false);
		map.addOverlay(mk);
	}
	//获取可以接到人的信息
	var data=JSON.parse(result);
	if(data.result.length>0){
		for(var i=0;i<data.result.length;i++){
			addMarker(data.result[i]);
		}
	}
	
	//绘制订单坐标
	function addOrderMarker(order){
		//{"result":[{"order_code":"1409304378226","recv_detail":"未央区广安路新房村","longitud":"109.0293","latitude":"34.3178"}]}
		var p= new BMap.Point(order.longitud,order.latitude);
		var mk=new BMap.Marker(p);
		var infoWindowOrder=new BMap.InfoWindow(
				" <h4 style='margin:0 0 5px 0;padding:0.2em 0'>订单信息</h4>" + 
				" <table>"+
				" <tr><td><span'>订单编号："+order.order_code+"</span></td></tr>"+
				" <tr><td><span>收货人:"+order.recv_name+"</span></td></tr>"+
				" <tr><td><span>联系电话:"+order.recv_phone+"</span></td></tr>"+
				" <tr><td><span>收货地址:"+order.recv_detail+"</span></td></tr>"+
				" </table>",{enableMessage:false}
				);
		mk.addEventListener('click',function(event){
			this.openInfoWindow(infoWindowOrder);
		},false);
		var label = new BMap.Label("订单:"+order.order_code,{offset:new BMap.Size(20,-10)});
		mk.setLabel(label);
		
		map.addOverlay(mk);
	}
	
	if(orderData.result.length>0){
		addOrderMarker(orderData.result[0]);
	}
</script>
</body>
</html>

