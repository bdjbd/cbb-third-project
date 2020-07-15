/*会员选择*/
function memberDialog(domain,unitId,valuesKey,namesKey,obj){openSelectDialog('选择会员','select_am_members',false,'UserDialog',domain,unitId,valuesKey,namesKey,obj);}

/*通用方法*/
var selectDialog;
var selectDomain;
var selectUnitId;
var selectValuesKey;//存储选中值的元素编号
var selectNamesKey;//存储选中名称的元素编号
var selectObj;//触发事件的对象，用于列表定位本行
function openSelectDialog(title,urlUnitId,isCheckBox,css,domain,unitId,valuesKey,namesKey,obj){
	selectDomain=domain;
	selectUnitId=unitId;
	selectValuesKey=valuesKey;
	selectNamesKey=namesKey;
	selectObj = obj;
	selectDialog = new MooDialog.Iframe('/am_bdp/'+urlUnitId+'.do?checkbox='+(isCheckBox?'1':'0')+'&clear=am_bdp.'+urlUnitId,{
		title: title,
		'class': 'MooDialog '+css
	});
}
/*处理选中的值*/
function setValues(values,names){
	if(selectValuesKey){
		if(selectObj){
			$(selectObj).getParent('tr').getElement('input[name='+selectUnitId+'.'+selectValuesKey+']').value=values;
			$(selectObj).getParent('tr').getElement('input[name='+selectUnitId+'.'+selectNamesKey+']').value=names;
		}else{
			document.getElement('input[name='+selectUnitId+'.'+selectValuesKey+']').value=values;
			document.getElement('input[name='+selectUnitId+'.'+selectNamesKey+']').value=names;
		}
	}else{
		document.getElement('input[name='+selectUnitId+'.selectedvalues]').value=values;
		var url ='/am_bdp/select_am_members/load.do?ajaxd='+selectDomain+'&ajaxu='+selectUnitId;
		var obj=$fUnit(selectUnitId).getParent();
		new Request.HTML({url:encodeURI(url),async:true,update:obj}).post(document.forms[0]);
	}
	selectDialog.close();
}

/*供弹出窗调用*/
function getValues(){
	if(selectObj){
		return $(selectObj).getParent('tr').getElement('input[name='+selectUnitId+'.'+(selectValuesKey?selectValuesKey:'selectedvalues')+']').value;
	}else{
		return document.getElement('input[name='+selectUnitId+'.'+(selectValuesKey?selectValuesKey:'selectedvalues')+']').value;
	}
}
function getNames(){
	if(selectNamesKey){
		if(selectObj){
			return $(selectObj).getParent('tr').getElement('input[name='+selectUnitId+'.'+selectNamesKey+']').value;
		}else{
			return document.getElement('input[name='+selectUnitId+'.'+selectNamesKey+']').value;
		}
	}else{
		return '';
	}
}
