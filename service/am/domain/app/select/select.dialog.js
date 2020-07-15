//设置父页面的值，重新装载列表单元
function setSelect(){
	var unitId = document.getElement('input[name=_u_]').value.split(',')[1];
	window.parent.setValues($fnv(unitId+'.values'),$fnv(unitId+'.names'));
}
// 设置本页面的值
function setRadio(obj){
	var unitId = document.getElement('input[name=_u_]').value.split(',')[1];
	var valueKey = $fnv(unitId+'.valuekey');
	var nameKey = $fnv(unitId+'.namekey');
	var row = obj.getParent('tr');
	var value = row.getElement('input[name='+unitId+'.list.'+valueKey+']').value;
	var name = row.getElement('input[name='+unitId+'.list.'+nameKey+']').value;
	//重新设置values和names
	$fn(unitId+'.values').value = value;
	$fn(unitId+'.names').value = name;
}
function setCheckBox(obj){
	var unitId = document.getElement('input[name=_u_]').value.split(',')[1];
	var valueKey = $fnv(unitId+'.valuekey');
	var nameKey = $fnv(unitId+'.namekey');
	var row = obj.getParent('tr');
	var value = row.getElement('input[name='+unitId+'.list.'+valueKey+']').value;
	var name = row.getElement('input[name='+unitId+'.list.'+nameKey+']').value;
	var values = $fnv(unitId+'.values');
	var names = $fnv(unitId+'.names');
	if(values){
		var values = ','+values+',';
		var names = ','+names+',';
	}
	if(obj.checked){
		if(values){
			values+=value+',';
			names+=name+',';
		}else{
			values=','+value+',';
			names=','+name+',';
		}
	}else{
		values = values.replace(','+value+',',',');
		names = names.replace(','+name+',',',');
		if(values==',') values='';
		if(names==',') names='';
	}
	//重新设置values和names
	$fn(unitId+'.values').value = values.substring(1,values.length-1);
	$fn(unitId+'.names').value = names.substring(1,names.length-1);
}
// 页面初始化
function initSelect(){
	//外层单元编号（查询单元）
	var unitId = document.getElement('input[name=_u_]').value.split(',')[1];
	//1、刚弹出页面时，设置值和名称；查询或下一页时，不会取父页面
	if($fnv(unitId+'.values')=='.'){
		$fn(unitId+'.values').value=window.parent.getValues();
		$fn(unitId+'.names').value=window.parent.getNames();
	}
	//2、初始化选中状态
	var selectBoxs=document.getElements('input[name=_s_'+unitId+'.list]');
	if(selectBoxs && selectBoxs.length>0){
		var valueKey = $fnv(unitId+'.valuekey');
		var valueObjs=document.getElements('input[name='+unitId+'.list.'+valueKey+']');
		var values = ','+$fnv(unitId+'.values')+',';
		for(var i=0;i<selectBoxs.length;i++){
			if(values.indexOf(','+valueObjs[i].value+',')>=0){
				selectBoxs[i].value = '1';
				selectBoxs[i].parentNode.childNodes[1].checked=true;
			}
		 }
	}
}
//关闭
function closeDialog(){
	window.parent.selectDialog.close();
}
//默认带clear参数，去掉
function _to(obj,pageNumber){
	$(obj).getParent('div').getElement('input').value=pageNumber;
	var url = document.getElement('input[name=_u_]').value.replace(',','/');
	url = '/'+url+'.do';
	doSubmit(url);
}
/*覆盖全选事件，最后添加了setCheckBox(box);*/
function $fSelectAll(uid,obj){
	var _s=$$('input[name=_s_'+uid+']');
	for(var i=0;i<_s.length;i++){
		var box = $(_s[i].parentNode.childNodes[1]);
		_s[i].value=(obj.checked?'1':'0');
		if(box.checked!=obj.checked){
			box.checked=obj.checked;
			setCheckBox(box);
		}
	}
}
