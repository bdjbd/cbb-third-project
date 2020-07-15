function saveLang(){
	var select = $$('input[name=_s_langelements]');
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
		doConfirm('/dev/elementlang/save.do');
	}
}
