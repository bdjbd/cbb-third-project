function checkAll(checkboxName){
	var checked = event.srcElement.checked;
	var all = $$('input[name='+checkboxName+']');
	for(var i=0;i<all.length;i++){
		if(all[i].disabled!=true){
			all[i].checked = checked;
			exclude(all[i]);
		}
	}
}

function setDate(obj){
	var d = $(obj).getParent('div').getElement('a').getElement('input');
	d.setStyle('visibility',obj.checked?'visible':'hidden');
	d.set('disabled',!obj.checked);
}

function exclude(obj){
	setDate(obj);
	if(!obj.checked){
		return;
	}
	if(obj.name=="a.roles"){
		obj.parentNode.all("a.repellentroles").checked=false;
	}else if(obj.name=="a.repellentroles"){
		obj.parentNode.all("a.roles").checked=false;
	}
}

function showPrivileges(){
	$('user.authorization.privileges').src='/adm/user.privileges.do?userid='+$fnv('user.authorization.userid');
}

function showNodes(){
	$('user.authorization.nodes').src='/cc/user.privileges.do?userid='+$fnv('user.authorization.userid');
}
