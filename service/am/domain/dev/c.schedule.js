//[root]
function addJob(){
	window.parent.main.location="/dev/job.do?m=a";
}
function addJobGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=21";
}
var mr21 = new Tree('mr-21','mr21');
mr21.add(new Tree('mr-211','计划',addJob,'/domain/dev/t/addschedule.png'));
mr21.add(new Tree('mr-212','组',addJobGroup,'/domain/dev/t/addgroup.png'));
function wmr21(){
	mr21.writeMenu();
}
//[group]
function addJob2Group(){
	window.parent.main.location="/dev/job.do?groupid="+getSelectedId()+"&m=a";
}
function deleteJobGroup(){
	deleteGroup(21);
}
var mg21 = new Tree('mg-21','mg21');
mg21.add(new Tree('mg-211','计划',addJob2Group,'/domain/dev/t/addschedule.png'));
var mg219 = mg21.add(new Tree('mg-219','删除',deleteJobGroup,'/common/images/tool/delete.png'));
function wmg21(){
	var c = mg21.writeMenu();
	setGroupDeleteStatus(c,mg219,deleteJobGroup);
}
//[job]
function copyJob(){
	window.parent.main.location="/dev/job.do?src="+getSelectedId()+"&m=a";
}
function deleteJob(){
	var jid = getSelectedId();
	if (confirm("删除计划'"+jid+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/job/delete.do?jid="+jid);
	}
}
var m21 = new Tree('m-21','m21');
m21.add(new Tree('m-212','复制',copyJob,'/common/images/tool/copy.png'));
m21.add(new Tree('m-219','删除',deleteJob,'/common/images/tool/delete.png'));
function wm21(){
	m21.writeMenu();
}



