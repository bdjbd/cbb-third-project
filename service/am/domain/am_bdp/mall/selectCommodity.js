function selectCommodit(){
	var select = $$('input[name=_s_mall_commodity.list_select]');
	//商品ID
	var commodID = $$('input[name=mall_commodity.list_select.id.k]');
	//商品名称
	var commodityName=$$('input[name=mall_commodity.list_select.name]');
	//商品物资分类
	//var materialstypeIds=$$('input[name=mall_commodity.list_select.materialstypeid]');
	//商品状态
	var comodityStates=$$('input[name=mall_commodity.list_select.commoditystate]');

	var commoditData=[];
	
	var selectID='';
	var selectName='';
	var materId='';
	var comState='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			selectID=commodID[i].value;
			selectName=commodityName[i].value;
			//materId=materialstypeIds[i].value;
			comState=comodityStates[i].value;

			// commoditData[commoditData.length]={
			// 	'ID':commodID[i].value,'NAME':commodityName[i].value,
			// 	'MATERID':"materialstypeIds[i].value",'COMSTATE':comodityStates[i].value};
			commoditData[commoditData.length]={
				'ID':commodID[i].value,'NAME':commodityName[i].value,'COMSTATE':comodityStates[i].value};
		}
	}
	

	if(selectID){
		//window.parent.document.getElelementById("").value="";
		//parent.opener.setCodeType(cname,value,name);
		var parent = window.parent;
		if(location.search.indexOf('details')>0){
			parent.setSelectCommodityDetails(commoditData);
		}else{
			parent.setSelectCommodity(selectID,selectName);
		}
		parent.myDialog.close();
	}else{
		alert("请选择商品");
	}
}//selectCommodit


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
		window.opener.setCodeType(cname,value,name);
		
	}else{
		alert('没有选择数据');
	}
	window.close();
}