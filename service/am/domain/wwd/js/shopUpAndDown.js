function shopup(url, uid) {
	var table = $fGrid(uid);
	var action = false;
	var _s = table.getElements('input[name=_s_' + uid + ']');
	for ( var i = 0; i < _s.length; i++) {
		if (_s[i].value == '1') {
			action = true;
			break;
		}
	}
	if (action) {
		if (confirm("确认上架吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}



function shopdown(url, uid) {
	var table = $fGrid(uid);
	var action = false;
	var _s = table.getElements('input[name=_s_' + uid + ']');
	for ( var i = 0; i < _s.length; i++) {
		if (_s[i].value == '1') {
			action = true;
			break;
		}
	}
	if (action) {
		if (confirm("确认下架吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}


function submitm(url, uid) {
	var table = $fGrid(uid);
	var action = false;
	var _s = table.getElements('input[name=_s_' + uid + ']');
	for ( var i = 0; i < _s.length; i++) {
		if (_s[i].value == '1') {
			action = true;
			break;
		}
	}
	if (action) {
		if (confirm("确认提交吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}


function showStatus(data_status,comdity_id){
	if(data_status=='1'){
		window.location.href='/wwd/ws_commodity_name.form1.do?m=e&ws_commodity_name.form1.comdity_id='+comdity_id;

	}else if(data_status=='3'){
        window.location.href='/wwd/ws_commodity_name.form.do?m=e&ws_commodity_name.form.comdity_id='+comdity_id;
	}
}