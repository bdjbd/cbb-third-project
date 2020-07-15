function saveLang(){
	var select = $$('input[name=_s_table.addlang]');
	var noSelect = true;
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			noSelect = false;
			break;
		}
	}
	if(noSelect){
		alert('请选择');
	}else{
		doConfirm('/dev/table.addlang/save.do');
	}
}

var sa = $('_sa_');
sa.set('checked','checked');
$fSelectAll('table.addlang',sa);