//张少飞  2017/7/26    fastUnit练习  JavaScript如何获取后台列表选中的数据
function showSelected(){
	var list = $$('input[name=_s_list.select]');  //得到整个列表所有行  当前单元编号为：list.select
	var bookId = $$('input[name=list.select.bookid.k]');  //得到整个列表的所有主键ID   当前主键ID为：bookid
	var msg='';
	for(var i=0;i<list.length;i++){
		if(list[i].value=='1'){        //'1'=已选中,'0'=未选中,
			msg+=bookId[i].value+',';  //拼接提示信息，最后会多出一个逗号
		}
	}
	if(msg){
		alert('选择的数据为：'+msg.substring(0,msg.length-1));   //去掉最后一个逗号
	}else{
		alert('没有选择数据');
	}
}