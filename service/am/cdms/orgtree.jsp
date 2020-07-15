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

<%
	request.setCharacterEncoding("UTF-8");
	DBManager db = new DBManager();
	String orgid = request.getParameter("orgid");
	String type = request.getParameter("type");
	String tSql = "select orgid,orgname as name,'true' as open,'../img/cz.png' as iconOpen,'../img/czh.png' as iconClose from aorg where orgid = '"
			+ orgid + "'";
	String tTemplateSQL = "select orgid,orgname as name,'true' as open,'../img/cz.png' as iconOpen ,'../img/czh.png' as iconClose from aorg where parentid='[orgid]'";
	TableList tlOrgTree = new TableList("AORG", "orgid", "orgid", tSql,
			tTemplateSQL);
	tSql = "select orgid,orgname as name,'true' as open,'../img/cz.png' as iconOpen ,'../img/czh.png' as iconClose from aorg where orgid = '"
			+ orgid + "'";
	LoadDataToJson ldtj = new LoadDataToJson(tlOrgTree, tSql);

	String data = ldtj.getJson().replaceAll("NAME", "name")
			.replaceAll("CDMS_VEHICLEBASICINFORMATION", "children")
			.replaceAll("AORG", "children").replaceAll("OPEN", "open")
			.replaceAll("ICONopen", "iconOpen")
			.replaceAll("ICONCLOSE", "iconClose")
			.replaceAll("ICON", "icon");
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
<link rel="stylesheet" href="./css/bootstrap-treeview.min.css" />
<script type="text/javascript" src="./js/jquery.min.js"></script>
<script type="text/javascript" src="./js/bootstrap.min.js"></script>
<script src="./js/bootstrap-table.min.js"></script>
<script src="./js/bootstrap-table-zh-CN.min.js"></script>
<script src="./js/bootstrapValidator.min.js"></script>
<!-- 引入样式 -->
	<link rel="stylesheet"
		href="https://cdn.bootcss.com/element-ui/2.3.4/theme-chalk/index.css">
	<!-- 先引入 Vue -->
	<script src="https://cdn.bootcss.com/vue/2.5.16/vue.js"></script>
	<!-- 引入组件库 -->
	<script src="https://cdn.bootcss.com/element-ui/2.3.4/index.js"></script>
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
				<li onClick="location.href='locus.jsp?noCache='+Math.random()">基础信息</li>
				<li onClick="location.href='locus.jsp?noCache='+Math.random()">参数设置</li>
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
    new Vue().$mount('#app')
  </script>

	<SCRIPT type="text/javascript">
		var orgid = "<%=orgid%>"
		console.info('当前登录机构='+orgid);
	    var type='';
      
	       type="<%=type%>";
	   
	   
	    
		var cph='';
		var setting = {
			view: {
				dblClickExpand: true
			},
			check: {
				enable: true
			},
			callback: {
				onRightClick: OnRightClick,
				onCheck: zTreeOnCheck  
			}
		};
        var data=<%=data%>;
		console.log(data);
		var zNodes =data;
        var tdata={};
        //节点勾选事件
           function zTreeOnCheck(event, treeId, treeNode) {
				    console.log(treeNode)
        var checked = treeNode.checked;
		  console.log(checked)
		if(treeNode.name!=''&&checked){
               cph=treeNode.name;
             if(type=='1'){
                  window.parent.orgxz(cph)
		          window.parent.$("#myModal_org").modal('hide');
			 }else{
			    window.parent.orgup(cph)
		        window.parent.$("#myModal_org").modal('hide');
			 }
			
            }	
			
		   };
		   //刷新方法
		   function sx(){
              	$.ajax({
		   type: "POST",
		   url: "/p2p/com.cdms.Monitoring.do?cph="+cph+"",
		   dataType: "json",
		   success: function(msg){
			  tdata=msg
			 window.parent.loadmap(msg)
			 
			
		   }
		   });
		   };
	    	//跳转轨迹回放
			function gj(){
				console.log(cph)
                window.parent.gj(cph)
			}
        //右键点击事件      
		function OnRightClick(event, treeId, treeNode) {
			   console.log(treeNode)
			    cph=treeNode.name
               
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root", event.clientX, event.clientY);
			} else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node", event.clientX, event.clientY);
			}
		 
		
 };
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
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}
		
		
		

		var zTree, rMenu;
		$(document).ready(function(){
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
			zTree = $.fn.zTree.getZTreeObj("treeDemo");
			rMenu = $("#rMenu");
			
		});
		//勾选tree的指定节点
        function gx(name){
            var node=zTree.getNodeByParam("name", name)		
			zTree.selectNode(node);
			node.checked = true;
			 zTree.updateNode(node); 
		}
    
</script>




</body>
</html>
