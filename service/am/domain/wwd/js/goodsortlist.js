function getcode(){
	var parent_id = document.getElementsByName('ws_commodity_edit.parent_id')[0].value;
	var code = document.getElementsByName('ws_commodity_edit.c_code')[0].value;
	if(code!=null||code!=""){
       var codeId = code.substring(0,code.lastIndexOf("-"));//ǰ׺
	   var codeno = code.substring(code.lastIndexOf("-")+1,code.length);//��׺
       var comdy_class_id = document.getElementsByName('ws_commodity_edit.comdy_class_id')[0].value;
       if(comdy_class_id==""){//����
	     document.getElementsByName('ws_commodity_edit.suf')[0].value=code;
	   }else{//�޸�
		 document.getElementsByName('ws_commodity_edit.suf')[0].value=codeId;
         document.getElementsByName('ws_commodity_edit.c_code_s')[0].value=codeno;
	   }
    }
}
