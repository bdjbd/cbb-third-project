//��ȡ��Ƭ�ϴ���·��
function doSave(uid){
	var userid=document.getElementsByName(uid+".userid")[0].value;//���
	var rootpath='files/AUSER/'+userid;

	var photo = document.getElementsByName(uid+".photo")[0].value;//��Ƭ
	if(photo!=""){
		var photoName = getFileName(photo);
		document.getElementsByName(uid+".photopath")[0].value=rootpath+'/photo/'+photoName;
	}
	else{
		document.getElementsByName(uid+".photopath")[0].value="";
	}
	doSubmit('/am_bdp/user.form/save.do');
}

/*��ȡ�ļ���*/
function getFileName(path){
	var pos1 = path.lastIndexOf('/');
	var pos2 = path.lastIndexOf('\\');
	var pos  = Math.max(pos1, pos2)
	if( pos<0 )
	return path;
	else
	return path.substring(pos+1);
}