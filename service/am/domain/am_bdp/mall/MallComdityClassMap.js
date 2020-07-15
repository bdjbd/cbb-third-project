var myDialog;
/**选择商品**/
var commodityEleDetails=null;
/**选择标签**/
var commodityClass=null;

/**选择商品标签**/
var commodityMark=null;

/**选择商品**/
function openSelectCommdDetailsWind(e){
	commodityEleDetails=e;
	if(commodityEleDetails.getAttribute("readOnly")!=="readonly"){
		var url='/am_bdp/mall_commodity_select.do?m=s&operation=details';

		myDialog = new MooDialog.Iframe(url,{
			title: '',
			'class': 'MooDialog myDialog'
		});

		//window.open('/am_bdp/mall_commodity_select.do?m=s&operation=details','选择商品','height=650,width=1000,top=70,left=100,toolbar=no,menubar=no');
	}
}

/**
* 选择商品回调函数
**/
function setSelectCommodityDetails(commoditData){
	
	if(commodityEleDetails){
		commodityEleDetails.value=commoditData[0].NAME;
		commodityEleDetails.nextSibling.value=commoditData[0].ID;
		// commodityEleDetails.parentNode.nextSibling.firstChild.value=commoditData[0].MATERID;
		//commodityEleDetails.parentNode.nextSibling.nextSibling.firstChild.value=commoditData[0].COMSTATE;
		commodityEleDetails.parentNode.nextSibling.firstChild.value=commoditData[0].COMSTATE;
	}
}
/**选择标签**/
function openSelectMarkWind(e){
	commodityClass=e;
	if(commodityClass.getAttribute("readOnly")!=="readonly"){
		window.open('/am_bdp/mall_mark_select.do?m=s&operation=details','选择标签','height=650,width=1000,top=70,left=100,toolbar=no,menubar=no');
	}
}

/**
* 设置标签回调函数
*/
function setNameDateValue(markObj){
	if(commodityClass!=null){
		// 回填名称
		commodityClass.value = markObj.NAME;
		// 回填时间
		commodityClass.parentNode.nextSibling.firstChild.value = markObj.TIME;
	}
	if(commodityMark!=null){
		// 回填名称
		commodityMark.value = markObj.NAME;
		// 回填时间
		//commodityMark.nextSibling.value = markObj.ID;
		commodityMark.parentNode.nextSibling.firstChild.value = markObj.TIME;
	}
}

/**选择商品标签**/
function openSelectCommodityMarkWind(e){
	commodityMark=e;
	if(commodityMark.getAttribute("readOnly")!=="readonly"){
		window.open('/am_bdp/mall_mark_select.do?m=s&operation=details','选择标签','height=650,width=1000,top=70,left=180,toolbar=no,menubar=no');
	}
}



//弹出选择商品所属店铺
function selectComdityStroe(s,nameELe){
	var mallClass=document.getElementById("mall_commodity.form.mall_class").value;

	if(mallClass==""){
		alert("请先选择所属商城");
		return ;
	}
	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	}
	//queryMallClass
	var url="/am_bdp/select_mall_store.do?m=s&queryMallClass="+
		mallClass+"&parentid="+parentid+"&index="+index+"&nameELe="+nameELe;
	//window.open(url,
	//	'',
	//	'height=650,width=1010,top=5,left=10,scrollbars =yes,toolbar=no,menubar=no');

	myDialog = new MooDialog.Iframe(url,{
			title: '',
			'class': 'MooDialog myDialog'
		});


}

/**
 * 商品特色设置，选择车型分类
 *
 * @param      {<type>}  s           事件触发单元
 * @param      {string}  mallClassId  店铺分类
 */
function selectCarTypeName(s,nameELe,mallClassId){
	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	}
	//queryMallClass
	var url="/am_bdp/select_mall_store.do?m=s&queryMallClass="+
		mallClassId+"&parentid="+parentid+"&index="+index+"&nameELe="+nameELe;
	window.open(url,
		'',
		'height=650,width=1010,top=5,left=10,scrollbars =yes,toolbar=no,menubar=no');

}


/**选择店铺**/
function selectStroe(){ 
	//var parentid = request("parentid");
	//当前弹出层list页面所有数据
	var selectArray=$$('input[name=_s_select_mall_store.list]');
	//当前弹出层list页面所有数据的id
	var idArray=$$('input[name=select_mall_store.list.id]');
	//获取隐藏域中的存入session中的值
	var parentid=$$('input[name=select_mall_store.list.parentid]')[0].value;
	var index=$$('input[name=select_mall_store.list.index]')[0].value;
	var nameELe=$$('input[name=select_mall_store.list.name_ele]')[0].value;
	//获取具体显示名称
	var nameArray=$$('input[name=select_mall_store.list.store_name]');

	var finished = false;

	var parent = window.parent;

	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			parent.document.getElementsByName(parentid)[index].value=nameArray[i].value;
			parent.document.getElementsByName(nameELe)[index].value=idArray[i].value;
			finished = true;
			try{
				parent.document.getElementsByName(parentid)[index].fireEvent("onchange"); 
				parent.document.getElementsByName(parentid)[index].onchange(); 
			}catch(e){}
			break;
		}
	}
	if(finished){
		parent.myDialog.close();
	}
}


function clearInput(eleName){
	$$('input[name='+eleName+']')[0].value="";
}


/**
 * 选择日期类型 1：整周；2：选择星体
 */
function selectInputDataType(s){
	if(s.value==2){
		document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.style="display:inherit;"
		document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.setAttribute("style","display:inherit;");
	}else{
		document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.style="display:none;";
		document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.setAttribute("style","display:none;");
	}
}

function hiddenSelectWeek(){
	document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.setAttribute("style","display:none;");
	document.getElementsByName("mall_commodityspec_fs_form.weeks")[0].parentNode.style="display:none;";

}

/**
 * 提交数据，批量生成数据
 */
function executeData(){
	var inputStartDate=$$("input[name=mall_commodityspec_fs_form.intput_start_date]")[0].value;
	var inputEndDate=$$("input[name=mall_commodityspec_fs_form.intput_end_date]")[0].value;
	var intpuDataeType=$$("select[name=mall_commodityspec_fs_form.intput_data_type]")[0].value;
	var pirce=$$("input[name=mall_commodityspec_fs_form.intput_price]")[0].value;
	var childPrice=0;

	if($$("input[name=mall_commodityspec_fs_form.input_children_price]")
		&&$$("input[name=mall_commodityspec_fs_form.input_children_price]").length>0){
		childPrice=$$("input[name=mall_commodityspec_fs_form.input_children_price]")[0].value;
	}
	
	var store=0;
	if($$("input[name=mall_commodityspec_fs_form.intput_store]").length>0){
		store=$$("input[name=mall_commodityspec_fs_form.intput_store]")[0].value;
	}
	var weeks=0;
	if($$("input[name=mall_commodityspec_fs_form.weeks]").length>0){
		weeks=$$("input[name=mall_commodityspec_fs_form.weeks]")[0].value;
	}
	

	var url="/am_bdp/mall_commodityspec_fs_form/execute_date.do?inputStartDate="
	+inputStartDate+"&inputEndDate="+inputEndDate
	+"&intpuDataeType="+intpuDataeType
	+"&pirce="+pirce
	+"&store="+store
	+"&weeks="+weeks
	+"&childPrice="+childPrice;
	doAjax(url)
}