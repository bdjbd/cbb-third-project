function formatSelect(){
	var select = $$('input[name=_s_p2p_materialscode_dialog.list]');
	var codes = $$('input[name=p2p_materialscode_dialog.list.code.k]');
	var cnames=$$('input[name=p2p_materialscode_dialog.list.cname]');
	var names=$$('input[name=p2p_materialscode_dialog.list.name]')
	var value='';
	var cname="";
	var name="";
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			value+=codes[i].value;
			name=cnames[i].value;
			name=names[i].value;
		}
	}
	
	if(value){
		parent.opener.setCodeType(cname,value,name);
		if(window.parent){
			window.parent.close();
		}
	}else{
		alert('没有选择数据');
	}
	
}