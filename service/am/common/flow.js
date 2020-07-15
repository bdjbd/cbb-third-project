function selectFlowUser(actionUrl,nextTaskId){
	showNextFlowUsers(false,false,actionUrl,nextTaskId);
}
function selectFlowUsers(actionUrl,nextTaskId){
	showNextFlowUsers(true,false,actionUrl,nextTaskId);
}
function autoSelectFlowUser(actionUrl,nextTaskId){
	showNextFlowUsers(false,true,actionUrl,nextTaskId);
}
function autoSelectFlowUsers(actionUrl,nextTaskId){
	showNextFlowUsers(true,true,actionUrl,nextTaskId);
}
function showNextFlowUsers(multi,auto,actionUrl,nextTaskId){
  var selectNextTaskObject = $fn($fnv('_flow_unit_id')+'..next_task_id');
  if(selectNextTaskObject && selectNextTaskObject.value){
		nextTaskId=selectNextTaskObject.value;
	}
	document.forms[0].action=actionUrl;
	var endIndex = actionUrl.indexOf('.do');
	var startIndex = actionUrl.substring(0,endIndex).lastIndexOf('/')+1;
	var actionId = actionUrl.substring(startIndex,endIndex);
	var fiidObj = $fn('flowinstid');
	var fiid = fiidObj?fiidObj.value:'';
	var params= '?multi='+(multi?'1':'0')+'&domain='+$fnv('_flow_domain')+'&flowid='+$fnv('_flow_id')+'&taskid='+$fnv('_flow_task_id')+'&fiid='+fiid+'&actionid='+actionId+'&clear=adm.flow.nextusers.query';
	params+='&nexttaskid='+(nextTaskId?nextTaskId:'');//未指定时也要传空字符串、这样不会取session
	var one=false;
	if(auto){
		var temp = new Element('div',{'styles':{'display':'none'}});
		$(document.forms[0]).adopt(temp);
		new Request.HTML({url:encodeURI('/adm/flow.nextusers.list/auto.do'+params),async:false,update:temp}).post();
		one = '1'==temp.get('html');
		if(one && confirm(Lang.Confirm)){
			document.forms[0].submit();
			return ;
		}
	}
	if(!one){
		var dialog = new MooDialog.Iframe('/adm/flow.nextusers.do'+params,{
			useEscKey:false,
			closeOnOverlayClick:false,
			'class': 'MooDialog FLDU'
		});
	}
}
function selectBackTask(actionUrl){
	document.forms[0].action=actionUrl;
	var url = '/adm/flow.backtasks.do?fiid='+$fnv('flowinstid')+'&domain='+$fnv('_flow_domain')+'&flowid='+$fnv('_flow_id')+'&taskid='+$fnv('_flow_task_id');
	var dialog = new MooDialog.Iframe(url,{
		useEscKey:false,
		closeOnOverlayClick:false,
		'class': 'MooDialog FLDU'
	});
}
function flowPrint(){
	showObjs('none');
	window.print();
	window.setTimeout("showObjs('block');",1);
}
function showObjs(show){
	showObj($('u_u_top'),show);
	showObj($('u_u_bottom'),show);
	showObj($('u_u_flow.track'),show);
	showObj($('u_u_flow.todo'),show);
	showObj($('u_u_flow.do'),show);
	showObj($('print'),show);
}
function showObj(obj,show){
	if(obj) obj.setStyle('display',show);
}
function showAutoTasks(show){
	var grid=new Grid('flow.track');
	var groups = grid.groups;
	for(var i=0;i<groups.length ;i++){
		var row = groups[i].rows[0];
		if(row.hasClass('FLTS')){$fShowRow(row,show);}
	}
	grid.setIndex();
}
