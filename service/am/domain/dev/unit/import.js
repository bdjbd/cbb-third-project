function doImport(){
	var checks = $$('input[name=_s_elements]');
	if(checks.length==0){
		alert("该单元没有元素");
		return;
	}
	var unselected = true;
	for(var i=0;i<checks.length;i++){
		if (checks[i].value == "1") {
			unselected=false ;
		}
  }
	if(unselected){
		alert("请选择元素");
	}else{
		doSubmit('/dev/unit.import/import.do') ;
	}
}

