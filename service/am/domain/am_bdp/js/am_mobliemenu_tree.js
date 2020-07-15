
var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};

var menu = new Tree('menu','menu');

menu.add(new Tree('m7','新增',addTerminalMenu,'/common/images/tool/add.png'));
menu.add(new Tree('m8','修改',editTerminalMenu,'/common/images/tool/edit.png'));
var m9=menu.add(new Tree('m9','删除',deleteMoblieMenu,'/common/images/tool/delete.png')); 


function reloadTree(){ 
	window.parent.mobliemenu.location='/am_bdp/am_mobliemenu_tree.do';    
}

//添加终端菜单信息
function addTerminalMenu(){
	window.parent.mobliemenuform.location='/am_bdp/am_mobliemenu_content.do?m=a&am_mobliemenu_form.id='+th.ae.s.id;
}
//修改终端信息
function editTerminalMenu(){
	window.parent.mobliemenuform.location='/am_bdp/am_mobliemenu_content.do?m=e&am_mobliemenu_form.id='+th.ae.s.id;
}
//删除终端信息
function deleteMoblieMenu(){
	if (confirm("确定要删除'"+th.ae.s.innerText+"'吗？")){
		var id = th.ae.s.id;
		if(id == '1'){
		alert("此目录为固定目录，不可删除");
		return false;
		}
		var str = "/am_bdp/am_mobliemenu_tree/delete.do?nodeid="+th.ae.s.id;
       doAjaxSubmit(str);
	}
}


function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteMoblieMenu,true);
	}
}


