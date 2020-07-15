
function FAI_OCX_OpenDoc(ReadOnly)
{
    try{
		var URL=document.getElementsByName("editoffice.filespath")[0].value;
        FAI_OCX_OBJ = document.getElementById("FAI_OCX");
		var addres =  document.getElementsByName("editoffice.serveraddres")[0].value;
		var fp=decodeURI("http://"+addres+"/files/"+URL);
        FAI_OCX_OBJ.OpenFromURL(fp);
		//FAI_OCX_OBJ.MaxUploadSize=102400000;
    }catch(err){
        alert("请设置IE浏览器,允许浏览器安装ActiveX控件后重新进入此页面.");
        return ;
    }
  
//读写权限
    if(ReadOnly=="true"||ReadOnly=="True")
    {
        //设置只读打开
        FAI_OCX_OBJ.SetReadOnly(true);
        //隐藏工具栏
        FAI_OCX_OBJ.Toolbars=true;
        //隐藏工具菜单
        FAI_OCX_OBJ.IsShowToolMenu=true;
        //隐藏菜单
        FAI_OCX_OBJ.Menubar=true;
        //禁止拷贝
        FAI_OCX_OBJ.IsNoCopy=true;
        //禁止保存
        FAI_OCX_OBJ.EnableFileCommand(3) = false;
        //禁止关闭
        FAI_OCX_OBJ.EnableFileCommand(2) = false;
        //禁止另存
        FAI_OCX_OBJ.EnableFileCommand(4) = false;
        //禁止新建
        FAI_OCX_OBJ.EnableFileCommand(0) = false;
    }
    else
    {
        //设置读写打开
        FAI_OCX_OBJ.SetReadOnly(false);
        //隐藏工具栏
        FAI_OCX_OBJ.Toolbars=true;
        //隐藏工具菜单
        FAI_OCX_OBJ.IsShowToolMenu=true;
        //隐藏菜单
        FAI_OCX_OBJ.Menubar=true;

    }
	        //保留痕迹
		SetReviewMode(true);
}

function CloseSave()
{
   var comActiveX=null;
	 try
	 {
		   comActiveX= new ActiveXObject("NTKO.OfficeControl");
		   var save=confirm("是否保存文件内容?");
		   if(save==true)
		   {
				FAI_OCX_SaveEditToServerDisk();                          
		   }
	 }
	 catch (ee)
	 {
	 }       
}

//保存文件
function FAI_OCX_SaveEditToServerDisk()
{
	var newwin,newdoc;
	try
	{
	 	if(!FAI_OCX_doFormOnSubmit()){
			return;
		}
		var path=document.getElementsByName("editoffice.savapath")[0].value;
		var fileName=document.getElementsByName("editoffice.filespath")[0].value;
		var retHTML = FAI_OCX_OBJ.SaveToURL
		(
			"/domain/app/office/upLoadOfficeFile.jsp?path="+path+"/files/"+fileName,  
			"EDITFILE",	
			"", //other params seperrated by '&'. For example:myname=tanger&hisname=tom
			encodeURI(path+"/files/"+fileName) //filename
		); //this function returns dta from server

        //alert("保存成功！");
	}
	catch(err){
 		//alert("err:" + err.number + ":" + err.description);
	}
}

function FAI_OCX_doFormOnSubmit()
{
	var form = document.forms[0];
  	if (form.onsubmit)
	{
    	var retVal = form.onsubmit();
     	if (typeof retVal == "boolean" && retVal == false)
       	return false;
	}
	return true;
}

//是否显示批注
function SetReviewMode(boolvalue)
{
	if(FAI_OCX_OBJ.doctype==1)
	{
		FAI_OCX_OBJ.ActiveDocument.TrackRevisions = boolvalue;//设置是否保留痕迹
	}
} 
//在线打开文件
/**
id:td单元格的id
tableName:数据库表名称
dataid:数据id
ename:文件元素的编号
*/
function formOnload(id,tableName,dataObj,ename) {
  var dataid=document.getElementsByName(dataObj)[0].value;
  var address=window.location.host;
  var Request = new Object();
  Request = GetRequest();
  var td = document.getElementById(id);
  var link = td.getElementsByTagName("a");
  for(var i=0;i<link.length;i++) {
	var att = $(link[i]).get('onclick');
	att = att.substring(att.indexOf('&fileName=')+10);
	var fileName = att.substring(0,att.indexOf('\')'));
	fileName = fileName.substring(att.lastIndexOf('\/')+1,att.length);
	$(link[i]).id=fileName;
    var strSuffix = link[i].innerText;
    var intIndex = strSuffix.lastIndexOf("(");
    strSuffix = strSuffix.substr(0, intIndex - 1);
    intIndex = strSuffix.lastIndexOf(".");
    strSuffix = strSuffix.substr(intIndex + 1).toLowerCase();
    if (strSuffix == "xls" || strSuffix == "xlsx" || strSuffix == "doc" || strSuffix == "docx" || strSuffix == "ppt" || strSuffix == "pptx") {
      link[i].onclick = function() {
		//var fileName = window.event.srcElement.innerText;
        //var lastindex = fileName.lastIndexOf("(");
        //fileName = fileName.substr(0, lastindex - 1);
		var fileName=window.event.srcElement.id;
		msgbox('友好提示！','请选择您的操作！',tableName,dataObj,ename,fileName);
      }
    }
  }
}

//获取url中的参数
function GetRequest() {
  var url = location.search; //获取url中"?"符后的字串
  var theRequest = new Object();
  if (url.indexOf("?") != -1) {
    var str = url.substr(1);
    strs = str.split("&");
    for(var i = 0; i < strs.length; i ++) {
      theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
    }
  }
  return theRequest;
}

var objWindow;
function openWindow(url) {
  if(!objWindow || objWindow.closed) {
    var iWidth = 1070;	//弹出窗口的宽度,自定义
    var iHeight = 1000;	//弹出窗口的高度，自定义
    var iTop = 0;    //获得窗口的垂直位置;
    var iLeft = (window.screen.availWidth-10-iWidth)/2;      //获得窗口的水平位置;
    objWindow = window.open(url,
      "","height=" + iHeight + ",width=" + iWidth + ",top=" + iTop + ",left=" + iLeft + ",location=no,scrollbars=auto,resizable=no");
  } else {
    objWindow.focus();
  }
}


//点击关闭按钮
function closeFile(){
	msgbox1('友好提示！','请确认是否保存文件内容！');
}