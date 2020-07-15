function kill(url, uid) {
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
		if (confirm("确认冻结吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}



function start(url, uid) {
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
		if (confirm("确认激活吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}



function started(url, uid) {
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
		if (confirm("确认启用吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}


function deleted(url, uid) {
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
		if (confirm("确认删除吗？")) {
			document.forms[0].action = url;
			document.forms[0].submit();
		}
	} else {
		alert("请选择一条数据。");
	}
}