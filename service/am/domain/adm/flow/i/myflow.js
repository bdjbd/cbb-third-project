function setFlow(unitId,flow){
	var domain = '';
	var flowId = '';
	if (flow && flow!=''){
		var index = flow.indexOf(".");
		domain=flow.substring(0, index);
		flowId=flow.substring(index + 1);
	}
	$$('input[name='+unitId+'.domain]')[0].value=domain;
	$$('input[name='+unitId+'.flowid]')[0].value=flowId;
}
