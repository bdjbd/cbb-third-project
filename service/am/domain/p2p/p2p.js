/**
 * 
 */

function selectUserBadgepar(e){
	var tempId=e.options[e.selectedIndex].value;
}


function showImages(){
	var src1=document.getElementById("productimage4").src;
	var src2=document.getElementById("productimage5").src;
	if(src1.length>1){
//		document.getElementById("productimage4").src=src1.split(",")[0];
		var srcs=src1.split(",");
		var imgHtml="";
		for(var i=0;i<srcs.length;i++){
			imgHtml+="<img src='"+srcs[i]+"' style='width:26%;height:auto;margin:7px;' />"
		}
		document.getElementById("productimage4Td").innerHTML=imgHtml;
	}
	if(src2.length>1){
//		document.getElementById("productimage5").src=src2.split(",")[0];
		var srcs=src2.split(",");
		var imgHtml="";
		for(var i=0;i<srcs.length;i++){
			imgHtml+="<img src='"+srcs[i]+"' style='width:26%;height:auto;margin:7px;' />"
		}
		document.getElementById("productimage5Td").innerHTML=imgHtml;
	}

}

function showNewsDetailImages(){
	try{
	var src1=document.getElementById("contentimage").src;
	var src2=document.getElementById("mcontentimage").src;
	if(src1.length>1){
		document.getElementById("contentimage").src=src1.split(",")[0];
	}
	if(src2.length>1){
		document.getElementById("mcontentimage").src=src2.split(",")[0];
	}
	}catch(e){
	}

}

/**
派单管理
***/
function dispathcOrder(){
	
	var select = $$('input[name=_s_ws_memberws.list]');
	var bookId = $$('input[name=ws_memberws.list.member_code.k]');
	var orderCode=document.getElementById("ordercode").value;
	var msg='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg+=bookId[i].value+',';
		}
	}
	
	if(msg){
		var membercode=msg.substring(0,msg.length-1);
		//alert('选择的数据为：'+msg.substring(0,msg.length-1));
		//alert(orderCode);
		doAjax("/wwd/ws_memberws.list/save.do?member_code="+membercode+"&orderCode="+orderCode);
	}else{
		alert('没有选择数据');
	}
}


function autoRereshServerOrder(){
	setTimeout('myrefresh()',1000*60*5); //指定1秒刷新一次 
}
function myrefresh() 
{ 
	location.reload(); 
} 
