//[root]
function addIncrementor(){
	window.parent.main.location="/dev/incrementor.do?m=a";
}
function addIncrementorGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=7";
}
var mr7 = new Tree('mr7','');
mr7.add(new Tree('mr71','自增器',addIncrementor,'/domain/dev/t/addincrementor.png'));
mr7.add(new Tree('mr72','组',addIncrementorGroup,'/domain/dev/t/addgroup.png'));
function wmr7(){
	mr7.writeMenu();
}
//[group]
function addIncrementor2Group(){
	window.parent.main.location="/dev/incrementor.do?groupid="+getSelectedId()+"&m=a";
}
function deleteIncrementorGroup(){
	deleteGroup(7);
}
var mg7 = new Tree('mg7','mg7');
mg7.add(new Tree('mg71','自增器',addIncrementor2Group,'/domain/dev/t/addincrementor.png'));
var mg79 = mg7.add(new Tree('mg79','删除',deleteIncrementorGroup,'/common/images/tool/delete.png'));
function wmg7(){
	var c = mg7.writeMenu();
	setGroupDeleteStatus(c,mg79,deleteIncrementorGroup);
}
//[incrementor]
function copyIncrementor(){
	window.parent.main.location="/dev/incrementor.do?src="+getSelectedId()+"&m=a";
}
function deleteIncrementor(){
	var incrementorId = getSelectedId();
	if (confirm("删除自增器'"+incrementorId+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/incrementor/delete.do?iid="+incrementorId);
	}
}
function lookIncrementorReference(){
	window.parent.main.location="/dev/table.reference.do?iid="+getSelectedId();
}
var m7 = new Tree('im','chartMenu');
m7.add(new Tree('im1','关联',lookIncrementorReference,'/domain/dev/t/link.png'));
m7.add(new MenuSeparator());
m7.add(new Tree('im2','复制',copyIncrementor,'/common/images/tool/copy.png'));
m7.add(new Tree('im9','删除',deleteIncrementor,'/common/images/tool/delete.png'));
function wm7(){
	m7.writeMenu();
}




