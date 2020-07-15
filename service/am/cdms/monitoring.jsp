<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.fastunit.Var"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.fastunit.MapList"%>
<%@page import="com.fastunit.Row"%>
<%@page import="com.fastunit.jdbc.DB"%>
<%@page import="com.am.frame.webapi.db.DBManager"%>
<%@page import="com.fastunit.jdbc.exception.JDBCException"%>
<%@page import="com.fastunit.util.Checker"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.am.sdk.json.TableList"%>
<%@page import="com.am.sdk.json.LoadDataToJson"%>
<%@page import="com.fastunit.User"%>
<%@page import="com.fastunit.util.Checker"%>

<%
   	request.setCharacterEncoding("UTF-8");

	DBManager db = new DBManager();	
	
	//获得机构Id
	String orgid=request.getParameter("orgid");
	//获得当前机构所在城市名称
    String osql="select (select proname from province where proid=ao.province),(select cityname from city where cityid=ao.city) from aorg as ao where ao.orgid='"+orgid+"'";
	MapList mapList = db.query(osql);
    String cityname=mapList.getRow(0).get("cityname");
	if(Checker.isEmpty(cityname)){
		cityname=mapList.getRow(0).get("proname");
	}

	//为联想输入准备数据
    JSONObject objectJson =null;
	JSONArray array = new JSONArray();
	JSONObject objectJson1 =new JSONObject();
    String csql="select license_plate_number from CDMS_VEHICLEBASICINFORMATION ";
	MapList mapList1 = db.query(csql);
    for(int i = 0; i < mapList1.size(); i++){		
		objectJson = new JSONObject();
		objectJson.put("title",mapList1.getRow(i).get("license_plate_number"));
		array.put(objectJson);
	}
	objectJson1.put("data",array);
    String cph_data=array.toString();
%>  

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="initial-scale=1.0, user-scalable=no, width=device-width">
<title>监控中心</title>
<link rel="stylesheet"
	href="http://cache.amap.com/lbs/static/main1119.css" />
<link rel="stylesheet"
	href="http://cache.amap.com/lbs/static/main.css?v=1.0" />
<script type="text/javascript"
	src="http://webapi.amap.com/maps?v=1.4.3&key=16c83a3fef165e22dfd8a9f976e880ee"></script>
<script src="http://cache.amap.com/lbs/static/es5.min.js"></script>
<script type="text/javascript"
	src="http://webapi.amap.com/demos/js/liteToolbar.js"></script>
<link rel="stylesheet" href="./css/bootstrap.min.css" />
<link rel="stylesheet" href="./css/bootstrap-table.min.css" />
<link rel="stylesheet" href="./css/jquery.bigautocomplete.css" />
<script type="text/javascript" src="./js/jquery.min.js"></script>
<script type="text/javascript" src="./js/jquery.bigautocomplete.js"></script>
<script type="text/javascript" src="./js/bootstrap.min.js"></script>
   <script src="./js/bootstrap-table.min.js"></script>  
<script src="./js/bootstrap-table-zh-CN.min.js"></script> 
<script src="./js/bootstrapValidator.min.js"></script>
<!-- 引入样式 -->
	<!-- <link rel="stylesheet"
		href="https://cdn.bootcss.com/element-ui/2.3.4/theme-chalk/index.css"> -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/element-ui/2.3.4/theme-chalk/index.css" rel="stylesheet">		
	<!-- 先引入 Vue -->
	<!-- <script src="https://cdn.bootcss.com/vue/2.5.16/vue.js"></script> -->
	    <script type="text/javascript" src="./js/vue.min.js"></script>

	<!-- 引入组件库 -->
	<!-- <script src="https://cdn.bootcss.com/element-ui/2.3.4/index.js"></script> -->
<!-- 	<script src="https://cdnjs.cloudflare.com/ajax/libs/element-ui/2.3.4/index.js"></script>
	 --><script type="text/javascript" src="./js/index.js"></script>

<link rel="stylesheet" href="/zTree_v3/css/zTreeStyle/zTreeStyle.css"
	type="text/css">

<!--  <script type="text/javascript">  
            var jQuery_1_4_4 = $.noConflict(true);  
        </script>   -->
<script type="text/javascript" src="/zTree_v3/js/jquery.ztree.core.js"></script>
<script type="text/javascript"
	src="/zTree_v3/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="/zTree_v3/js/jquery.ztree.exedit.js"></script>
<style type="text/css">
.alert {
    display: none;
    z-index: 99999;
    padding: 15px;
    border: 1px solid transparent;
    border-radius: 4px;
	position: fixed;
    top: 0px;
    left: 0px;
    right: 0px;
    bottom: 0px;
    margin: auto;
	width: 400px;
	height: 200px;
	overflow: hidden;
	opacity:0.9;
}

.alert-success {
	text-align: center;
    color: #333333;
    background-color: #FFFFFF;
    border-color: #FFFFFF;
}

.alert-info {
    color: #31708f;
    background-color: #d9edf7;
    border-color: #bce8f1;
}

.alert-warning {
    color: #8a6d3b;
    background-color: #fcf8e3;
    border-color: #faebcc;
}

.alert-danger {
    color: #a94442;
    background-color: #f2dede;
    border-color: #ebccd1;
}

.amap-logo {
	display: none !important;
}

.amap-copyright {
	display: none !important;
}

#container {
	padding-left: 300px;
	padding-bottom: 200px;
	position: absolute;
}

#left {
	width: 280px;
	background-color: #fff;
	border-radius: 3px;
	position: absolute;
	top: 10px;
	bottom: 10px;
	left: 10px;
	opacity: 0.9;
	filter: alpha(opacity = 90);
	box-shadow: 0 3px 14px rgba(0, 0, 100, 0.6);
}

#locus {
	width: 90%;
	position: absolute;
	bottom: 14px;
	left: 14px;
	right:14px;
}

#fence {
	width: 40%;
	position: absolute;
	bottom: 5%;
	left: 55%;
}

#bottom {
	height: 200px;
	background-color: #fff;
	border-radius: 3px;
	position: absolute;
	bottom: 10px;
	left: 300px;
	right: 10px;
	opacity: 0.9;
	filter: alpha(opacity = 90);
	box-shadow: 0 3px 14px rgba(0, 0, 100, 0.6);
}

.form-group {
	margin-bottom: 0px;
}

#rMenu {
	position: absolute;
	visibility: hidden;
	top: 0;
	background-color: #555;
	text-align: left;
	padding: 2px;
}

#rMenu ul li {
	margin: 1px 0;
	padding: 0 5px;
	cursor: pointer;
	list-style: none outside none;
	background-color: #DFDFDF;
}
#rMenu ul li:hover{
	background-color:#F2F2F2;
}

td{
     font-size: 14px;
       overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
	border:none;
}
th{
     font-size: 14px;
     border-left: none;
}
tr{
    border-left: none;
}
.fixed-table-body {
    overflow-x: auto;
    overflow-y: auto;
    height: 75%;
}
.fixed-table-pagination .pagination-detail, .fixed-table-pagination div.pagination {
    margin-top: 0px;
    margin-bottom: 0px;
    display: none;
     height:0px
}
.fixed-table-container {
border:none;
}
.bootstrap-table .table thead>tr>th {
border-left: none;
}
.fixed-table-container tbody td {
    border-left: none;
}
</style>
</head>
<body>
<div id="app">
		<div id="container"></div>
		<div id="left" style="text-align: center;">
			<div id="procitytree" style="overflow-x: hidden">
				<div
					style="padding: 15px; border-bottom: 1px solid #e5e5e5; min-height: 16.42857143px; font-size: 14px;" class="row">
					<div class="col-xs-7">
					    <input type="text" id="ba" placeholder="请输入车牌号码" style="width:150px;height:25px;font-size:12px;border-radius: 3px;background: #fff; border: 1px solid #dcdfe6;border-radius: 3px;padding: 0 15px;color: #606266;" />
					</div>
					<div class="col-xs-5">
					<button onclick="ss()"
						style="float: right;height:25px;font-size:15px;padding: 4px 15px;font-size: 12px;background: #fff; border: 1px solid #dcdfe6;border-radius: 3px;"><i class="el-icon-search"></i><span>搜索</span></button> 
                     </div>
				</div>
				<div class="content_wrap">
					<div>
						<iframe name="myfram" id="myfram" style="height: 80%;width: 280px;border-width: 0px;"></iframe>
					</div>
				</div>
				<div id="rMenu">
					<ul style="margin: 0;padding:0;">
						<li
							onClick="location.href='locus.jsp?noCache='+Math.random()">轨迹回放</li>
						<li onClick="location.href='locus.jsp?noCache='+Math.random()">基础信息</li>
						<li onClick="location.href='locus.jsp?noCache='+Math.random()">参数设置</li>
					</ul>
				</div>
			</div>
			<el-button id="locus" icon="el-icon-location" type="primary"
				onclick="gj()">轨迹回放</el-button>
		</div>
		
		<div id="bottom" style="display:none; ">
			<ul id="myTab" class="nav nav-tabs" >
				<li class="active"><a href="#location" data-toggle="tab">实时位置</a></li>
				<li><a href="#carcondition" data-toggle="tab">车况报告</a></li>
				<li><a href="#breakdown" data-toggle="tab">故障查看</a></li>
				<a class="close" data-dismiss="modal" aria-hidden="true" onclick="gb()" style="margin-right: 5px;">x</a>
			</ul>
			
			<div id="myTabContent" class="tab-content" >
				<div id="location" class="tab-pane fade in active" >
		
    <table id="table"></table>  

</div>
				
				<div id="carcondition" class="tab-pane fade" >
					<table id="table1"></table>  
				</div>
				<div id="breakdown" class="tab-pane fade">
					<table id="table2"></table>  
				</div>
			</div>
		</div>
        </div>
<script type="text/javascript">
	


	new Vue().$mount('#app');
	
	var orgid="<%=orgid%>";
	console.info('当前机构'+orgid);
	
	//加载机构Tree
	document.getElementById('myfram').src ="/cdms/mytree.jsp?orgid="+orgid;

	var cph_data=<%=cph_data%>
	var scph='';//车牌号(ajax参数,拼了逗号)
	var sscph='';//车牌号(用于勾选节点)
	var mark = '';//记录表格展示的哪辆车，页面刷新后自动加载出来
	var infoWindow=new AMap.InfoWindow({offset: new AMap.Pixel(0, -30)});;//定义点击车辆图标弹框
	var infocontent = [];//弹框内容
	//联想输入
    $(function(){								
		$("#ba").bigAutocomplete({data:cph_data,callback:function(data){
			scph="'"+data.title+"',";
			sscph=data.title;		
		}});	
	})
	
	//构建地图
	var map = new AMap.Map("container", {resizeEnable: true});

	//设置当前地图所显示的城市
	var cityname="<%=cityname%>";
	map.setCity(cityname);
		

	//加载地图
	//msg 车辆信息json集合
	function loadmap(msg)
	{	
		document.getElementById('bottom').style.display='none';//关闭底部table
		map.clearMap();// 清除地图覆盖物		
		var iconpath;//marker图标路径
    	var iconcolor;//marker图标颜色 
    	var vehicle_state;//车辆状态
    	var direction;//方向
    	var marker;//地图标记
		var tips='';//弹框提醒内容
		infocontent=[];
		

		if(msg!==null)
		{
			
    		for (var i = 0; i < msg.length; i++) 
			{
    			if(msg[i].l[0]!=='null'&&msg[i].l[1]!=='null')
				{
					//获得当前车辆的状态
					vehicle_state = msg[i].vehicle_state;
					//获得当前车辆的方向
					direction = msg[i].direction;

					//生成图标路径
					if(vehicle_state==1){//正常使用
						iconcolor = 'green';
					}
					else if(vehicle_state==5){//停车
						iconcolor = 'blue';
					}
					else if(vehicle_state==6){//怠速
						iconcolor = 'orange';
					}
					else if(vehicle_state==7){//报警
						iconcolor = 'red';
					}
					else if(vehicle_state==2||vehicle_state==3||vehicle_state==4||vehicle_state==8){//离线
						iconcolor = 'gray';
					}else{
						iconcolor = 'gray';
					}
					iconpath = '/img/'+iconcolor+'/car1.png';
					
					//生成车辆图标
					marker = new AMap.Marker({
						map: map,
						title:msg[i].cph,
						position:msg[i].l,
						offset: new AMap.Pixel(-15, -40),//相对于基点的位置  
						icon:new AMap.Icon({            
							size: new AMap.Size(36, 66),
							image: iconpath,
							imageOffset: new AMap.Pixel(0, 0)
						}),
						angle:direction//Marker旋转角度（方向）    
					});
					
					//设置图标对应的车牌号
					marker.cph =  msg[i].cph;
					msg[i].speed = typeof(msg[i].speed) == "undefined"?'':msg[i].speed+' km/h';
					msg[i].mileage = typeof(msg[i].mileage) == "undefined"?'':msg[i].mileage+' km';
					msg[i].location = typeof(msg[i].location) == "undefined"?'':msg[i].location;
					msg[i].member = typeof(msg[i].member) == "undefined"?'':msg[i].member;
					msg[i].tel = typeof(msg[i].tel) == "undefined"?'':msg[i].tel;
					msg[i].positioning_time = typeof(msg[i].positioning_time) == "undefined"?'':msg[i].positioning_time;
					
					var content = '<form class="form-horizontal" role="form" style="min-width: 215px;"><b>实时位置</b>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">车牌号</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].cph + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">速度</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].speed + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">当前里程</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].mileage + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">位置</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].location + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">是否触警</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].fatname + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">驾驶员姓名</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].member + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">驾驶员电话</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].tel + '</p></div>'+
						'</div></div></div>'+
						'<div class="form-group">'+
						'<label class="col-sm-5 control-label">定位时间</label>'+
						'<div class="col-sm-7"><p class="form-control-static">' + msg[i].positioning_time + '</p></div>'+
						'</div></div></div></form>';
					marker.content=content;
					var markerinfo = {cph:msg[i].cph,position:msg[i].l,content:content}
					infocontent.push(markerinfo);
					//Marker点击事件
					AMap.event.addListener(marker, 'click',function(e){						
						mark = e.target.cph;
						markerClick(e.target.cph);
					});
					if(mark!==''){
						//页面刷新不关闭已打开的弹窗
						markerClick(mark);
					}
					
				}else{
					//车辆没有坐标
					tips+=msg[i].cph+',';
			}

			}
			if(tips!==''){
				$('<div>').appendTo('body').addClass('alert alert-success').html('以下车辆没有位置信息！<br>'+tips.substring(0,tips.length-1)).show().delay(1500).fadeOut();
				//要不要取消勾选没有位置的车辆（将对应车牌号的节点的checked设为false，重置tips='',暂不需要修改）
			}
			setCarLocation();//定位车辆	
		}
	}
	
	//定位地图至mytree上点击的车辆
	function setCarLocation()
	{
		var positioning = window.myfram.positioning;
			if(positioning!==''){
				for(var j=0;j<infocontent.length;j++){
					if(positioning==infocontent[j].cph){
						map.setZoomAndCenter(18,infocontent[j].position);
					}
				}
		}else{
			map.setFitView();//地图自适应显示到合适的范围
		}
	}
	//marker点击事件
	function markerClick(cph){

		$("#table").bootstrapTable('destroy');
		$("#table1").bootstrapTable('destroy');
		$("#table2").bootstrapTable('destroy'); 

		//加载实时位置数据
		$('#table').bootstrapTable({  		   
			url: "/p2p/com.cdms.Monitoring_ss.do?cph="+cph+"",  
			queryParamsType: '',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
			method: "post",  
			pagination: true,  
			pageNumber: 1,  
			pageSize: 2,  
			pageList: [10, 20, 50, 100],  
			sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
			striped: true,                    //是否显示行间隔色  
			cache: true, 
			dataType:"json",
			uniqueId: "id",               //每一行的唯一标识，一般为主键列  
			columns: [               
				{ title: '车牌号', field: 'cph' },  
				{ title: '机构', field: 'orgenam' },  
				{ title: '速度km/h', field: 'speed' },  
				{ title: '里程km', field: 'mileage' },
				{ title: '驾驶员', field: 'member' },
				{ title: '联系方式', field: 'tel' },
				// { title: '油量L', field: 'oilmass' },
				{ title: '报警', field: 'fatname' },
				{ title: '方向', field: 'direction' },
				{ title: '时间', field: 'positioning_time' },
				{ title: '位置', field: 'location' },
				{ title: '经纬度', field: 'l' },
			],
			responseHandler: function(res) {
				console.log(res)
				return {
					"total": res.total,//总页数
					"rows": res.rows   //数据
				};
			} 
		});

		//加载车况报告数据
		$('#table1').bootstrapTable({  		   
			url: "/p2p/com.cdms.Monitoring_ss.do?cph="+cph+"",  
			queryParamsType: '',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
			method: "post",  
			pagination: true,  
			pageNumber: 1,  
			pageSize: 2,  
			pageList: [10, 20, 50, 100],  
			sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
			striped: true,                    //是否显示行间隔色  
			cache: true, 
			dataType:"json",
			uniqueId: "id",               //每一行的唯一标识，一般为主键列  
			paginationPreText: "上一页",  
			paginationNextText: "下一页",  
			columns: [               
				{ title: '电频电压\r(V)', field: 'battery_voltage' },  
				{ title: '发动机转速\r(rpm)', field: 'engine_speed' },  
				{ title: '行驶车速km/h', field: 'driving_speed' },  
				{ title: '节气门开度\r(%)', field: 'throttle_opening' },
				{ title: '发动机负荷\r(%)', field: 'engine_load' },
				{ title: '冷却液温度\r(℃)', field: 'coolant_temperature' },
				{ title: '瞬时油耗\r(L/100km)', field: 'instantaneous_fuel_consumption' },
				{ title: '本次行驶里程\r(km)', field: 'travel_mileage' },
				{ title: '总里程\r(km)', field: 'total_mileage' },				
				{ title: '本次耗油量\r(L)', field: 'fuel_consumption' },
				{ title: '累计耗油量\r(L)', field: 'accumulative_oil_consumption' },
			],
			responseHandler: function(res) {
				return {
					"total": res.total,//总页数
					"rows": res.rows   //数据
				};
			}
		});

		//加载故障数据        
		$('#table2').bootstrapTable({  		   
			url: "/p2p/com.cdms.Monitoring_gz.do?cph="+cph+"",  
			queryParamsType: '',              //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort              
			method: "post",  
			pagination: true,  
			pageNumber: 1,  
			pageSize: 2,  
			pageList: [10, 20, 50, 100],  
			sidePagination: "server",         //分页方式：client客户端分页，server服务端分页（*）  
			striped: true,                    //是否显示行间隔色  
			cache: true, 
			dataType:"json",
			uniqueId: "id",               //每一行的唯一标识，一般为主键列  
			paginationPreText: "上一页",  
			paginationNextText: "下一页",  
			columns: [               
				{ title: '故障码', field: 'alarm_code' },  
				{ title: '故障名', field: 'fatname' },  
				{ title: '故障时间', field: 'alarm_time' }    
			],
			responseHandler: function(res) {
				console.log(res)
				return {
					"total": res.total,//总页数
					"rows": res.rows   //数据
				};
			}
		});
		for(var j=0;j<infocontent.length;j++){
			if(cph==infocontent[j].cph){
				//设置窗体的显示内容
				infoWindow.setContent(infocontent[j].content);
				//设置在地图上打开窗体的位置
				infoWindow.open(map, infocontent[j].position);
			}
		}

		//设置底部表格显示状态=显示
		document.getElementById("bottom").style.display="block";
	}

	//搜索车牌号
	function ss()
	{
		window.myfram.gx(sscph);

		$.ajax({
			type: "POST",
		   	url: "/p2p/com.cdms.Monitoring.do?cph="+scph+"",
		   	dataType: "json",
		   	success: function(msg){
				loadmap(msg);
		   	}
		})	
	}
	
	//跳转至轨迹
    function gj(cph1){
		var params = '';
		if(typeof(cph1)=="undefined"){
			window.open("locus.jsp?orgid="+orgid+"");
		}else{
			window.open("locus.jsp?cph="+cph1+"&orgid="+orgid+"");	
		}
	}

	//基本信息
    function toBasicInfo(cph1,orgid1)
	{
		if(orgid1===''){
			window.open("cdms_vehiclebasicinformation.do?name=基础信息&cph="+cph1+"&clear=cdms.cdms_vehiclebasicinformation.query");
		}else{
			window.open("cdms_vehiclebasicinformation.do?name=基础信息&orgid="+orgid1+"&clear=cdms.cdms_vehiclebasicinformation.query");
		}
	}
	
	//参数设置
    function toParamsSetting(cph1,orgid1)
	{
		if(orgid1===''){
			window.open("cdms_vehiclebasicinformationpar.do?name=参数设置&cph="+cph1+"");	
		}else{
			window.open("cdms_vehiclebasicinformationpar.do?name=参数设置&orgid="+orgid1+"");	
		}
        
	}

	//关闭底栏
	function gb() {
		mark='';
		document.getElementById('bottom').style.display='none'
		
	}
	
</script>	
</body>
</html>
  