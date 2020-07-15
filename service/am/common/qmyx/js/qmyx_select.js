function showSelected(){
	var select = $$('input[name=_s_qmyx.mall_commodity.list]');
	var ID = $$('input[name=qmyx.mall_commodity.list.id.k]');
	var msg='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg+=ID[i].value+',';
		}
	}

	var str = "/am_bdp/qmyx.mall_commodity.form.do?m=e&qmyx.mall_commodity.form.id=&am_bdp.qmyx.mall_commodity.form.id=&commodity_list_array="+msg.substring(0,msg.length-1);
    doLink(str);
}
