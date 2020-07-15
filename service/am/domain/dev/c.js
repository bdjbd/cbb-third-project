var deleteIcon ={
	ENABLED : '/common/images/tool/delete.png',
	DISABLED :'/common/images/tool/delete_d.png'
};
function changeDomain(){
	if(window.parent.main){
		window.parent.main.location="/dev/welcome.do";
	}
	doSubmit();
}
function reload(){
	window.location='/dev/component.do';
}
function getSelectedTreeId(){
	return th.ae.s.id;
}
function getSelectedId(){
	var id = th.ae.s.id;
	return id.substring(4,id.length);
}
function addGroup(grouptype){
	window.parent.main.location="/dev/group.do?m=a&grouptype="+grouptype;
}
function deleteGroup(grouptype){
	var groupid = getSelectedId();
	if (confirm("删除组'"+groupid+"'，此操作不可恢复，你确认吗？")){
		window.parent.main.location="/dev/welcome.do";
		doSubmit("/dev/group/delete.do?grouptype="+grouptype+"&groupid="+groupid);
	}
}
function setGroupDeleteStatus(c,node,action){
	var htmlNode = c.all(node.id);
	var selected = th.all[getSelectedTreeId()];
	if(selected.cs.length<1){
		htmlNode.className = "";
		htmlNode.getElementsByTagName("IMG")[0].src=deleteIcon.ENABLED;
		node.action=action;
	}else{
		htmlNode.className = MenuNaming.CSS_DISABLED;
		htmlNode.getElementsByTagName("IMG")[0].src=deleteIcon.DISABLED;
		node.action=null;
	}
}
// root menu
function output(){
	window.parent.main.location="/dev/output.do";
}
function input(){
	window.parent.main.location="/dev/input.do";
}
var m0 = new Tree('m0','m0');
m0.add(new Tree('m01','版本导出',output,'/common/images/tool/output.png'));
m0.add(new Tree('m02','版本导入',input,'/common/images/tool/input.png'));
function wm0(){
	m0.writeMenu();
}
