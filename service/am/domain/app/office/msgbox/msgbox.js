	var tableName="";
	var dataObj="";
	var ename="";
	var fileName="";
	/*
	本Js代码用于创建一个自定义的确认窗口,
	具体功能包括:自定义窗口标题,自定义窗口内容,是否显示取消按钮,焦点位置设定
	
	*/
	function get_width(){
		return (document.body.clientWidth+document.body.scrollLeft);
	}
	function get_height(){
		return (document.body.clientHeight+document.body.scrollTop);
	}
	function get_left(w){
		var bw=document.body.clientWidth;
		var bh=document.body.clientHeight;
		w=parseFloat(w);
		return (bw/2-w/2+document.body.scrollLeft)-100;
	}
	function get_top(h){
		var bw=document.body.clientWidth;
		var bh=document.body.clientHeight;
		h=parseFloat(h);
		return (bh/2-h/2+document.body.scrollTop)-60;
	}
	function create_mask(){//创建遮罩层的函数
		var mask=document.createElement("div");
		mask.id="mask";
		mask.style.position="absolute";
		mask.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=4,opacity=25)";//IE的不透明设置
		mask.style.opacity=0.9;//Mozilla的不透明设置
		mask.style.background="black";
		mask.style.top="0px";
		mask.style.left="0px";
		mask.style.width=get_width();
		mask.style.height=get_height();
		mask.style.zIndex=1000;
		document.body.appendChild(mask);
	}
	function create_msgbox(w,h,t){//创建弹出对话框的函数
		var box=document.createElement("div")	;
		box.id="msgbox";
		box.style.position="absolute";
		box.style.width=w;
		box.style.height=h;
		box.style.overflow="visible";
		box.innerHTML=t;
		box.style.zIndex=9999999;
		document.body.appendChild(box);
		re_pos();
	}
	function re_mask(){
		/*
		更改遮罩层的大小,确保在滚动以及窗口大小改变时还可以覆盖所有的内容
		*/
		var mask=document.getElementById("mask")	;
		if(null==mask)return;
		mask.style.width=get_width()+"px";
		mask.style.height=get_height()+"px";
	}
	function re_pos(){
		/*
		更改弹出对话框层的位置,确保在滚动以及窗口大小改变时一直保持在网页的最中间
		*/
		var box=document.getElementById("msgbox");
		if(null!=box){
			var w=box.style.width;
			var h=box.style.height;
			//box.style.left=get_left(w)+"px";
			//box.style.top=get_top(h)+"px";
			box.style.top=(window.screen.height-200)/2-160+"px";
			box.style.left=(window.screen.width-355)/2-200+"px";
			//alert("box.style.top="+box.style.top);
			//alert("box.style.left="+box.style.left);
		}
	}
	
	function clickFun(obj){
		/*
		清除遮罩层以及弹出的对话框
		*/
		var mask=document.getElementById("mask");
		var msgbox=document.getElementById("msgbox");
		var dataid=document.getElementsByName(dataObj)[0].value;
		if(null==mask&&null==msgbox)return;
		if(obj.value=="打  开"){
			//alert("/app/editoffice.do?filepath="+tableName+"/"+dataid+"/"+ename+"/"+fileName);
			var fp=encodeURI(tableName+"/"+dataid+"/"+ename+"/"+fileName);
			//alert(fp);
			openWindow("/app/editoffice.do?filepath="+fp);
		}
		if(obj.value=="下  载"){
			//alert("/adm/common/url_download.do?url=/files/"+tableName+"/"+dataid+"/"+ename+"/"+fileName);
			var fp=encodeURI(fileName);
			doDownload("/adm/common/url_download.do?url=/files/"+tableName+"/"+dataid+"/"+ename+"/"+fp);
		}
		document.body.removeChild(mask);
		document.body.removeChild(msgbox);
	}

	function clickFun1(obj){
		/*
		清除遮罩层以及弹出的对话框
		*/
		var mask=document.getElementById("mask");
		var msgbox=document.getElementById("msgbox");
		var dataid=document.getElementsByName(dataObj)[0].value;
		if(null==mask&&null==msgbox)return;
		if(obj.value=="是"){
			FAI_OCX_SaveEditToServerDisk(); 
			window.close();
		}
		else if(obj.value=="否"){
			window.close();
		}
		else if(obj.value=="取消"){
			
		}
		document.body.removeChild(mask);
		document.body.removeChild(msgbox);
	}

	function msgbox(title,text,tName,dObj,en,fName){
		tableName=tName;
		dataObj=dObj;
		ename=en;
		fileName=fName;
		create_mask();//创建遮罩层
		var temp="<table width=\"355\" height=\"82\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font:14px Verdana, Geneva, Arial, Helvetica, sans-serif\">";
		temp+="<tr><td background=\"/domain/app/office/msgbox/alert_01.gif\" width=\"355\" height=\"22\" style=\"padding-left:8px;padding-top:2px;font-weight: bold;color:white;\">"+title+"</td></tr>";
		temp+="<tr><td background=\"/domain/app/office/msgbox/alert_02.gif\" width=\"355\" height=\"40\" style=\"padding-left:6px;padding-right:2px;padding-bottom:10px;\">&nbsp;<img src=\"/domain/app/office/msgbox/alert_mark.gif\">&nbsp;"+text+"</td>";
		temp+="</tr><tr align=\"center\"><td width=\"355\" height=\"50\" align=\"center\" background=\"/domain/app/office/msgbox/alert_03.gif\">";
		temp+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp";
		temp+="<input name=\"msgconfirmb\" type=\"button\" id=\"msgconfirmb\" value=\"打  开\" onclick=\"clickFun(this);\">";
		temp+="&nbsp;&nbsp;<input name=\"msgconfirmb\" type=\"button\" id=\"msgconfirmb\" value=\"下  载\" onclick=\"clickFun(this);\">";
		temp+="&nbsp;&nbsp;<input name=\"msgcancelb\" type=\"button\" id=\"msgcancelb\" value=\"取  消\" onclick=\"clickFun(this);\"></td>";
		temp+="</tr><tr><td background=\"/domain/app/office/msgbox/alert_04.gif\" width=\"355\" height=\"8\"></td></tr></table>";
		create_msgbox(355,200,temp);
		if(focus==0||focus=="0"||null==focus){document.getElementById("msgconfirmb").focus();}
		else if(focus==1||focus=="1"){document.getElementById("msgcancelb").focus();}		
	}

	function msgbox1(title,text){
		create_mask();
		var temp="<table width=\"355\" height=\"127\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font:14px Verdana, Geneva, Arial, Helvetica, sans-serif\">";
		temp+="<tr><td background=\"/domain/app/office/msgbox/alert_01.gif\" width=\"355\" height=\"22\" style=\"padding-left:8px;padding-top:2px;font-weight: bold;color:white;\">"+title+"</td></tr>";
		temp+="<tr><td background=\"/domain/app/office/msgbox/alert_02.gif\" width=\"355\" height=\"75\" style=\"padding-left:6px;padding-right:2px;padding-bottom:10px;\">&nbsp;<img src=\"/domain/app/office/msgbox/alert_mark.gif\">&nbsp;"+text+"</td>";
		temp+="</tr><tr align=\"center\"><td width=\"355\" height=\"22\" align=\"center\" background=\"/domain/app/office/msgbox/alert_03.gif\">";
		temp+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp";
		//temp+="<input name=\"msgconfirmb\" type=\"button\" id=\"msgconfirmb\" value=\"是\" onclick=\"clickFun1(this);\">";
		//temp+="<input name=\"msgconfirmb\" type=\"button\" id=\"msgconfirmb\" value=\"否\" onclick=\"clickFun1(this);\">";
		temp+="&nbsp;&nbsp;<input name=\"msgcancelb\" type=\"button\" id=\"msgcancelb\" value=\"取消\" onclick=\"clickFun1(this);\"></td>";
		temp+="</tr><tr><td background=\"/domain/app/office/msgbox/alert_04.gif\" width=\"355\" height=\"8\"></td></tr></table>";
		create_msgbox(355,200,temp);
		if(focus==0||focus=="0"||null==focus){document.getElementById("msgconfirmb").focus();}
		else if(focus==1||focus=="1"){document.getElementById("msgcancelb").focus();}	
	}
	function re_show(){
		/*
		重新显示遮罩层以及弹出窗口元素
		*/
		re_pos();
		re_mask();	
	}
	function load_func(){
		/*
		加载函数,覆盖window的onresize和onscroll函数
		*/
		window.onresize=re_show;
		window.onscroll=re_show;	
	}