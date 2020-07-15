/**
*工作确认状态修改
*confirm=status
**/
function submitWorkConfirm(url,listName,msg){
	
	
		//export_work_cinfirm.list
		var select = $$('input[name=_s_'+listName+']');
		var workIds = $$('input[name='+listName+'.id.k]');
		
		var id='';

		for(var i=0;i<select.length;i++){
			if(select[i].value=='1'){
				id=workIds[i].value+',';
		//		break;
			}
		}

		if(id){
			if(confirm(msg)){
				//doAjax(url+"&id="+id);
				doSubmit(url+"&id="+id);
			}
		}else{
			alert('请选择数据');
		}
	
	
}