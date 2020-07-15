<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.fastunit.MapList,com.fastunit.jdbc.DB,
com.fastunit.jdbc.DBFactory,com.fastunit.Row,
com.fastunit.jdbc.exception.JDBCException,com.fastunit.util.Checker" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>企业信息</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, minimum-scale=1.0 maximum-scale=1.0">
<meta http-equiv="pragma" content="no-cache">     
<meta http-equiv="cache-control" content="no-cache,must-revalidate">   
<meta http-equiv="expires" content="0"> 

<link rel="stylesheet"	href="/domain/wwd/weshop/jquery.mobile-1.3.2.min.css" />
<script src="/domain/wwd/weshop/jquery-1.7.1.min.js"></script>
<script	src="/domain/wwd/weshop/jquery.mobile-1.3.2.min.js"></script>
<script type="text/javascript">
var page=<%=1%>;
function replaceBDPstr(str){
	str=str.replace(/&lt;/g,"<");
	str=str.replace(/&gt;/g,">");
	return str;
}
function getNewsDetail(nid,callBack){
	$.get("/weshop/getNewsDetail.do",{NID:nid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}
function loadMoreInfo(cid,page,orgid,callBack){
	$.get("/weshop/loadMoreInfo.do",{CID:cid,PAGE:page,ORGID:orgid},function(data,textStatus){
		if(textStatus=="success"){
			callBack(eval("("+replaceBDPstr(data)+")"));
		}else{
			showNetWorkException();
		}
	});
}

function isEmpty(obj){
	if(obj==null)return true;
	if(obj=="")return true;
	if(obj=="undefined")return true;
	if(obj=="null")return true;
	if(obj.toString().trim()==null)return true;
	if(obj.toString().trim().length==0)return true;
	return false;
}
</script>
<style type="text/css">
span{
	font-weight:normal;
	font-style: normal;
}
.more{
	text-align: center;
}
.reals_date{
	font-size: x-small;
	font-weight: normal;
	font-style: normal;
}
.newsImage{
	width:100%;
	height: auto;
}
.content{
	white-space: normal;
	margin:2px;
}
.abstract{
	font-size:small;
	font-weight: normal;
	font-style: normal;
}
</style>
</head>
<body>
<!--企业信息摘要 start-->
<div data-role="page" id="newlistPage">
<script type="text/javascript">
	var cid="<%=request.getParameter("cid")%>";
	var orgid="<%=request.getParameter("orgid")%>"
	function loadMore(){
		$("#loadMoreLi").remove();
		$("#cidinfoListView").listview("refresh");
		loadMoreInfo(cid,page,orgid,function(data){
			if(isEmpty(data)){
				return;
			}
			for(var i=0;i<data.length;i++){
				$("#cidinfoListView").append("<li>"+
						"<a href=\"#newdetailsPage?nid="+data[i].NID+"\" data-ajax=\"false\">"+
						"<h3>"+data[i].TITLE+"</h3>"+
						"<span class=\"abstract\">"+data[i].ABSTRACT+"</span>"+
						"</a></li>");
			}
			if(data.length>=9){
				page++;
				$("#cidinfoListView").append("<li id=\"loadMoreLi\"><div class=\"more\" onclick=\"loadMore()\">查看更多</div></li>");
			}
			$("#cidinfoListView").listview("refresh");
		});
	}
</script>
	<%
try {
	String orgid=request.getParameter("orgid");
	String cid=request.getParameter("cid");
	session.setAttribute("NEWS_ORGID",orgid);
	session.setAttribute("NEWS_CID",cid);
	DB db=DBFactory.getDB();
	String sql="SELECT c.cid,c.cinfo,d.nid,d.title,substring(content,0,20) AS abstract "+
			"	FROM newscategory AS c RIGHT JOIN newsdetail AS d                     "+
			"	ON c.cid=d.cid                                                        "+
			"	WHERE d.datastatus=2 AND c.orgid='"+orgid+"'"+
			"   AND c.cid="+cid+"   ORDER BY d.createdate DESC "+
			"   LIMIT 10 OFFSET 0*10 ";
	MapList map=db.query(sql);
	int i=0;
	if(!Checker.isEmpty(map)){
		%>
<div data-role="header" data-theme="d"><h4><%=map.getRow(0).get("cinfo") %></h4></div>
<div data-role="content">
	<ul data-role="listview" id="cidinfoListView">
		<%
		for(;i<map.size();i++){
			Row row=map.getRow(i);
			%>
		<li>
		<a href="#newdetailsPage?nid=<%=row.get("nid") %>" data-ajax="false">
		<h3><%=row.get("title") %></h3>
		<span class="abstract"><%=row.get("abstract") %></span>
		</a>
		</li>
			<%
		}
		if(i>=9){
			out.print("<li id=\"loadMoreLi\"><div class=\"more\" onclick=\"loadMore()\">查看更多</div></li>");
		}
	}
} catch (JDBCException e) {
	e.printStackTrace();
}
%>
	</ul>
</div>
</div>
<!--企业信息摘要 end-->

<!--企业信息详情 start-->
<div data-role="page" id="newdetailsPage">
<script type="text/javascript">
$("#newdetailsPage").live("pagebeforeshow",function(){
	$("#imagesDiv").html("");
	$("#dcinfo").text("");
	$("#dtitle").text("");
	$("#drelesase_date").text("");
	$("#deatilContent").text("");
});
$("#newdetailsPage").live("pageshow",function(){
	var url=$.mobile.path.get($(this).attr("value"));
	var index=0;
	if(url!=""&&url.length>0){
		var params=url.split("?");
		index=String(params[1]).substring(String(params[1]).indexOf("=")+1);
	}
	//$("#myResult").html(index);
	if(!isEmpty(index)){
		getNewsDetail(index,function(data){
			if(isEmpty(data)){
				return;
			}
			$("#dcinfo").text(data[0].CINFO);
			$("#dtitle").text(data[0].TITLE);
			$("#drelesase_date").text(data[0].RELEASE_DATE);
			$("#deatilContent").text(data[0].CONTENT);
			if(data.length>1){
				var images=data[1].replace("[","").replace("]","").split(",");
				for(var i=0;i<images.length;i++){
					$("#imagesDiv").append("<img class='newsImage' src='"+images[i]+"'>");
				}
			}
		});
	}
});
</script>
<div data-role="header" data-theme="d"><h4 id="dcinfo"></h4></div>
<div data-role="content" data-theme="d">
	<div class="ui-body-d-d">
		<h3 id="dtitle"></h3>
		<span class="reals_date" >发布日期:<span  class="reals_date"  id="drelesase_date"></span></span>
		</br></br>
		<div id="imagesDiv">
		
		</div>
		
		<p id="deatilContent" class="content">
		</p>
	</div>
</div>
</div>
<!--企业信息详情 end-->
</body>
</html>