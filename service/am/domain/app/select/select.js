var myDialog;
/*用户选择*/
function userDialog(domain,unitId,valuesKey,namesKey,obj){openSelectDialog('选择用户','select.user',false,'UserDialog',domain,unitId,valuesKey,namesKey,obj);}
function usersDialog(domain,unitId,valuesKey,namesKey,obj){openSelectDialog('选择用户','select.user',true,'UserDialog',domain,unitId,valuesKey,namesKey,obj);}
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
	selectDialog = new MooDialog.Iframe('/app/'+urlUnitId+'.do?checkbox='+(isCheckBox?'1':'0')+'&clear=app.'+urlUnitId,{
		title: title,
		'class': 'MooDialog '+css
	});
}
function memberDialog(){
	//selectDialog=new MooDialog.Iframe('/wwd/ws_select_member.do?m=s&clear=wwd.ws_member.query',{title:'选择用户','class': 'MooDialog UserDialog'});
	selectDialog=open('/wwd/ws_select_member.do?m=s&clear=wwd.ws_member.query','选择会员','width=660,height=600,left=120,top=70');
}

function selectMember(){
	var select = $$('input[name=_s_ws_member.list]');
	var member_code = $$('input[name=ws_member.list.member_code.k]');
	var member_names=$$('input[name=ws_member.list.mem_name]')
	var msg='';
	var name=''
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg=member_code[i].value;
			name=member_names[i].value
			break;
		}
	}
	if(msg){
		$(window.opener.document).getElementById("member_name_name_id").value=name;
		$(window.opener.document).getElementById("member_name_id_id").value=msg;
		window.close();
	}else{
		alert('没有选择数据');
	}
	
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
		var url ='/app/select.user/load.do?ajaxd='+selectDomain+'&ajaxu='+selectUnitId;
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



var mintaCodeType;
function selectMaterilTypeCode(e){
	mintaCodeType=e;
	//e.setAttribute("id","formatSetCodeId");
	if(mintaCodeType.getAttribute("readOnly")!=="readonly"){
		selectDialog=open('/am_bdp/p2p_mater_type_code.do?m=s','选择物资cs','width=900,height=600,left=150,top=50');
	}
}

function setCodeType(cname,val,name){
	
	if(mintaCodeType){
		mintaCodeType.value=name;
		mintaCodeType.nextSibling.nextSibling.value=val;
		
	}
}

function selectCommoditFromates(e){
	mintaCodeType=e;
	selectDialog=open('/am_bdp/p2p_mater_type_code.do?m=s','选择物资','width=900,height=600,left=150,top=50');
}


/**选择商品**/
var commodityEle=null;

/**选择商品**/
function openSelectCommdWind(e){
	commodityEle=e;
	if(commodityEle.getAttribute("readOnly")!=="readonly"){
		var url='/am_bdp/mall_commodity_select.do?m=s';
		//window.open(url,'选择商品','height=650,width=1000,top=70,left=100,toolbar=no,menubar=no');
		myDialog = new MooDialog.Iframe(url,{
			title: '',
			'class': 'MooDialog myDialog'
		});
	}
}

/**
* 选择商品回调函数
**/
function setSelectCommodity(selectID,selectName){

	if(commodityEle){
		commodityEle.value=selectName;
		commodityEle.nextSibling.value=selectID;
	}
}