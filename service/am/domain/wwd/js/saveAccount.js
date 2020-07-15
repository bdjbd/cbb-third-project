var saveActionBtn;
function init(){
   var account_belong = document.getElementsByName("ws_public_accounts.form.account_belong")[0].value;
   if(account_belong==1){
      document.getElementsByName("ws_public_accounts.form.password")[0].value="******";
   }
	if(self.location.search.indexOf("m=s")>=-1){
	   saveActionBtn=document.getElementById("ws_public_accounts.form.save");
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
	var url="/wwd/ws_public_accounts.form/valid.do?token="+token+"&app_id="+appid+"&app_secret="+appSecret+"&is_valid="+isValid;
	doAjax(url);
}

function enableSaveBtn(){
	var saveBtn=document.getElementById("ws_public_accounts.form.save");
	if(saveBtn!=null&&saveBtn!="undefined"){
		saveBtn.onclick=function(){
			doSubmit('/wwd/ws_public_accounts.form/save.do');
			}
		alert("验证通过，请保存数据");
	}
}


var fsaflag=false;
function selectAccount(obj){
	if(!fsaflag){
		fsaflag=true;
		var wchat_account=obj.value;
		if(wchat_account!=null || wchat_account!=""){
			document.getElementsByName("ws_public_accounts.form.password")[0].value="";
			document.getElementsByName("ws_public_accounts.form.is_valid")[0].value="";
			document.getElementsByName("ws_public_accounts.form.app_id")[0].value="";
			document.getElementsByName("ws_public_accounts.form.app_secret")[0].value="";
			document.getElementsByName("ws_public_accounts.form.token")[0].value="";
			document.getElementsByName("ws_public_accounts.form.welcomeword")[0].value="";
		    document.getElementsByName("ws_public_accounts.form.explain")[0].value="";  
			var url="/wwd/ws_public_accounts.form/select.do?wchat_account="+wchat_account;
			doAjaxSubmit(url,"","wwd","ws_public_accounts.form");
		}
	}
    fsaflag = false;
}


function clearnPA(wchat_account){
	waccount=wchat_account.value;
	
	if(waccount==null||waccount==""){
		
		document.getElementsByName("ws_public_accounts.form.password")[0].value="";
		document.getElementsByName("ws_public_accounts.form.is_valid")[0].value="";
		document.getElementsByName("ws_public_accounts.form.app_id")[0].value="";
		document.getElementsByName("ws_public_accounts.form.app_secret")[0].value="";
		document.getElementsByName("ws_public_accounts.form.token")[0].value="";
		document.getElementsByName("ws_public_accounts.form.welcomeword")[0].value="";
		document.getElementsByName("ws_public_accounts.form.explain")[0].value=""; 
		var url="/wwd/ws_public_accounts.form/select.do?wchat_account="+waccount;
		doAjaxSubmit(url,"","wwd","ws_public_accounts.form");
	}
}

function setIsVaild(value){
	if(value==1){
		document.getElementById('isValid').value="是"; 
	}else if(value==3){
		document.getElementById('isValid').value="否"; 
	}
}
