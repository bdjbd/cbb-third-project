var myDialog;

function openMyDialog(){
	myDialog = new MooDialog.Iframe('/demo/moodialog.iframe.do',{
		title: '标题',
		'class': 'MooDialog myDialog'
	});
}

function setMyValue(){
	var parent = window.parent;
	parent.document.getElement('input[name=moodialog.a]').value=document.getElement('input[name=moodialog.iframe.x]').value;
	parent.myDialog.close();
}
