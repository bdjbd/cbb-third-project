function cancelFlow(obj){
	if (confirm(Lang.FlowCancel)){
		var row = $($fTr(obj));
		var flowInstId = row.getElement('input[name=flow.monitor.list.fiid.k]').value;
		var domain = row.getElement('input[name=flow.monitor.list.domain]').value;
		var flowId = row.getElement('input[name=flow.monitor.list.flowid]').value;
		var url = '/adm/flow.monitor.list/cancel.do?fiid='+flowInstId+'&domain='+domain+'&flowid='+flowId;
		doLink(url);
	}
}
