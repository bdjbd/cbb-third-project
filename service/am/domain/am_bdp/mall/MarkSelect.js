function selectMark(){
	var select = $$('input[name=_s_mall_mark.list_select]');
	//标签ID
	var markID = $$('input[name=mall_mark.list_select.id.k]');
	//名称
	var markName = $$('input[name=mall_mark.list_select.name]');
	//创建时间
	var markTime = $$('input[name=mall_mark.list_select.createtime]');

	var markData=[];
	
	var selectID='';
	var selectName='';
	var selectTime='';
	var markObj = new Object();
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			selectID=markID[i].value;
			selectName=markName[i].value;
			selectTime=markTime[i].value;			
			markObj = {'ID':markID[i].value,
                                'NAME':markName[i].value,
                                'TIME':markTime[i].value};
		}
	}
	if(selectID){
		//window.parent.document.getElelementById("").value="";
		parent.opener.setNameDateValue(markObj);
		
		window.close();
	}else{
		alert("请选择数据");
	}
}
