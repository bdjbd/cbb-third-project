

var FAI_OCX_bDocOpen = false;
var FAI_OCX_filename;
var FAI_OCX_actionURL; //For auto generate form fiields
var FAI_OCX_OBJ=null; //The Control
var FAI_OCX_Username="tl";

//Open Document From URL
//文件的打开操作，可以加相应的参数：只读，读写，打印
function FAI_OCX_OpenDoc(URL,ReadOnly,Print)
{
	alert("eeee");
//if(URL!=null&&URL!="")
//{
    //FAI_OCX_OBJ = document.all.item("FAI_OCX");
    
    //解决bug_id 1712; update by yht
    try{
    
        FAI_OCX_OBJ = document.getElementById("FAI_OCX");
        FAI_OCX_OBJ.OpenFromURL(URL);
    }catch(err){
    
        alert("请设置IE浏览器,允许浏览器安装ActiveX控件后重新进入此页面.");
        return ;
    
    }
  
//读写权限
    if(ReadOnly=="true"||ReadOnly=="True")
    {
        //郭军刚 2007-07-26 修改
        //不显示痕迹
         if((document.all.zwtype.value!=".xls")&&(document.all.zwtype.value!=".xlsx"))
        {
         FAI_OCX_OBJ.SetReadOnly(false);
	     FAI_OCX_OBJ.ActiveDocument.ShowRevisions =false;
	     }
	    
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
        //禁止打开
        //FAI_OCX_OBJ.EnableFileCommand(1) = false;
        
    }
    else
    {
        //设置读写打开
        FAI_OCX_OBJ.SetReadOnly(false);
        //郭军刚 2007-07-26 修改
    	
	   
        //隐藏工具栏
        FAI_OCX_OBJ.Toolbars=true;
        //隐藏工具菜单
        FAI_OCX_OBJ.IsShowToolMenu=true;
        //隐藏菜单
        FAI_OCX_OBJ.Menubar=true;
        //保留痕迹
        if((document.all.zwtype.value!=".xls")&&(document.all.zwtype.value!=".xlsx"))
        {
           //显示痕迹
           FAI_OCX_OBJ.ActiveDocument.ShowRevisions =true;
           FAI_OCX_SetReviewMode(true);
	       FAI_OCX_EnableReviewBar(true);
	    }
        //允许拷贝
        FAI_OCX_OBJ.IsNoCopy=false;
	    FAI_OCX_OBJ.focus();
    }
    //打印控制    
    FAI_OCX_OBJ.EnableFileCommand(5) = true;

}

function FAI_OCX_SaveEditToServerDisk(CurrentKey)
{
    
	if(!FAI_OCX_bDocOpen)
	{
		alert("No document opened now.");
		return;
	}	
	FAI_OCX_filename = "88888";
	if ( (typeof(FAI_OCX_filename) == "undefined")||(!FAI_OCX_filename) || (strtrim(FAI_OCX_filename)==""))
	{
		alert("You must input a file name.");
		return;
	}
	var newwin,newdoc;
	try
	{
	 	if(!FAI_OCX_doFormOnSubmit())return; //we may do onsubmit first
		var retHTML = FAI_OCX_OBJ.SaveToURL
		(
			"app/uploadoffice.do,  
			"EDITFILE",	
			"", //other params seperrated by '&'. For example:myname=tanger&hisname=tom
			FAI_OCX_filename //filename
		); //this function returns dta from server

        alert("保存成功！");
	}
	catch(err){
 		alert("err:" + err.number + ":" + err.description);
	}
	finally{
	}
}





//add end
function FAI_OCX_SaveAsHTML()
{
	var newwin,newdoc;

	if(!FAI_OCX_bDocOpen)
	{
		alert("No document opened now.");
		return;
	}
	FAI_OCX_filename = document.all.item("htmlfile").value;
	if ( (typeof(FAI_OCX_filename) == "undefined")||(!FAI_OCX_filename) || (strtrim(FAI_OCX_filename)==""))
	{
		alert("You must input a html file name.");
		return;
	}	
	try
	{
		var retHTML = FAI_OCX_OBJ.PublishAsHTMLToURL
			(
				"uploadhtmls.aspx",
				"HTMLFILES", 
				"",
				FAI_OCX_filename
				);
		newwin = window.open("","_blank","left=200,top=200,width=400,height=300,status=0,toolbar=0,menubar=0,location=0,scrollbars=1,resizable=1",false);
		newdoc = newwin.document;
		newdoc.open();
		newdoc.write("<center><hr>"+retHTML+"<hr><input type=button VALUE='Close Window' onclick='window.close()'></center>");
		newdoc.close();	
		newwin.focus();
		if(window.opener)
		{
			window.opener.location.reload();
		}
	}
	catch(err){
		alert("err:" + err.number + ":" + err.description);
	}
	finally{
	}
}
//从本地增加印章文档指定位置
function AddSignFromLocal()
{

   if(FAI_OCX_bDocOpen)
   {
      FAI_OCX_OBJ.AddSignFromLocal(
	FAI_OCX_Username,//当前登陆用户
	"",//缺省文件
	true,//提示选择
	0,//left
	0,"",1,100,0)  //top
   }
}

function addSian()
{
   try{
   if(FAI_OCX_bDocOpen)
    {
        FAI_OCX_OBJ.AddSecSignFromLocal(document.all.FAI_OCX_Username.value,
        "",true,0,0,1,0,false,false,true,true,"");
    }
    }
    catch(Exception){}
}

//从URL增加印章文档指定位置
function AddSignFromURL(URL)
{
   if(FAI_OCX_bDocOpen)
   {
      FAI_OCX_OBJ.AddSignFromURL(
	FAI_OCX_Username,//当前登陆用户
	URL,//URL
	-50,//left
	-50,"",1,100,0)  //top
   }
}

//开始手写签名
function DoHandSign()
{
try{	
     if(FAI_OCX_bDocOpen)
       {	
         FAI_OCX_OBJ.AddSecHandSign
         (FAI_OCX_Username,0,0,1,2,false,false,true,true);
       }
    }
    catch(Exception){}
}
//检查签名结果
function DoCheckSign()
{
	if(FAI_OCX_bDocOpen)
	{		
	var ret = FAI_OCX_OBJ.DoCheckSign
	(
	/*可选参数 IsSilent 缺省为FAlSE，表示弹出验证对话框,否则，只是返回验证结果到返回值*/
	);//返回值，验证结果字符串
	//alert(ret);
	}	
}
function AddPictureFromLocal()
{
	if(FAI_OCX_bDocOpen)
	{	
    FAI_OCX_OBJ.AddPicFromLocal(
	"", //path 
	true,//prompt to select
	true,//is float
	0,//left
	0); //top
	};	
}

function AddPictureFromURL(URL)
{
	if(FAI_OCX_bDocOpen)
	{
    FAI_OCX_OBJ.AddPicFromURL(
	URL,//URL Note: URL must return Word supported picture types
	true,//is float
	0,//left
	0);//top
	};
}
function InsertDocFromURL(URL)
{
	if(FAI_OCX_bDocOpen)
	{
		FAI_OCX_OBJ.AddTemplateFromURL(URL);
	};
}


function DoHandDraw()
{
	if(FAI_OCX_bDocOpen)
	{	
	FAI_OCX_OBJ.DoHandDraw2(
	0,0,0);//top optional
	}
}

function FAI_OCX_AddDocHeader( strHeader )
{
   	var i,cNum = 30;
	var lineStr = "";
	try
	{
		for(i=0;i<cNum;i++) lineStr += "_"; 
		with(FAI_OCX_OBJ.ActiveDocument.Application)
		{
			Selection.HomeKey(6,0); // go home
			Selection.TypeText(strHeader);
			Selection.TypeParagraph(); 
			Selection.TypeText(lineStr); 
			Selection.TypeText("★");
			Selection.TypeText(lineStr);  
			Selection.TypeParagraph();
			Selection.HomeKey(6,1); 
			Selection.ParagraphFormat.Alignment = 1; 
			with(Selection.Font)
			{
				Name = "Arial";
				Size = 12;
				Bold = false;
				Italic = false;
				Underline = 0;
				UnderlineColor = 0;
				StrikeThrough = false;
				DoubleStrikeThrough = false;
				Outline = false;
				Emboss = false;
				Shadow = false;
				Hidden = false;
				SmallCaps = false;
				AllCaps = false;
				Color = 255;
				Engrave = false;
				Superscript = false;
				Subscript = false;
				Spacing = 0;
				Scaling = 100;
				Position = 0;
				Kerning = 0;
				Animation = 0;
				DisableCharacterSpaceGrid = false;
				EmphasisMark = 0;
			}
			Selection.MoveDown(5, 3, 0); 
		}
	}
	catch(err){
		//alert("err:" + err.number + ":" + err.description);
	}
	finally{
	}
}
function strtrim(value)
{
	return value.replace(/^\s+/,'').replace(/\s+$/,'');
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

function FAI_OCX_EnableReviewBar(boolvalue)
{
	FAI_OCX_OBJ.ActiveDocument.CommandBars("Reviewing").Enabled = boolvalue;
	FAI_OCX_OBJ.ActiveDocument.CommandBars("Track Changes").Enabled = boolvalue;
	FAI_OCX_OBJ.IsShowToolMenu = boolvalue;
}

function FAI_OCX_SetReviewMode(boolvalue)
{
	 
	FAI_OCX_OBJ.ActiveDocument.TrackRevisions = boolvalue;
}

function FAI_OCX_SetMarkModify(boolvalue)
{
	FAI_OCX_SetReviewMode(boolvalue);
	FAI_OCX_EnableReviewBar(!boolvalue);
}

function FAI_OCX_ShowRevisions(boolvalue)
{
	FAI_OCX_OBJ.ActiveDocument.ShowRevisions = boolvalue;
}

function FAI_OCX_PrintRevisions(boolvalue)
{
	FAI_OCX_OBJ.ActiveDocument.PrintRevisions = boolvalue;
}

function FAI_OCX_SetDocUser(cuser)
{
	with(FAI_OCX_OBJ.ActiveDocument.Application)
	{
		UserName = cuser;
		FAI_OCX_Username = cuser;
	}	
}

function FAI_OCX_ChgLayout()
{
 	try
	{
		FAI_OCX_OBJ.showdialog(5); 
	}
	catch(err){
		alert("err:" + err.number + ":" + err.description);
	}
	finally{
	}
}

function FAI_OCX_PrintDoc()
{
	try
	{
		FAI_OCX_OBJ.printout(true);
	}
	catch(err){
		alert("err:"  + err.number + ":" + err.description);
	}
	finally{
	}
}


function FAI_OCX_EnableFileNewMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(0) = boolvalue;
}

function FAI_OCX_EnableFileOpenMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(1) = boolvalue;
}

function FAI_OCX_EnableFileCloseMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(2) = boolvalue;
}

function FAI_OCX_EnableFileSaveMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(3) = boolvalue;
}

function FAI_OCX_EnableFileSaveAsMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(4) = boolvalue;
}

function FAI_OCX_EnableFilePrintMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(5) = boolvalue;
}

function FAI_OCX_EnableFilePrintPreviewMenu(boolvalue)
{
	FAI_OCX_OBJ.EnableFileCommand(6) = boolvalue;
}

function FAI_OCX_OnDocumentOpened(str, obj)
{
	FAI_OCX_bDocOpen = true;	
	FAI_OCX_SetDocUser(FAI_OCX_Username);
}

function FAI_OCX_OnDocumentClosed()
{
   FAI_OCX_bDocOpen = false;
}

function FAI_OCX_CircleSet()
{
FAI_OCX_OBJ1 = document.all.item("FAI_OCX");
// alert(FAI_OCX_OBJ1);
}