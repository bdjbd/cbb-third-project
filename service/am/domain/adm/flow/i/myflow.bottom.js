function cancelFlow(obj){
	if (confirm(Lang.FlowCancel)){
		var row = $($fTr(obj));
		var flowInstId = row.getElement('input[name=myflow.list.fiid.k]').value;
		var domain = row.getElement('input[name=myflow.list.domain]').value;
		var flowId = row.getElement('input[name=myflow.list.flowid]').value;
		var url = '/adm/myflow.list/cancel.do?fiid='+flowInstId+'&domain='+domain+'&flowid='+flowId;
		doLink(url);
	}
}

function restartFlow(obj){
	if (confirm(Lang.FlowRestart)){
		var row = $($fTr(obj));
		var flowInstId = row.getElement('input[name=myflow.list.fiid.k]').value;
		var domain = row.getElement('input[name=myflow.list.domain]').value;
		var flowId = row.getElement('input[name=myflow.list.flowid]').value;
		var url = '/adm/myflow.list/restart.do?fiid='+flowInstId+'&domain='+domain+'&flowid='+flowId;
		doLink(url);
	}
}


