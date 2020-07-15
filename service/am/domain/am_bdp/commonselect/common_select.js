var myDialog;

/*
*s this，弹出点击对象
*unitName 弹出选择单元的单元名称
*qParams  弹出单元ulr参数，可以为空
*name  显示名称的单元名
**/
function openDialog(s,unitName,qParams,name){
	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	}
		var url='/am_bdp/'+unitName+'.do?parentid='+parentid+'&index='+index;
		
		if(name){
			url+="&parentname="+name
		}
			
		url+='&clear=am_bdp.'+unitName+'.query';

		if(qParams){
			url+="&"+qParams;
		}
	//window.open(url,
	//	'',
	//	'height=600,width=900,top=10,left=100,scrollbars =yes,toolbar=no,menubar=no');
	myDialog=new MooDialog.Iframe(url,{
		title: '',
		'class': 'MooDialog myDialog'
	});
}



/***
**商品套餐，选择商品规格
***/
function selectGroupSpec(s,targetEleName,name){
	var params="commodityid=";

	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	}
	
	var targetEname='input[name='+targetEleName+']';

	params+=$$(targetEname)[index].value;
	openDialog(s,"select_comdity_spec_list",params,name);
}