//[root]
function addFlow(){
	window.parent.main.location="/dev/flow.do?m=a";
}
function addFlowGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=22";
}
var mr22 = new Tree('mr-22','mr22');
mr22.add(new Tree('mr-221','流程',addFlow,'/domain/dev/t/addflow.png'));
mr22.add(new Tree('mr-222','组',addFlowGroup,'/domain/dev/t/addgroup.png'));
function wmr22(){
	mr22.writeMenu();
}
//[group]
function addFlow2Group(){
	window.parent.main.location="/dev/flow.do?groupid="+getSelectedId()+"&m=a";
}
function deleteFlowGroup(){
	deleteGroup(22);
}
var mg22 = new Tree('mg-22','mg22');
mg22.add(new Tree('mg-221','流程',addFlow2Group,'/domain/dev/t/addflow.png'));
var mg229 = mg22.add(new Tree('mg-229','删除',deleteFlowGroup,'/common/images/tool/delete.png'));
function wmg22(){
	var c = mg22.writeMenu();
	setGroupDeleteStatus(c,mg229,deleteFlowGroup);
}
//[Flow]
function copyFlow(){
	window.parent.main.location="/dev/flow.do?src="+getSelectedId()+"&m=a";
}
function deleteFlow(){
	var fid = getSelectedId();
	if (confirm("删除流程'"+fid+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/flow/delete.do?fid="+fid);
	}
}
var m22 = new Tree('m-22','m22');
m22.add(new Tree('m-222','复制',copyFlow,'/common/images/tool/copy.png'));
m22.add(new Tree('m-229','删除',deleteFlow,'/common/images/tool/delete.png'));
function wm22(){
	m22.writeMenu();
}



