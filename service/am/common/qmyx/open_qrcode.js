var myDialog;

function open_qrcode_MyDialog(url){
	myDialog = new MooDialog.Iframe('/common/qmyx/qrcode.html?qrcode_url_key='+url,{
		'class': 'MooDialog myDialog'
	});
}