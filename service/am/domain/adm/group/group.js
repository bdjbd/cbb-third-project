function reloadTree(){
	window.parent.document.getElement('iframe[id=group.tree]').src='/adm/group.tree.do';
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
	$('group.roles.privileges').src='/adm/group.privileges.do?groupid='+$fnv('group.roles.groupid');
}

function showNodes(){
	$('group.roles.nodes').src='/cc/group.privileges.do?groupid='+$fnv('group.roles.groupid');
}
