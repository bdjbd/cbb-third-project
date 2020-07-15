function reloadTree(){
	window.parent.document.getElement('iframe[id=org.tree]').src='/am_bdp/org.tree.do';
}
function checkAll(){
	var checked = event.srcElement.checked;
	var all = $$('input[name=_treecheck]');
	for(var i=0;i<all.length;i++){
		if(all[i].disabled!=true){
		  all[i].checked = checked;
		}
	}
}

function showPrivileges(){
	$('org.roles.privileges').src='/adm/org.privileges.do?orgid='+$fnv('org.roles.orgid');
}
