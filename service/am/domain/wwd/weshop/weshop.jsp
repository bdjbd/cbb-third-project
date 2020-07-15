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
<title>商城</title> 
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, minimum-scale=1.0 maximum-scale=1.0">
<meta http-equiv="pragma" content="no-cache">     
<meta http-equiv="cache-control" content="no-cache,must-revalidate">   
<meta http-equiv="expires" content="0"> 

<link rel="stylesheet"	href="/domain/wwd/weshop/jquery.mobile-1.3.2.min.css" />
<link rel="stylesheet"  href="/domain/wwd/weshop/weshop.css"/>
<script src="/domain/wwd/weshop/jquery-1.7.1.min.js"></script>
<script	src="/domain/wwd/weshop/jquery.mobile-1.3.2.min.js"></script>
<script src="weshop.js"></script>
<script type="text/javascript">
var pid="<%=request.getParameter("token")%>";
var orgid="<%=request.getParameter("orgidp")%>";
var currentPage="<%=0%>";
var orgname="";
var isLoadMore=false;
var isLoadMoreClick=false;

function loadMore(){
	showDialog("正在加载数据");
	if(isLoadMoreClick){
		return;
	}
	isLoadMoreClick=true;
	loadShopListView(pid,orgid,currentPage,function(data){
		isLoadMore=true;
		$("#loadMoreLi").remove();
		showShop(data);
		closeDialog();
		isLoadMoreClick=false;
		if(data[0].length>=5){
			currentPage++;
		}
	});
}

function showShop(data){
	console.log(data);
	if(!isLoadMore){
		$("#shopListView li").remove();
	}
	if(isEmpty(data[0])) return;
	for(var i=0;i<data[0].length;i++){
		$("#shopListView").append(
		"<li>"+
		"<a href=\"deatil.jsp?pid="+pid+"&cid="+data[0][i].COMDITY_ID+"&orgidp="+orgid+"\" data-transition=\"slidefade\" rel=\"external\">"+
		"<img class=\"showImages\"  src=\""+data[0][i].FILEPATH+data[1][i]+"\" />"+
		"<p style=\"white-space: normal;font-size:0.7em;font-weight:900;display:inline;\" class=\"ui-li-name\">"+data[0][i].NAME+"</p>"+
		"<br><p class=\"ui-li-pric-p\">￥ "+data[0][i].PRICE+"元</p><img src=\"images\\hdfk.png\" class=\"ui-li-payment-img\" />"+
		"<font>&nbsp;</font>"+
		"<p class=\"ui-li-norm-p\">"+data[0][i].COMDITY_NORM+"</p>"+
		"<p class=\"ui-li-descript-p\">"+data[0][i].SUMMARY+"</p>"+
		"<p class=\"ui-li-aside\"><span style=\"color:#b1470e;\">订购量:</span>"+data[0][i].ORDERS+"</p>"+
		"</a></li>"
		);
	}
	if(data[0].length>=5){
		$("#shopListView").append("<li id=\"loadMoreLi\"><div class=\"more\" onclick=\"loadMore()\">查看更多</div></li>");
	}
	$("#shopListView").listview("refresh");
}


<%
session.setAttribute("CURRENT_ORG",request.getParameter("orgidp"));
if(request.getParameter("openid")!=null&&WeChatInfaceServer.USERINFO_CATCH.get(request.getParameter("openid"))!=null){
	WeChatInfaceServer.USERINFO_CATCH.get(request.getParameter("openid")).setOrgind(request.getParameter("orgidp"));
}
if(request.getParameter("orgidp")!=null&&request.getParameter("orgidp").toString().length()>0){
	WeShopServer wss=new WeShopServer();
	out.print("orgname='"+wss.getOrgnameByOrgid(request.getParameter("orgidp"))+"';");
}
%>
var user=new User();
var token;
var userInfo=<%=session.getAttribute("JSON_USER_INFO")%>;
/**显示标题**/
function showTitle(){
	//标题栏
	//var c=document.getElementById("weshop_title_canvas");
	var cs=document.getElementsByClassName("weshop_title_canvas");
	for(var i=0;i<cs.length;i++){
		var ctx=cs[i].getContext("2d");
		ctx.clearRect(0,0,cs[i].width,cs[i].height);
		ctx.font="22px Verdana";
		ctx.fillText(orgname,10,30);
	}
	localStorage.setItem("orgname_title",orgname);
}
$(document).ready(function(){
	        //打开panel
	        $("#comd_class").click(function(){
	            //$("#class_panel").panel("open"); 
	            $.mobile.changePage("#comdityClassPage");
	        });
	        //关闭panel
	         $("#comd_class_close").click(function(){
	             $("#class_panel").panel("close");
	         });
	        //初始化分类选择事件；
	        function initModClass(){
	            $("#popupBasic ol").click(function(){
	                $("#popupBasic").popup("close");
	                $("#class_panel").panel("close");
	            });
	        }
	        initModClass();
	        
	        $('body').bind('scrollstart', function(event) {
	        	console.log("scrollstart");
	          });
	    });
	    
	    $(document).bind("mobileinit",function(){
	        //alert("mobileinit");
	    });
	    $(document).bind('pageinit',function(){
	        //alert("pageinit")
	    })
	    
	</script>
<style type="text/css">
    .leftBar {
	float:left;
　	width:25%;
	display:inline;
	margin:1em;
	background-color:#900;
　　}
#div2{
    position:fixed;
transform:rotate(30deg);
-ms-transform:rotate(30deg); /* IE 9 */
-moz-transform:rotate(30deg); /* Firefox */
-webkit-transform:rotate(30deg); /* Safari and Chrome */
-o-transform:rotate(30deg); /* Opera */
z-index:1000;
background-color:#232323;
border:0.3em solid;
border-radius:1em;
-moz-border-radius:1em;
top:0.1em;
left:80%;
width:5em;
height:2em;
padding-top:0.3em;
}
.showImages{
	margin:0.2em;
	width:10em;
	height:10em;
	margin-top:0.8em;
}
.more{
	text-align: center;
	margin-top:0.5em;
	height:2em;
}
body{
    -webkit-transform: translate3d(0,0,0);/*启用硬件加速*/
}
    </style>
</head> 
<body> 

<div data-role="page" id="shopShowPage">
<script type="text/javascript">
$("#shopShowPage").live("pageshow", function(){
	loadingData(loadDataStr);
	if($.mobile.path.get($(this).attr("value")).indexOf("cpid=")>-1){
		var cpid=$.mobile.path.get($(this).attr("value")).substring(
				$.mobile.path.get($(this).attr("value")).indexOf("cpid=")+5,$.mobile.path.get($(this).attr("value")).length);
		loadShopListViewByClass(pid,orgid,cpid,function(data){
			$("#loadMoreLi").remove();
			isLoadMore=false;
			showShop(data);
			closeDialog();
		});
	}else{
		loadShopListView(pid,orgid,currentPage,function(data){
			$("#loadMoreLi").remove();
			isLoadMore=false;
			showShop(data);
			closeDialog();
			if(data[0].length>=5){
				currentPage++;
			}
		});
	}
	$("#comd_price").click(function(){
		$("#shopListView li").remove();
		var orderby;
		if($(this).attr("data-icon")=="arrow-d"){
			$(this).attr("data-icon","arrow-u");
			orderby="ASC";
		}else{
			$(this).attr("data-icon","arrow-d");
			orderby="DESC";
		}
		loadingData(loadDataStr);
		loadShopByPrice(pid,orderby,orgid,function(data){
			isLoadMore=false;
			showShop(data);
			closeDialog();
		});
	});
	$("#comd_amount").click(function(){
		loadingData(loadDataStr);
		var orderby;
		if($(this).attr("data-icon")=="arrow-d"){
			$(this).attr("data-icon","arrow-u");
			orderby="ASC";
		}else{
			$(this).attr("data-icon","arrow-d");
			orderby="DESC";
		}
		loadShopByAmount(pid,orgid,orderby,function(data){
			isLoadMore=false;
			showShop(data);
			closeDialog();
		})
	});
	getOrgnameByToken(pid,orgid,function(data){
		orgname=data[0].ORGNAME;
		token=data[0].TOKEN;
		showTitle();
	});
	$("#searchBtn").click(function(){
		if(isEmpty($("#searchInput").val())){
			loadShopListView(pid,orgid,'0',function(data){
				$("#loadMoreLi").remove();
				isLoadMore=false;
				showShop(data);
				closeDialog();
			});
		}else{
			showDialog("正在搜索");
			getSearchShopList($("#searchInput").val(),pid,orgid,function(data){
				closeDialog();
				//搜索结果处理
				isLoadMore=false;
				showShop(data)
			});
		}
		
	});
});
$("#shopShowPage").live("pagecreate",function(){
});
$("#shopShowPage").on("scrollstop",function(event){
	$("#shopListView").listview("refresh");
});
$("#shopShowPage").on("vmouseup",function(event){
	$("#shopListView").listview("refresh");
});

</script>
<div data-role="panel" id="class_panel" data-theme="e" data-position="left" data-display="overlay">
<div data-role="ui-grid-a">
	<div class="ui-block-a" style="width:50%">
	</div>
    <div class="ui-block-a" style="width:50%">
    	<ul data-role="listview" data-inset="true" data-theme="c" id="comdityClassChildLivtView">
		</ul>
    </div>
</div>
</div><!--panel end-->
<div data-role="head">
	<div id="weshop_log" class="weshop_title">
		<canvas class="weshop_title_canvas" >
			<h1 class="title_content"> </h1>
		</canvas>
	</div>
</div>
  	<table border="0">
  		<colgroup width="3%"></colgroup>
  		<colgroup width="85%"></colgroup>
  		<colgroup width="10%"></colgroup>
  		<colgroup width="2%"></colgroup>
  		<tr>
  			<td></td>
  			<td><input id="searchInput" placeholder="输入关键字搜索" data-type="search" class="ui-input-text ui-body-c" data-mini="true"/></td>
  			<td><button id="searchBtn"  data-mini="true" >搜索</button></td>
  			<td></td>
  		</tr>
  	</table>
<div data-role="navbar" data-iconpos="right" data-inset="true" >
   	<ul id="navbarListView">
   		<li><a href="#" id="comd_class" data-theme="c" data-role="button" data-inline="true" data-icon="bars" data-iconpos="right" data-transition="slidefade">分类</a></li>
   		<li><a href="#" id="comd_price" data-theme="c" data-role="button" data-inline="true" data-icon="arrow-d" data-iconpos="right">价格</a></li>
   		<li><a href="#" id="comd_amount" data-theme="c" data-role="button" data-inline="true" data-icon="arrow-d" data-iconpos="right">订购量</a></li>
   	</ul>
 </div>
<div data-role="content" >
  <div>
  	<ul data-role="listview" data-inset="false" id="shopListView">
  	</ul>
  </div>
</div><!--content end-->
</div><!--page end-->

<div data-role="page" id="comdityClassPage">
<script type="text/javascript">
$("#comdityClassPage").live("pageshow", function(){
	showTitle();
	var parendid=1;
	var parendName="商品分类";
	function loadShopClassByParentId(cpid){
		loadShopClass(user.token,cpid,orgid,function(data){
			closeDialog();
			if(isEmpty(data)){
				$.mobile.changePage("#shopShowPage&cpid="+parendid,{dataUrl:"#shopShowPage&cpid="+parendid,transition:"slidefade"});
			}else{
				if(cpid===1){
					$("#comdityClassListView li:eq(0)").text(parendName);
				}else{
					$("#comdityClassListView li:eq(0)").text($("#comdityClassListView li:eq(0)").text()+" >> "+parendName);
				}
				$("#comdityClassListView li:gt(1)").remove();
				$("shopListView li").listview("refresh");
				$("#comdityClassListView").listview("refresh");
				for(var i=0;i<data.length;i++){
					$("#comdityClassListView").append("<li class=\"shopClassItem\" index=\""+
							data[i].COMDY_CLASS_ID+"\" cpid=\""+data[i].PARENT_ID+"\"><a>"+
							data[i].CLASS_NAME+"</a></li>");
				}
				$(".shopClassItem").click(function(){
					loadingData(loadDataStr);
					parendid=$(this).attr("index");
					parendName=$(this).text();
					loadShopClassByParentId(parendid);
				});
				$("#allClass").click(function(){
					location.href=location.search;
				});
			}
			$("#comdityClassListView").listview("refresh");
		});
	}
	loadShopClassByParentId(parendid);
});
</script>
<div data-role="header">
<div id="weshop_log" class="weshop_title">
		<canvas class="weshop_title_canvas" >
			<h1 class="title_content"> </h1>
		</canvas>
	</div>
</div>
<div data-role="content">
	<ul data-role="listview" data-inset="false" data-theme="c" id="comdityClassListView">
    	<li data-theme="c" style="text-align:left;font-weight:900;font-size:large;">商品分类</li>
    	<li id="allClass" data-theme="c" style="text-align:left;"><a href="#">全部</a></li>
	</ul>
</div>
</div>
</body>
</html>