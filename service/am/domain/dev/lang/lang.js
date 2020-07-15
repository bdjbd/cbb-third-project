function toLowerCase(obj){
	if(obj.value){
		obj.value = obj.value.toLowerCase();
	}
}

function doCreate(url){
	if (confirm('“自动创建”将把：\n1、单元标题（显示的标题）\n2、元素（过滤隐藏、闲置、单元、树、统计图）名称\n3、枚举节点名称\n4、树节点名称\n转换为多语言定义，将跳过空字符、已定义或键值已存在的项。\n\n在此之前，您最好确认现有中文已调整合适。\n\n此操作不可恢复，你确认吗？')){
		if (confirm('请再次确认：此操作不可恢复，你确认吗？')){
			document.forms[0].action=url;
			doSubmit();
		}
	}
}
