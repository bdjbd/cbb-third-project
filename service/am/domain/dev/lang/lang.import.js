function validateIndex(obj){
	if (obj.value && (!_isInt(obj.value) || parseInt(obj.value)<2)){
		alert('请填入大于1的整数');
		obj.value='';
	}
}

function checkLang(obj){
	var tr = $(obj).getParent('tr');
	var check = tr.getElement('input[name=check]');
	var index = tr.getElement('input[name=index]');
	
	if(obj.checked){
		check.value='1';
		index.set('readonly','');
		index.removeClass('i');
	}else{
		check.value='0';
		index.set('readonly','readonly');
		index.addClass('i');
	}
}

function confirmLang(url){
	if(!$$('input[name=lang.import.file]')[0].value){
		alert('未设定excel文件');
		return;
	}
	var check = $$('input[name=check]');
	if(!check){
		return ;
	}
	var index = $$('input[name=index]');
	var mapping = $$('input[name=mapping]');
	var msg = '';
	for(var i=0;i<check.length;i++){
		if(check[i].value=='1'){
			var name = mapping[i].getParent('td').get('text');
			if(!index[i].value){
				alert('未指定"'+name+'"的列数！');
				return;
			}else{
				msg +=name+'：第'+index[i].value+'列\n';
			}
		}
	}
	if (!msg){
		alert('未指定语言！');
		return;
	}
	if (confirm('以下语言将被替换，你确认吗？\n'+msg)){
		document.forms[0].action=url;
		doSubmit();
	}
}