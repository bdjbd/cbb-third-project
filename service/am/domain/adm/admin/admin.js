function checkAll(checkboxName){
	var checked = event.srcElement.checked;
	var all = $$('input[name='+checkboxName+']');
	for(var i=0;i<all.length;i++){
		if(all[i].disabled!=true){
			all[i].checked = checked;
		}
	}
}

function showPrivileges(){
	$('admin.authorization.privileges').src='/adm/admin.privileges.do?adminid='+$fnv('admin.authorization.adminid');
}
