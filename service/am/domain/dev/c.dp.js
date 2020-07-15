//[root]
function addDataPrivilege(){
	window.parent.main.location="/dev/dp.do?m=a";
}
function addDataPrivilegeGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=6";
}
var mr6 = new Tree('mr6','mr6');
mr6.add(new Tree('mr61','数据权限',addDataPrivilege,'/domain/dev/t/adddp.png'));
mr6.add(new Tree('mr62','组',addDataPrivilegeGroup,'/domain/dev/t/addgroup.png'));
function wmr6(){
	mr6.writeMenu();
}
//[group]
function addDataPrivilege2Group(){
	window.parent.main.location="/dev/dp.do?groupid="+getSelectedId()+"&m=a";
}
function deleteDataPrivilegeGroup(){
	deleteGroup(6);
}
var mg6 = new Tree('mg6','mg6');
mg6.add(new Tree('mg61','数据权限',addDataPrivilege2Group,'/domain/dev/t/adddp.png'));
var mg69 = mg6.add(new Tree('mg69','删除',deleteDataPrivilegeGroup,'/common/images/tool/delete.png'));
function wmg6(){
	var c = mg6.writeMenu();
	setGroupDeleteStatus(c,mg69,deleteDataPrivilegeGroup);
}
//[dp]
function copyDP(){
	window.parent.main.location="/dev/dp.do?src="+getSelectedId()+"&m=a";
}
function deleteDataPrivilege(){
	var dpid = getSelectedId();
	if (confirm("删除枚举'"+dpid+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/dp/delete.do?dpid="+dpid);
	}
}
function lookDataPrivilegeReference(){
	window.parent.main.location="/dev/unit.relation.do?c=110&id="+getSelectedId();
}
var m6 = new Tree('m6','m6');
m6.add(new Tree('m61','关联',lookDataPrivilegeReference,'/domain/dev/t/link.png'));
m6.add(new MenuSeparator());
m6.add(new Tree('m62','复制',copyDP,'/common/images/tool/copy.png'));
m6.add(new Tree('m69','删除',deleteDataPrivilege,'/common/images/tool/delete.png'));
function wm6(){
	m6.writeMenu();
}



