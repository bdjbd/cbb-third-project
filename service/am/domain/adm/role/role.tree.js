var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};
function setIFrameUrl(url){
	window.parent.document.getElement('iframe[id=role.role]').src=url;
}
function setPrivilege(){
	setIFrameUrl('/adm/role.privileges.do?roleid='+th.ae.s.id);
}
function listRelations(){
	setIFrameUrl('/adm/role.relation.do?roleid='+th.ae.s.id);
}
function listAllUsers(){
	setIFrameUrl('/adm/role.allusers.do?roleid='+th.ae.s.id);
}
function addRole(){
	setIFrameUrl('/adm/role.form.do?m=a&parentid='+th.ae.s.id);
}
function editRole(){
	setIFrameUrl('/adm/role.form.do?m=e&role.form.roleid='+th.ae.s.id);
}
function deleteRole(){
	if (confirm("删除角色'"+th.ae.s.id+"'，此操作不可恢复，你确认吗？")){
		setIFrameUrl('/adm/welcome.do');
		window.location='/adm/role.form/delete.do?roleid='+th.ae.s.id;
	}
}
var menu = new Tree('menu','menu');
menu.add(new Tree('m1','关联一览',listRelations,'/domain/dev/t/link.png'));
menu.add(new Tree('m2','用户一览',listAllUsers,'/domain/adm/u/user1.png'));
menu.add(new MenuSeparator());
menu.add(new Tree('m7','新增',addRole,'/common/images/tool/add.png'));
menu.add(new Tree('m8','修改',editRole,'/common/images/tool/edit.png'));
var m9=menu.add(new Tree('m9','删除',deleteRole,'/common/images/tool/delete.png'));
function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteRole,true);
	}
}
var rootMenu = new Tree('rootMenu','rootMenu');
rootMenu.add(new Tree('rm7','新增',addRole,'/common/images/tool/add.png'));
rootMenu.add(new Tree('rm8','修改',editRole,'/common/images/tool/edit.png'));
function showRootMenu(){
	rootMenu.writeMenu();
}