//[root]
function addTree(){
	window.parent.main.location="/dev/tree.do?m=a";
}
function addTreeGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=4";
}
var mr4 = new Tree('mr4','mr4');
mr4.add(new Tree('mr41','树',addTree,'/domain/dev/t/addtree.png'));
mr4.add(new Tree('mr42','组',addTreeGroup,'/domain/dev/t/addgroup.png'));
function wmr4(){
	mr4.writeMenu();
}
//[group]
function addTree2Group(){
	window.parent.main.location="/dev/tree.do?groupid="+getSelectedId()+"&m=a";
}
function deleteTreeGroup(){
	deleteGroup(4);
}
var mg4 = new Tree('mg4','mg4');
mg4.add(new Tree('mg41','树',addTree2Group,'/domain/dev/t/addtree.png'));
var mg49 = mg4.add(new Tree('mg49','删除',deleteTreeGroup,'/common/images/tool/delete.png'));
function wmg4(){
	var c = mg4.writeMenu();
	setGroupDeleteStatus(c,mg49,deleteTreeGroup);
}
//[tree]
function deleteTree(){
	var treeId = getSelectedId();
	if (confirm("删除树'"+treeId+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/tree/delete.do?treeid="+treeId);
	}
}
function copyTree(){
	window.parent.main.location="/dev/tree.do?src="+getSelectedId()+"&m=a";
}
function lookTreeReference(){
	window.parent.main.location="/dev/unit.relation.do?c=85&id="+getSelectedId();
}
var m4 = new Tree('m4','m4');
m4.add(new Tree('m41','关联',lookTreeReference,'/domain/dev/t/link.png'));
m4.add(new MenuSeparator());
m4.add(new Tree('m42','复制',copyTree,'/common/images/tool/copy.png'));
m4.add(new Tree('m49','删除',deleteTree,'/common/images/tool/delete.png'));
function wm4(){
	m4.writeMenu();
}
