var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};
function setIFrameUrl(url){
	window.parent.document.getElement('iframe[id=org.org]').src=url;
}
function setRole(){
	setIFrameUrl('/adm/org.roles.do?orgid='+th.ae.s.id);
}
function listRelations(){
	setIFrameUrl('/adm/org.relation.do?orgid='+th.ae.s.id);
}
function addOrg(){
	setIFrameUrl('/app/org.form.do?m=a&parentid='+th.ae.s.id);
}
function editOrg(){
	setIFrameUrl('/app/org.form.do?m=e&org.form.orgid='+th.ae.s.id);
}
function deleteOrg(){
	if (confirm("删除机构'"+th.ae.s.id+"'，此操作不可恢复，你确认吗？")){
		setIFrameUrl('/adm/welcome.do');
		window.location='/app/org.form/delete.do?orgid='+th.ae.s.id;
	}
}
var menu = new Tree('menu','menu');
menu.add(new Tree('m1','关联一览',listRelations,'/domain/dev/t/link.png'));
menu.add(new MenuSeparator());
menu.add(new Tree('m7','新增',addOrg,'/common/images/tool/add.png'));
menu.add(new Tree('m8','修改',editOrg,'/common/images/tool/edit.png'));
var m9=menu.add(new Tree('m9','删除',deleteOrg,'/common/images/tool/delete.png'));
function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteOrg,true);
	}
}