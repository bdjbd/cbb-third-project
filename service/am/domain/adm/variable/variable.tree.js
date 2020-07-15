var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};
function setIFrameUrl(url){
	window.parent.document.getElement('iframe[id=variables.variable]').src=url;
}
function addVariable(){
	setIFrameUrl('/adm/variable.do?m=a&parentid='+th.ae.s.id);
}
function editVariable(){
	setIFrameUrl('/adm/variable.do?m=e&variable.vid='+th.ae.s.id);
}
function deleteVariable(){
	if (confirm("删除常量'"+th.ae.s.id+"'，此操作不可恢复，你确认吗？")){
		setIFrameUrl('/adm/welcome.do');
		window.location='/adm/variable/delete.do?vid='+th.ae.s.id;
	}
}
var menu = new Tree('menu','menu');
menu.add(new Tree('m7','新增',addVariable,'/common/images/tool/add.png'));
var m9=menu.add(new Tree('m9','删除',deleteVariable,'/common/images/tool/delete.png'));
function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteVariable,true);
	}
}
var rootMenu = new Tree('rootMenu','rootMenu');
rootMenu.add(new Tree('rm7','新增',addVariable,'/common/images/tool/add.png'));
function showRootMenu(){
	rootMenu.writeMenu();
}