<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="com.wisdeem.wwd.WeChat.Utils,com.fastunit.jdbc.DB,com.fastunit.jdbc.DBFactory,com.fastunit.jdbc.exception.JDBCException" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
	<style type="text/css">
		body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;font-family:"微软雅黑";}
	</style>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=M6VBk7BLei5ySc1Ni7pA4NQZ"></script>
	<script type="text/javascript" src="jquery-1.8.3.js"></script>
	<title>手工派单</title>
</head>
<body>
	<div id="allmap"></div>
	
	<script type="text/javascript">
	var orderCode="<%=request.getParameter("orderid")%>";
	
	/**派单**/
	function dispatcheOrder(orderCode,acceptOrderID)
	{
		var url="/AmRes/com.am.mall.iwebAPI.DispatcherOrderIWebAPI.do?orderId="+orderCode+"&acceptOrderID="+acceptOrderID;
		$.get(url,function(data){
			
			var msg="";
			if(data.indexOf('true')>-1){
				msg="派单成功！";
			}else{
				msg="派单失败，请刷新界面检查订单状态。";
			}
			alert(msg);
			window.parent.location.reload();
			window.close();
			
		});
	}
	<%
	//订单信息查询SQL
	String orderSQL="SELECT * FROM mall_MemberOrder  WHERE id='"+request.getParameter("orderid")+"'";
	//加盟商信息
	String storeSQL="SELECT ms.*,org.orgname FROM mall_Store AS ms LEFT JOIN aorg AS org ON org.orgid=ms.orgcode  WHERE ms.orgcode LIKE '"+request.getParameter("orgid")+"%'";
	try {
		String orderInfo=Utils.ResultSetToJsonObject(DBFactory.getDB().getResultSet(orderSQL)).toString();
		String storeInfo=Utils.ResultSetToJsonObject(DBFactory.getDB().getResultSet(storeSQL)).toString();
		
		System.out.println("订单:"+orderInfo);
		System.out.println("加盟商信息:"+storeInfo);
		%>
	//订单ID
	var orderData=<%=orderInfo%>;
	//加盟商信息
	var storeData=<%=storeInfo%>;
		<%
	} catch (JDBCException e) {
		e.printStackTrace();
	}
	%>
	
	// 百度地图API功能
	var map = new BMap.Map("allmap");// 创建Map实例
	if(orderData.result.length>0){
		map.centerAndZoom(new BMap.Point(orderData.result[0].longitud,orderData.result[0].latitude),13);
	}else{
		map.centerAndZoom("西安",13);  // 初始化地图,用城市名设置地图中心点
	}
	map.enableScrollWheelZoom();
	
	//绘制会员图标
	var storeIcon=new BMap.Icon("store.png",new BMap.Size(27,27),{anchor:new BMap.Size(13,27)});

	function addMarker(store)
	{	

		var p=null;// new BMap.Point(store.longitud,store.latitude);
		
		if(store.longitud){
			p= new BMap.Point(store.longitud,store.latitude);
			createStoreMark(p,store);
		}else{
			var geocoder=new BMap.Geocoder();
			geocoder.getPoint(store.addressdetil,function(point){
				createStoreMark(point,store);
			});
		}
	}

	function createStoreMark(p,store){
		var mk=new BMap.Marker(p,{icon:storeIcon});

		var showDispatcherStr="";
		
		var infoWindow=new BMap.InfoWindow("<h4 id='"+store.id+"' style='margin:0 0 5px 0;padding:0.2em 0'>"+store.orgname+"</h4>" + 
				" <table><tr>"+
				" <td rowspan='3'><img style='margin:0px;width:80px;height:auto;' id='mark_img_"+store.id+"' src='dc.png'/></td>"+
				" <td><span>联系人："+store.contact+"</span></td>"+
				" </tr><tr><td><span>电话:"+store.contactphone+"</span></td></tr>"+
				" </table>"+
				" <button  onclick=\"dispatcheOrder('"+orderCode+"','"+store.id+"')\" "+showDispatcherStr+">派单</button>"
				,{enableMessage:false});
		mk.addEventListener('click',function(){

			this.openInfoWindow(infoWindow);
			
			var id=infoWindow.getContent().substring(8,infoWindow.getContent().indexOf('style=\'marg')-2);
			
			id="mark_img_"+id;
			document.getElementById(id).onload=function(){infoWindow.redraw();};
			
		},false);
		
		map.addOverlay(mk);
	};

	
	//绘制加盟商信息到地图
	if(storeData.result.length>0){
		for(var i=0;i<storeData.result.length;i++){
			addMarker(storeData.result[i]);
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
				" <tr><td><span'>订单编号："+order.ordercode+"</span></td></tr>"+
				" <tr><td><span>收货人:"+order.contact+"</span></td></tr>"+
				" <tr><td><span>联系电话:"+order.contactphone+"</span></td></tr>"+
				" <tr><td><span>收货地址:"+order.address+"</span></td></tr>"+
				" </table>",{enableMessage:false}
				);
		
		mk.addEventListener('click',function(event){
			this.openInfoWindow(infoWindowOrder);
		},false);
		var label = new BMap.Label("订单:"+order.ordercode,{offset:new BMap.Size(20,-10)});
		mk.setLabel(label);
		
		map.addOverlay(mk);
	}
	
	if(orderData.result.length>0){
		addOrderMarker(orderData.result[0]);
	}
</script>
</body>
</html>

