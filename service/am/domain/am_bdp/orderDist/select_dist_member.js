var myDialog;


function closeDialog(){
	var parent = window.parent;
	parent.myDialog.close();
}

/**选择项目**/
function selectListData(unitListName,membername){ 
	//var parentid = request("parentid");
	//当前弹出层list页面所有数据
	var selecter='input[name=_s_'+unitListName+']';
	var selectArray=$$(selecter);
	if(selectArray&&selectArray.length==0){
		alert("请选择数据！");
		return ;
	}
	//当前弹出层list页面所有数据的id
	selecter='input[name='+unitListName+'.id]';
	var idArray=$$(selecter);
	var nameArray=$$('input[name='+unitListName+'.'+membername+']');
	//获取隐藏域中的存入session中的值
	selecter='input[name='+unitListName+'.parentid]';
	var parentid=$$(selecter)[0].value;
	var parentname=$$('input[name='+unitListName+'.parentname]')[0].value;
	selecter='input[name='+unitListName+'.index]';
	var index=$$(selecter)[0].value;

	var finished = false;
	var parent = window.parent;

	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			parent.document.getElementsByName(parentname)[index].value=idArray[i].value;
			parent.document.getElementsByName(parentid)[index].value=nameArray[i].value;
			finished = true;
			try{
				parent.document.getElementsByName(parentid)[index].fireEvent("onchange"); 
				parent.document.getElementsByName(parentid)[index].onchange(); 
			}catch(e){} 
			break;
		}
	}
	if(finished){
		closeDialog();
	}else{
		alert("请选择数据！");
	}
}


function openSelectList(selectUnitUrl,s,nodename){   
	//var bookid = $$('input[name=showmodaldialog.bookid]')[0].value;
	//s 当前点击的name，name 单元id backname 隐藏元素id
	var parentid = s.get('name'); 
	var type=s.tagName;
	var index=0;
	if($$(type+'[name='+s.get('name')+']').length>1){
		index=s.parentNode.parentNode.id
	}
	//window.open('/gddl/select_auser.do?parentid='+parentid+'&parentname='+name+'&backname='+backname+'&clear=gddl.select_auser.query','','height=450,width=600,top=10,left=200,scrollbars =yes,toolbar=no,menubar=no'); 
	
	selectUnitUrl=selectUnitUrl+'&parentid='+parentid+'&index='+index+'&m=s';

	if(nodename){
		selectUnitUrl=selectUnitUrl+'&parentname='+nodename;
	}

	//window.open(selectUnitUrl+'&parentid='+parentid+'&index='+index
	//	+'&m=s',
	//	'',
	//	'height=650,width=1010,top=5,left=10,scrollbars =yes,toolbar=no,menubar=no'); 
	myDialog = new MooDialog.Iframe(selectUnitUrl,{
		title: '',
		'class': 'MooDialog myDialog'
	});


}