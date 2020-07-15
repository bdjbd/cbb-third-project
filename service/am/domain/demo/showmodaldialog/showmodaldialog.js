function openDialog(){   
	var bookid = $$('input[name=showmodaldialog.bookid]')[0].value;
	window.showModalDialog('/demo/showmodaldialog.son.do?clear=demo.showmodaldialog.son.query&bookid='+bookid,self,'dialogWidth=500px;dialogHeight=300px'); 
}

function xxx(value){   
	alert(value);   
}
