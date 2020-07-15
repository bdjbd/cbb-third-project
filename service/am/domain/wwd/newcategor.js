function addCategory(){
	var list=$$('input[name=_s_newscategory.list]');
	if(list.length>=5){
		alert('最多可以添加5个分类');
	}else{
		doListAdd('newscategory.list')
	}
}

function releaseOnload(){
}

function release(nid,datastatus){
	if(datastatus==1){
		doAjax('/wwd/newsdetail.list/release.do?nid='+nid);
		}else{
			alert("此信息已发布");
			}
}