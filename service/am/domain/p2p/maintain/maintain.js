function frozenMaintain(url,unit){
	var select = $$('input[name=_s_p2p_maintain.list]');
	var bookId = $$('input[name=p2p_maintain.list.member_code.k]');
	var msg='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg+=bookId[i].value+',';
		}
	}
	
	if(msg){
		var membercode=msg.substring(0,msg.length-1);
		//alert('选择的数据为：'+msg.substring(0,msg.length-1));
		doAjax("/wwd/p2p_maintain.list/delete.do?member_code="+membercode);
	}else{
		alert('没有选择数据');
	}
}