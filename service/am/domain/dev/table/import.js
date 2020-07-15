function doImport(){
	var checks = $$('input[name=_s_columns]');
	if(checks.length==0){
		alert("该表没有字段");
		return;
	}
	var unselected = true;
	for(var i=0;i<checks.length;i++){
		if (checks[i].value == "1") {
			unselected=false ;
		}
  }
	if(unselected){
		alert("请选择字段");
	}else{
		doSubmit('/dev/table.import/import.do') ;
	}
}

