//保存班组圈子信息
function doSave(uid){
	var code=document.getElementsByName(uid+".code")[0].value;//编号
	var rootpath='files/TC_TEAMCIRCLE/'+code;

	var backgroundimg = document.getElementsByName(uid+".background")[0].value;//封面
	if(backgroundimg!=""){
		var bgFileName = getFileName(backgroundimg);
		document.getElementsByName(uid+".bgimage")[0].value=rootpath+'/background/'+bgFileName;
	}
	var logoimg = document.getElementsByName(uid+".logoimage")[0].value;//LOGO
	if(logoimg!=""){
		var logoFileName = getFileName(logoimg);
		document.getElementsByName(uid+".logo")[0].value=rootpath+'/logoimage/'+logoFileName;
	}
	document.getElementsByName(uid+".cid")[0].value=code;
	doSubmit('/wd_base/tc_teamcircle.form/save.do');
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

function openWin(){
	var len=document.getElementsByName('tc_circlemembers.list.userid').length;
	window.open('/wd_base/auser.tc.do?clear=wd_base.auser.query.tc&len='+len,'select','left='+(window.screen.width-800)/2+',top='+((window.screen.height-450)/2-56)+',height=450,width=800,location=no,scrollbars=yes,resizable=1');
}