function sendNotice(){

	//选择列
	var select = $$('input[name=_s_am_notice_list]');
	//通知ID
	var noticeID = $$('input[name=am_notice_list.id.k]');
	
	var noticeIds="";

	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			noticeIds+=noticeID[i].value+",";
		}
	}

	if(noticeIds){
		doAjaxSubmit("/am_bdp/am_notice_list/send.do?noticeid="+noticeIds);
	}else{
		alert("请选择数据！");
	}
	
}