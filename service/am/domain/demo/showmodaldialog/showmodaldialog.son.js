function selectBook(){   
	var selectArray=$$('input[name=_s_showmodaldialog.son.list]');
	var idArray=$$('input[name=showmodaldialog.son.list.bookid]');
	var nameArray=$$('input[name=showmodaldialog.son.list.bookname]');
	var finished = false;
	for(var i=0;i<selectArray.length;i++){
		if(selectArray[i].value=='1'){
			var parent = window.dialogArguments;
			parent.document.getElementsByName('showmodaldialog.bookid')[0].value = idArray[i].value;
			parent.document.getElementsByName('showmodaldialog.bookname')[0].value = nameArray[i].value;
			finished = true;
			break;
		}
	}
	if(finished){
		closeDialog();
	}
}

function closeDialog(){   
	window.close(); 
}

function controlParent(){   
	var parent = window.dialogArguments;
	//1、获取父窗口对象的值
	var a = parent.document.getElementsByName('showmodaldialog.a')[0].value;
	//2、设置父窗口对象的值
	parent.document.getElementsByName('showmodaldialog.b')[0].value = a;
	//3、调用父窗口的方法
	parent.xxx('son');
}
