
function locationUrl(url,id){
	
	doSubmit('/app/abdp_message.list/del.do?id='+id);
	if(url!=null && url !=""){
		window.opener.parent.frames[1].frames[1].location=url;
	}
	window.close();
}