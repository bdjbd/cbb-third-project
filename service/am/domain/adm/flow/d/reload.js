function reloadTree(){
	var obj = window.parent;
	if(obj && obj.flow){
		obj.flow.location='/adm/tasktree.do';
	}else{
		obj.location='/adm/flowchart.do';
		obj.myDialog.close();
	}
}