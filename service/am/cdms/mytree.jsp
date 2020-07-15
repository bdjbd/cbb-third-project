<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.cdms.TreeData"%>
<%@ page import="com.fastunit.Var"%>
<%
	request.setCharacterEncoding("UTF-8");
	String orgid = request.getParameter("orgid");
	TreeData tree = new TreeData();
	String data = tree.getTreeData(orgid);
	int time = Var.getInt("refresh_time", 15000)*1000;
%>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="initial-scale=1.0, user-scalable=no, width=device-width">
<title></title>
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
<script type="text/javascript" src="./js/jquery.min.js"></script>
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
<script type="text/javascript" src="/zTree_v3/js/jquery-1.4.4.min.js"></script>
<!--  <script type="text/javascript">  
            var jQuery_1_4_4 = $.noConflict(true);  
        </script>   -->
<script type="text/javascript" src="/zTree_v3/js/jquery.ztree.core.js"></script>
<script type="text/javascript"
	src="/zTree_v3/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="/zTree_v3/js/jquery.ztree.exedit.js"></script>
<style type="text/css">
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
	right: 14px;
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
	background-color: #fff;
	text-align: left;
	padding: 6px;
	border: 1px solid #ccc;
	border-radius: 3px;
}

#rMenu ul li {
	margin: 1px 0;
	padding: 0 5px;
	cursor: pointer;
	list-style: none outside none;
	background-color: #DFDFDF;
}

#rMenu ul li:hover {
	background-color: #F2F2F2;
}

.table {
	width: 2000px;
}
</style>
</head>
<body>
	<div id="app">
		<div id="rMenu">
			<ul style="margin: 0; padding: 0;">
				<li onclick="gj()">轨迹回放</li>
				<li onClick="toBasicInfo()">基础信息</li>
				<li onClick="toParamsSetting()">参数设置</li>
			</ul>
		</div>
	</div>
	<div class="content_wrap">
		<div>
			<ul id="treeDemo" class="ztree"></ul>
		</div>
	</div>
	</div>

	<script>
    new Vue().$mount('#app');


</script>

	<SCRIPT type="text/javascript">
		var cph='';//车牌号(勾选使用)
		var cph1='';//车牌号(右键菜单传参)
		var orgid1='';//所选机构(右键菜单传参)
		var positioning = '';//需要定位的车牌号

		//zTree设置
		var setting = {
			view: {
				dblClickExpand: true
			},
			check: {
				enable: true
			},
			callback: {
			    
				onRightClick: OnRightClick,
				onCheck: zTreeOnCheck,
				onClick:onClick
            
			}
		};

        var data=<%=data%>;
		var zNodes =data;

        //节点点击事件，点击时勾选节点
		function onClick(e, treeId, treeNode, clickFlag)
		{
			positioning='';//重置当前点击车辆
			//如果未选中checked 
			if(treeNode.checked!==true){
				zTree.checkNode(treeNode,!treeNode.checked, true);
				zTreeOnCheck(event, treeId, treeNode);
			}else{
				//如果已选中且当前节点是车辆，则直接定位
				if(typeof(treeNode.ORGID) == "undefined"){
					if(positioning !== treeNode.name){
						//地图定位当前车辆
						positioning = treeNode.name;
						window.parent.mark = treeNode.name;
						window.parent.setCarLocation();
						window.parent.markerClick(treeNode.name);
					}
					
				}else{
					zTreeOnCheck(event, treeId, treeNode);
				}
				
			}
			
		}
		        
		//勾选节点事件
        function zTreeOnCheck(event, treeId, treeNode)
		{
			positioning='';//重置当前点击车辆
			window.parent.mark = '';//重置用于记住弹窗的变量，勾选和取消勾选时关闭弹窗
			cph='';
			var nodes = zTree.transformToArray(zTree.getNodes()); 
			for (i = 0; i < nodes.length; i++) { 
				var checked = nodes[i].checked;
				if(typeof(nodes[i].ORGID) == "undefined"){
					if(checked==true){
						cph+="'"+nodes[i].name+"',";
					}else{
						cph=cph.replace("'"+nodes[i].name+"',","");
					}
					if(nodes[i].name!==''){
						//刷新节点icon
						$.ajax({
			   				type: "POST",
			   				url: "/p2p/com.cdms.UpdateCarIcon.do?cph="+nodes[i].name+"&i="+i,
			   				dataType: "json",
			   				success: function(msg){
			   					nodes[msg[0].I].icon = msg[0].ICON;
		   						zTree.updateNode(nodes[msg[0].I]);
			   				}
						})
					}	
				}
			}
			$.ajax({
		   			type: "POST",
		   			url: "/p2p/com.cdms.Monitoring.do?cph="+cph+"",
		   			dataType: "json",
		   			success: function(msg){
		   				//加载地图
			 			window.parent.loadmap(msg);	  
		   			}
				})	
		}
		function sx(){
			console.info('刷新时间='+new Date());
			cph='';
			var nodes = zTree.transformToArray(zTree.getNodes()); 
			for (i = 0; i < nodes.length; i++) { 
				var checked = nodes[i].checked;
				if(typeof(nodes[i].ORGID) == "undefined"){
					if(checked==true){
						cph+="'"+nodes[i].name+"',";
					}else{
						cph=cph.replace("'"+nodes[i].name+"',","");
					}
					if(nodes[i].name!==''){
						//刷新节点icon
						$.ajax({
			   				type: "POST",
			   				url: "/p2p/com.cdms.UpdateCarIcon.do?cph="+nodes[i].name+"&i="+i,
			   				dataType: "json",
			   				success: function(msg){
			   					nodes[msg[0].I].icon = msg[0].ICON;
		   						zTree.updateNode(nodes[msg[0].I]);
			   				}
						})
					}	
				}
			}
			$.ajax({
		   			type: "POST",
		   			url: "/p2p/com.cdms.Monitoring.do?cph="+cph+"",
		   			dataType: "json",
		   			success: function(msg){
			 			window.parent.loadmap(msg);	  
		   			}
				});
	
		}

		//刷新
		var time = <%=time%>;
		console.info('监控中心刷新间隔（ms）='+time);
		$(document).ready(function (){setInterval("sx()",time);});

		//查询车牌号直接勾选节点
        function gx(name){
            var node=zTree.getNodeByParam("name", name)		
			zTree.selectNode(node);
			node.checked = true;
			zTree.updateNode(node); 
		}

        //zTree右键      
		function OnRightClick(event, treeId, treeNode) {
			console.log(treeNode)
			if(typeof(treeNode.ORGID) !== "undefined"){
				orgid1=treeNode.ORGID;
			}else{
				cph1=treeNode.name;
			}
			
			if(!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root", event.clientX, event.clientY);
			}else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node", event.clientX, event.clientY);
			}	
 		};
 		//zTree显示右键菜单
		function showRMenu(type, x, y) {
			$("#rMenu ul").show();
			if (type=="root") {
				$("#m_del").hide();
				$("#m_check").hide();
				$("#m_unCheck").hide();
			} else {
				$("#m_del").show();
				$("#m_check").show();
				$("#m_unCheck").show();
			}
            y += document.body.scrollTop;
            x += document.body.scrollLeft;
            rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});
			$("body").bind("mousedown", onBodyMouseDown);
		}
		//隐藏右键菜单
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);//解绑右键事件
		}
		//隐藏右键菜单
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}
		//初始化zTree
		var zTree, rMenu;
		$(document).ready(function(){
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
			zTree = $.fn.zTree.getZTreeObj("treeDemo");
			rMenu = $("#rMenu");
			
		});
	    //跳转轨迹页
		function gj(){
            window.parent.gj(cph1)
            rMenu.css({"visibility" : "hidden"});
		}
		//跳转基础信息
		function toBasicInfo(){
            window.parent.toBasicInfo(cph1,orgid1);
            cph1='';
            orgid1='';
            rMenu.css({"visibility" : "hidden"});
		}
		//跳转参数设置
		function toParamsSetting(){
            window.parent.toParamsSetting(cph1,orgid1);
            cph1='';
            orgid1='';
            rMenu.css({"visibility" : "hidden"});
		}
    
</script>
</body>
</html>
