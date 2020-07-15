//获取照片上传的路径
function doSave(uid){
	var userid=document.getElementsByName(uid+".userid")[0].value;//编号
	var rootpath='files/AUSER/'+userid;

	var photo = document.getElementsByName(uid+".photo")[0].value;//照片
	if(photo!=""){
		var photoName = getFileName(photo);
		document.getElementsByName(uid+".photopath")[0].value=rootpath+'/photo/'+photoName;
	}
	else{
		document.getElementsByName(uid+".photopath")[0].value="";
	}
	doSubmit('/am_bdp/user.form/save.do');
}

/*获取文件名*/
function getFileName(path){
	var pos1 = path.lastIndexOf('/');
	var pos2 = path.lastIndexOf('\\');
	var pos  = Math.max(pos1, pos2)
	if( pos<0 )
	return path;
	else
	return path.substring(pos+1);
}