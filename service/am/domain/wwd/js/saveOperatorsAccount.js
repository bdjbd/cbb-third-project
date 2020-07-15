var saveActionBtn;
function init(){
	if(self.location.search.indexOf("m=s")>=-1){
	   saveActionBtn=document.getElementById("ws_operators_account.form.save");
	   if(saveActionBtn!=null&&saveActionBtn!="undefined"){
		  saveActionBtn.onclick=function(){
	      alert('请先验证APPID和APPSECRET!');
		}
	  }
	}
}
function vaild(){
	var appid=document.getElementById("appid").value;
	var appSecret=document.getElementById("appSecret").value;
	var token=document.getElementById("token").value;
	var isValid=document.getElementById("isValid").value;
	var url="/wwd/ws_operators_account.form/valid.do?token="+token+"&app_id="+appid+"&app_secret="+appSecret+"&is_valid="+isValid;
	doAjax(url);
}

function enableSaveBtn(){
	var saveBtn=document.getElementById("ws_operators_account.form.save");
	if(saveBtn!=null&&saveBtn!="undefined"){
		saveBtn.onclick=function(){
			doSubmit('/wwd/ws_operators_account.form/save.do');
			}
		alert("验证通过，请保存数据");
	}
}

function setIsVaild(value){
	if(value==1){
		document.getElementById('isValid').value="是"; 
	}else if(value==3){
		document.getElementById('isValid').value="否"; 
	}
}