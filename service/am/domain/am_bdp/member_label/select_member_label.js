function selectMember(labelId){
	//获取列表选择列 (多选,select[i]='0'代表未选中,select[i]='1'代表选中) 
	var select = $$('input[name=_s_am_membe_labelr.list]');
	//bookId[i]为列表第i项的主键id
	var bookId = $$('input[name=am_membe_labelr.list.id.k]');

	var msg='';

	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg+=bookId[i].value+',';
		}
	}
	if(msg){
		doLink("/am_bdp/am_membe_labelr.list/addtolabel.do?labelId="+labelId+"&addIds="+msg);
	}else{
		alert('没有选择数据');
	}
}