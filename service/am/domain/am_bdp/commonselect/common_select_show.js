

/***
*unitName 单元名
*nodeName  选择需要显示名称的元素名称
* nodeName查看模式需要选择为文本带值
*/
function selectBook(unitName,nodeName){
	//当前弹出层list页面所有数据
	var selectArray=$$('input[name=_s_'+unitName+']');
	//当前弹出层list页面所有数据的id
	var idArray=$$('input[name='+unitName+'.id]');
	var nameArray=null;
	if(nodeName){
		nameArray=$$('input[name='+unitName+'.'+nodeName+']');
	}
	//获取隐藏域中的存入session中的值
	var parentid=$$('input[name='+unitName+'.parentid]')[0].value;
	var parentName=$$('input[name='+unitName+'.parentname]')[0].value;
	var index=$$('input[name='+unitName+'.index]')[0].value;
	var finished = false;
	var parent = window.parent;
	
	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			
			parent.document.getElementsByName(parentName)[index].value=idArray[i].value;
			if(nameArray){
				parent.document.getElementsByName(parentid)[index].value=nameArray[i].value;
			}
			
			
			finished = true;
			break;
		}
	}
	if(finished){
		parent.myDialog.close(); 
	}
}

function openDialog(s,popupUnitName){
    var index=0;
    var elementName = s.get('name');
    if(s.parentNode.parentNode.id!=""){
        index = s.parentNode.parentNode.id;
    }
    window.open('/am_bdp/'+popupUnitName+'.do?parentid='+elementName+'&index='+index+'' +
        '&clear=am_bdp.'+popupUnitName+'.query ',
        '',
        'height=600,width=900,top=10,left=100,scrollbars =yes,toolbar=no,menubar=no');
}

/**选择值**/
function selectValue( unitName){ 
	//var parentid = request("parentid");
	//当前弹出层list页面所有数据
	var selectArray=$$('input[name=_s_'+unitName+']');
	//当前弹出层list页面所有数据的id
	var idArray=$$('input[name='+unitName+'.id]');
	var nameArray=$$('input[name='+unitName+'.name]');
	//获取隐藏域中的存入session中的值
	var parentid=$$('input[name='+unitName+'.parentid]')[0].value;
	var parentName=$$('input[name='+unitName+'.parentname]')[0].value;
	var index=$$('input[name='+unitName+'.index]')[0].value;
	var finished = false;
	var parent = window.parent;

	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			parent.document.getElementsByName(parentid)[index].value=nameArray[i].value;
			
			try{
				parent.document.getElementsByName(parentName)[index].value=idArray[i].value;
			}catch(e){
			}
			
			finished = true;
			try{
				parent.document.getElementsByName(parentid)[index].fireEvent("onchange"); 
				parent.document.getElementsByName(parentid)[index].onchange(); 
			}catch(e){} 
			break;
		}
	}
	if(finished){
		var parent = window.parent;
		parent.myDialog.close();
	}
}



//处理地址传值
    function request(paras)
    {
        var url = location.href;
        var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");
        var paraObj = {}
        for (i=0; j=paraString[i]; i++){
            paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);
        }
        var returnValue = paraObj[paras.toLowerCase()];
        if(typeof(returnValue)=="undefined"){
            return "";
        }else{
            return returnValue;
        }
    };


