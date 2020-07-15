function fillTaskInfo(e){
	var tempId=e.options[e.selectedIndex].value;
	doAjax('/am_bdp/p2p_enterprisetask.form/tasktemplateid.do?tempId='+tempId);
	
}

function changeTaskValue(data){
	document.getElementById('enterpNameId').value=data.NAME;
	document.getElementById('enterpTaskparameId').value=data.TASKPARAME;
	//document.getElementById('enterpNoteId').value=data.NOTE;	
}


function stop(id){
	var select = $$('input[name=_s_p2p_enterprisetask.list]');
	var bookId = $$('input[name=p2p_enterprisetask.list.id.k]');
	var msg='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg+=bookId[i].value+',';
		}
	}
	alert(msg.substring(0,msg.length-1));

	doAjax("/am_bdp/p2p_enterprisetask.list/stop.do?type=stop&id="+msg.substring(0,msg.length-1));
}

function startTask(id){
	doAjax("/am_bdp/p2p_enterprisetask.list/stop.do?type=stop&id="+id);
}
function stopTask(id){
}