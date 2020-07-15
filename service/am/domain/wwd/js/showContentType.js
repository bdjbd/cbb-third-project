function removechilds(){    
		var msg_type = document.getElementsByName("ws_auto_replay_rule.form.msg_type");
		if(!isEmpty(msg_type[0])){
			msg_type=msg_type[0].value;
        var vTable = document.getElementById("u_g_ws_auto_replay_rule.form");//取得table
	    var vTd = vTable.childNodes[4];
		vTd.childNodes[5].style.display="none";
		vTd.childNodes[6].style.display="none";
		vTd.childNodes[7].style.display="none";
		vTd.childNodes[8].style.display="none";
		vTd.childNodes[9].style.display="none";
		ShowContentType();
		}
	}
   
function ShowContentType(){

    var msg_type = document.getElementsByName("ws_auto_replay_rule.form.msg_type")[0].value;
    var vTable = document.getElementById("u_g_ws_auto_replay_rule.form");//取得table
	var vTd = vTable.childNodes[4];
    
	if(msg_type==1){
		//显示行对象
		vTd.childNodes[4].style.display="";
		//隐藏行对象
		vTd.childNodes[5].style.display="none";
		vTd.childNodes[6].style.display="none";
		vTd.childNodes[7].style.display="none";
		vTd.childNodes[8].style.display="none";
		vTd.childNodes[9].style.display="none";
		
	}else if(msg_type==2){
        vTd.childNodes[5].style.display="";
        //隐藏行对象
        vTd.childNodes[4].style.display="none";
		vTd.childNodes[6].style.display="none";
		vTd.childNodes[7].style.display="none";
		vTd.childNodes[8].style.display="none";
		vTd.childNodes[9].style.display="none";

	}else if(msg_type==6){
		//alert(msg_type);
        vTd.childNodes[6].style.display="";
		vTd.childNodes[7].style.display="";
		vTd.childNodes[8].style.display="";
		vTd.childNodes[9].style.display="";
        //隐藏行对象
        vTd.childNodes[4].style.display="none";
		vTd.childNodes[5].style.display="none";
	}
}


function validFile(event){
		var temp="";
		var obj = event.srcElement ? event.srcElement:event.target;
		if(obj.value!=null&&obj.value!="underfined"&&obj.value!=""){
			var hz=obj.value.substr(obj.value-4);
			//if(hz.indexOf("jpg")<1||hz.indexOf("JPG")<1){
			//	alert("只能上传JPG格式的文件");
			//}
		}
}

function initFileTypeValid(unitid){
	removechilds();
	trimEditName();
	var btn=document.getElementById(unitid+'.save');
	btn.onclick=function(){
		var imageCover=document.getElementById("ws_auto_replay_rule.form.imagecover.25.adda");
		if(!isEmpty(imageCover)){
			 imageCover=imageCover.firstChild.value;
			// if(!isEmpty(imageCover)&&!isJPG(imageCover)){
			//		alert("请检查文件格式，只允许上传JPG格式图片");
			//		return;
			//	}
			}
		
		var imagestr=document.getElementById('ws_auto_replay_rule.form.image.25.adda');
		if(!isEmpty(imagestr)){
			imagestr=imagestr.firstChild.value;
			//if(!isEmpty(imagestr)&&!isJPG(imagestr)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
		
		var pc=document.getElementById('ws_commodity_name.form.productscover.25.adda');
		if(!isEmpty(pc)){
			pc=pc.firstChild.value;
			//if(!isEmpty(pc)&&!isJPG(pc)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
	
		var pf1=document.getElementById('ws_commodity_name.form.productimage1.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
			//if(!isEmpty(pf1)&&!isJPG(pf1)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
		pf1=document.getElementById('ws_commodity_name.form.productimage2.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
			//if(!isEmpty(pf1)&&!isJPG(pf1)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
		pf1=document.getElementById('ws_commodity_name.form.productimage3.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
			//if(!isEmpty(pf1)&&!isJPG(pf1)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
		
		pf1=document.getElementById('ws_commodity_name.form.productimage4.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
			//if(!isEmpty(pf1)&&!isJPG(pf1)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
			//	return;
			//}
		}
		
		pf1=document.getElementById('ws_commodity_name.form.productimage5.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
			//if(!isEmpty(pf1)&&!isJPG(pf1)){
			//	alert("请检查文件格式，只允许上传JPG格式图片");
		//		return;
		//	}
		}
		pf1=document.getElementById('ws_commodity_name.form.productimage6.25.adda');
		if(!isEmpty(pf1)){
			pf1=pf1.firstChild.value;
		//	if(!isEmpty(pf1)&&!isJPG(pf1)){
		//		alert("请检查文件格式，只允许上传JPG格式图片");
		//		return;
		//	}
		}
		doActionWithAjaxValidation('/wwd/'+unitid+'/ajax.do','/wwd/'+unitid+'/save.do');
		}
}

function isEmpty(obj){            
	if(obj==null)return true;       
	if(obj=="")return true;         
	if(obj=="undefined")return true;
	if(obj=="null")return true      
	return false;                   
}
function isJPG(filNameStr){
		 var suffix=filNameStr.substr(filNameStr.length-4);
		 //if(filNameStr.indexOf("jpg")>0||filNameStr.indexOf("JPG")>0){
		//		return true;
		//	}else{
		//		return false;
		//	}
}

function trimComdityName(){
  var obj=document.getElementsByName("ws_commodity_name.form.comdity_class_id")[0];
  var comdityClassName = obj.options[obj.selectedIndex].text;
  var str = comdityClassName.replace(/\s/g,'');
  obj.options[obj.selectedIndex].text=str;
}

function trimEditName(){
  var obj=document.getElementsByName("ws_commodity_name.form1.comdity_class_id")[0];
  if(obj!=null){
    var comdityClassName = obj.options[obj.selectedIndex].text;
    var str = comdityClassName.replace(/\s/g,'');
    obj.options[obj.selectedIndex].text=str;
  }
}