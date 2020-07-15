//[root]
function addEnumeration(){
	window.parent.main.location="/dev/enum.do?m=a";
}
function addEnumerationGroup(){
	window.parent.main.location="/dev/group.do?m=a&grouptype=3";
}
var mr3 = new Tree('mr3','mr3');
mr3.add(new Tree('mr31','枚举',addEnumeration,'/domain/dev/t/addenum.png'));
mr3.add(new Tree('mr32','组',addEnumerationGroup,'/domain/dev/t/addgroup.png'));
function wmr3(){
	mr3.writeMenu();
}
//[group]
function addEnumeration2Group(){
	window.parent.main.location="/dev/enum.do?groupid="+getSelectedId()+"&m=a";
}
function deleteEnumerationGroup(){
	deleteGroup(3);
}
var mg3 = new Tree('mg3','mg3');
mg3.add(new Tree('mg31','枚举',addEnumeration2Group,'/domain/dev/t/addenum.png'));
var mg39 = mg3.add(new Tree('mg39','删除',deleteEnumerationGroup,'/common/images/tool/delete.png'));
function wmg3(){
	var c = mg3.writeMenu();
	setGroupDeleteStatus(c,mg39,deleteEnumerationGroup);
}
//[enumeration]
function deleteEnumeration(){
	var eid = getSelectedId();
	if (confirm("删除枚举'"+eid+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/enum/delete.do?eid="+eid);
	}
}
function copyEnumeration(){
	window.parent.main.location="/dev/enum.do?src="+getSelectedId()+"&m=a";
}
function lookEnumerationReference(){
	window.parent.main.location="/dev/unit.relation.do?c=102&id="+getSelectedId();
}
var m3 = new Tree('m3','m3');
m3.add(new Tree('m31','关联',lookEnumerationReference,'/domain/dev/t/link.png'));
m3.add(new MenuSeparator());
m3.add(new Tree('m32','复制',copyEnumeration,'/common/images/tool/copy.png'));
m3.add(new Tree('m39','删除',deleteEnumeration,'/common/images/tool/delete.png'));
function wm3(){
	m3.writeMenu();
}
