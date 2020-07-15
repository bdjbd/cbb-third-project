//util
function getElement(){
	var grid=_getGridWithoutId(event.srcElement);
	for(var i=0;i<all.length;i++){var e=all[i];if(e.grid==grid)return e;}
	return xs;
}
function getShadow(e){
	var shadows=document.all("_elements").getElementsByTagName("SPAN");
	for(var i=0;i<shadows.length;i++){if(shadows[i].get('area')==e.area && shadows[i].get('group')==e.group && shadows[i].get('order')==e.order){return shadows[i];}}
}
function write(a){
	var html="";
	for(var i=0;i<a.length;i++){html+=a[i].toString();}
	return html;
}
function showGroup(groupId){_showGroupWithGrid($fTable(event.srcElement),groupId);}
function getColgroups(){
	var html="";
	var pecent=100/column;
	for(var i=0;i<column;i++){html+="<colgroup width="+pecent+"%></colgroup>"}
	return html;
}
function sort(e1,e2){return parseInt(e1.order)-parseInt(e2.order);}
function getUnitId(){
	var unitId=document.all("u.uid").value;
	return (!unitId || unitId=='')?'xxx':unitId;
}
//control
function addButtonControl(id){
	addElement('41',id);
	xs.setArea('t');
}
function addBackControl(){
	addButtonControl('back');
	xs.setName('返回');
	xs.setLink('/common/images/tool/back.png');
	xs.grid.all("e.cu").value="onclick=doLink('/"+currentDomain+"/xxx.do')";
}
function addSaveControl(){
	addButtonControl('save');
	xs.setName('保存');
	xs.setLink('/common/images/tool/save.png');
	xs.grid.all("e.asm").value="保存成功";
	xs.grid.all("e.afm").value="保存失败";
	xs.setAuth("1");
	xs.setIsAction("1");
	xs.load();
}
function addNewControl(){
	addButtonControl('new');
	xs.setName('新增');
	xs.setLink('/common/images/tool/new.png');
	xs.grid.all("e.cu").value="onclick=doLink('/"+currentDomain+"/xxx.do?m=a')";
}
function addDeleteControl(){
	addButtonControl('delete');
	xs.setName('删除');
	xs.setLink('/common/images/tool/delete.png');
	xs.grid.all("e.cu").value="onclick=doListDelete('/"+currentDomain+"/"+getUnitId()+"/delete.do','"+getUnitId()+"')";
	xs.setAuth("1");
	xs.setIsAction("1");
	xs.load();
}
function addAddControl(){
	addButtonControl('add');
	xs.setName('添加');
	xs.setLink('/common/images/tool/add.png');
	if(isList){
		xs.grid.all("e.cu").value="onclick=doListAdd('"+getUnitId()+"')";
	}else{
		xs.grid.all("e.cu").value="onclick=doFormAdd('"+getUnitId()+"')";
	}
}
function addRemoveControl(){
	addButtonControl('remove');
	xs.setLink('/common/images/tool/remove.png');
	xs.setName('移除');
	xs.grid.all("e.cu").value="onclick=doRemove('"+getUnitId()+"')";
}
function addClearControl(){
	addButtonControl('clear');
	xs.setName('重置');
	xs.setLink('/common/images/tool/clear.png');
	xs.grid.all("e.aa").value='com.fastunit.support.action.ClearAction';
	xs.setIsAction("1");
	xs.load();
}
function addQueryControl(){
	addButtonControl('query');
	xs.setLink('/common/images/tool/query.png');
	xs.setName('查询');
	xs.grid.all("e.aa").value='com.fastunit.support.action.QueryAction';
	xs.setIsAction("1");
	xs.load();
}
function addScanControl(){
	addElement('11','scan');
	xs.setName('查看');
	xs.grid.all("e.l").value="/"+currentDomain+"/xxx.do?m=s";
}
function addEditControl(){
	addElement('11','edit');
	xs.setName('修改');
	xs.grid.all("e.l").value="/"+currentDomain+"/xxx.do?m=e";
}
function addAjaxControl(){
	addButtonControl('load');
	xs.setName('Ajax');
	xs.setLink('/common/images/tool/ajax.png');
	xs.grid.all("e.aa").value='com.fastunit.support.action.AjaxAction';
	xs.grid.all("e.cu").value="onclick=doAjaxSubmit('/"+currentDomain+"/"+getUnitId()+"/"+xs.id+".do','id','domain','unitid')";
	xs.setAuth("1");
	xs.setIsAction("1");
	xs.load();
}
function addExcelControl(){
	addButtonControl('excel');
	xs.setName('导出');
	xs.setLink('/common/images/tool/excel.png');
	xs.grid.all("e.aa").value='com.fastunit.support.action.Unit2ExcelAction';
	xs.grid.all("e.cu").value="onclick=doDownload('/"+currentDomain+"/"+getUnitId()+"/"+xs.id+".do')";
	xs.setAuth("1");
	xs.setIsAction("1");
	xs.load();
}
function addPdfControl(){
	addButtonControl('pdf');
	xs.setName('导出');
	xs.setLink('/common/images/tool/pdf.png');
	xs.grid.all("e.aa").value='com.fastunit.support.action.Unit2PdfAction';
	xs.grid.all("e.cu").value="onclick=doDownload('/"+currentDomain+"/"+getUnitId()+"/"+xs.id+".do')";
	xs.setAuth("1");
	xs.setIsAction("1");
	xs.load();
}
function addFlowControl(id,name,confirm,msg,forward){
	addButtonControl(id);
	xs.setName(name);
	xs.setLink('/common/images/flow/'+id+'.png');
	xs.grid.all("e.aa").value='com.fastunit.support.action.FlowAction';
	xs.grid.all("e.asf").value=forward;
	xs.setAuth("1");
	xs.setIsAction("1");
	if(msg){
		xs.grid.all("e.asm").value=name+'成功';
		xs.grid.all("e.afm").value=name+'失败';
	}
	if(confirm){
		xs.grid.all("e.cu").value="onclick=doConfirm('/"+currentDomain+"/"+getUnitId()+"/"+xs.id+".do')";
	}
	xs.load();
}
//init
function en(e,name,css){
	var obj=e.grid.all(name);
	obj.readOnly=false;
	obj.className=css;
}
function di(e,name,css){
	var obj=e.grid.all(name);
	obj.value='';
	obj.readOnly=true;
	obj.className=css;
}
function configComponent(e){
	switch (e.c){
	case '0':
		di(e,'e.d','d2');di(e,'e.r','d2');di(e,'e.css','d2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '1':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '2':
		en(e,'e.d','y2');di(e,'e.r','d2');di(e,'e.css','d2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '3':
	case '42':
	case '43':
	case '44':
		en(e,'e.d','y2');en(e,'e.r','s2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '56':
		en(e,'e.d','y2');en(e,'e.r','s2');en(e,'e.css','y2');en(e,'e.w','s1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '5':
	case '21':
		di(e,'e.d','d2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '6':
	case '46':
	case '48':
		en(e,'e.d','y2');en(e,'e.r','s2');en(e,'e.css','y2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '53':
		en(e,'e.d','y2');en(e,'e.r','s2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '8':
	case '60':
	case '68':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '9':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '11':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');di(e,'e.w','d1');di(e,'e.h','d1');en(e,'e.l','y2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '12':
		di(e,'e.d','d2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');en(e,'e.l','y2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '13':
	case '30':
		di(e,'e.d','d2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');en(e,'e.l','y2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '20':
		di(e,'e.d','d2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '25':
	case '58':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');en(e,'e.w','s1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '41':
		di(e,'e.d','d2');di(e,'e.r','d2');di(e,'e.css','d2');di(e,'e.w','d1');di(e,'e.h','d1');en(e,'e.l','y2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '52':
		en(e,'e.d','y2');di(e,'e.r','d2');en(e,'e.css','y2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '80':
	case '85':
		di(e,'e.d','d2');en(e,'e.r','s2');di(e,'e.css','d2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');di(e,'e.cu','d6');di(e,'e.t','d2');break;
	case '87':
		di(e,'e.d','d2');en(e,'e.r','s2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');en(e,'e.l','y2');en(e,'e.cu','y6');en(e,'e.t','y2');break;
	case '90':
	case '91':
		en(e,'e.d','y2');di(e,'e.r','d2');di(e,'e.css','d2');en(e,'e.w','s1');en(e,'e.h','s1');di(e,'e.l','d2');en(e,'e.cu','y6');di(e,'e.t','d2');break;
	case '92':
		di(e,'e.d','d2');di(e,'e.r','d2');di(e,'e.css','d2');di(e,'e.w','d1');di(e,'e.h','d1');di(e,'e.l','d2');di(e,'e.cu','d6');di(e,'e.t','d2');break;
	case '99':
		en(e,'e.d','y2');en(e,'e.r','s2');en(e,'e.css','y2');en(e,'e.w','s1');en(e,'e.h','s1');en(e,'e.l','y2');en(e,'e.cu','y6');en(e,'e.t','y2');
	}
	if ("3"==unitC){e.grid.all('e.qm').style.display=(e.c=="1" || e.c=="2" || e.c=="8" || e.c=="43")?"block":"none";}
}
