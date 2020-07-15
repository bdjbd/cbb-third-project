/**
 * 
 */
var clazzVale="3";

function selectServerClazz(e){
	//alert(e.options[e.options.selectedIndex].innerHTML);
	clazzVale=e.options[e.options.selectedIndex].value;
}

function saveServerClazz(memberCode){
	if(clazzVale==3){
		if(confirm("你确定要为此用户添加所有服务项目吗？")){
			doAjax('/wwd/memberability.list/batchsave.do?member_code='+memberCode+"&clazzVale="+clazzVale);
		}
	}else{
		doAjax('/wwd/memberability.list/batchsave.do?member_code='+memberCode+"&clazzVale="+clazzVale);
	}
}