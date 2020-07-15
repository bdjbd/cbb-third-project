var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};
function setIFrameUrl(url){
	window.parent.document.getElement('iframe[id=group.group]').src=url;
}
function setRole(){
	setIFrameUrl('/adm/group.roles.do?groupid='+th.ae.s.id);
}
function listRelations(){
	setIFrameUrl('/adm/group.relation.do?groupid='+th.ae.s.id);
}
function addGroup(){
	setIFrameUrl('/adm/group.form.do?m=a&parentid='+th.ae.s.id);
}
function editGroup(){
	setIFrameUrl('/adm/group.form.do?m=e&group.form.groupid='+th.ae.s.id);
}
function deleteGroup(){
	if (confirm("删除用户组'"+th.ae.s.id+"'，此操作不可恢复，你确认吗？")){
		setIFrameUrl('/adm/welcome.do');
		window.location='/adm/group.form/delete.do?groupid='+th.ae.s.id;
	}
}
var menu = new Tree('menu','menu');
menu.add(new Tree('m1','关联一览',listRelations,'/domain/dev/t/link.png'));
menu.add(new MenuSeparator());
menu.add(new Tree('m7','新增',addGroup,'/common/images/tool/add.png'));
menu.add(new Tree('m8','修改',editGroup,'/common/images/tool/edit.png'));
var m9 = menu.add(new Tree('m9','删除',deleteGroup,'/common/images/tool/delete.png'));
function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteGroup,true);
	}
}