function openapp() {
	var obj = $fns('login.clean');
	if(obj && obj[0] && obj[0].value=='1'){
		window.open("/app/index.do","","Status=1,resizable=1,location=no");
		window.opener='anyone';
		window.close();
	}else{
	  window.location="/app/index.do";
	}
}

$(document).addEvent('keyup',function(e){
	if(e.code==13 && $fnv('login.userid')!='' && $fnv('login.password')!=''){
		doSubmit('/app/login/login.do');
	}
});
$fn('login.userid').focus();

function loadCode(obj) {
	obj.src='/common/tools/validate_code.jsp?'+Math.random(); 
}

function repeatedLogin(sid) {
	if(confirm(Lang.RepeatedLogin)){
        new Request.HTML({url:'/adm/common/dropuser.do?sid='+sid}).post();
		openapp();
	}else{
        new Request.HTML({url:'/adm/common/dropuser.do'}).post();
	}
}

//记住用户名事件
function userChecked(){
	var username = document.getElementsByName("login.rememberuser")[0];
	if(username.value=='1'){
		selectPasswordNot();
		selectUserNot();
	}
	else if(username.value!='1')
	{
		selectUser();
	}
}

//记住密码事件
function passwordChecked(){
	var obj = document.getElementsByName("login.rememberpassword")[0];
	if(obj.value!='2'){
		selectPassword();
		selectUser();
	}
	else{
		selectPasswordNot();
	}
	
}
//选中'记住用户名'复选框
function selectUser() 
{ 
	var userCheckBox = document.getElementsByName("login.rememberuser")[0].parentNode.children;
	for (var i=0;i<userCheckBox.length;i++){ 
		if(userCheckBox[i].type=="checkbox"){
			userCheckBox[i].checked=true;
			var userobj=document.getElementsByName("login.rememberuser")[0];
			userobj.value="1";
		}
	}
} 
//选中'记住密码'复选框
function selectPassword() 
{ 
	var passwordCheckbox=document.getElementsByName("login.rememberpassword")[0].parentNode.children;
	for (var i=0;i<passwordCheckbox.length;i++){ 
		if (passwordCheckbox[i].type=="checkbox"){ 
			passwordCheckbox[i].checked=true;
			var pswobj=document.getElementsByName("login.rememberpassword")[0];
			pswobj.value="2";
		}
	}
} 


//未选中‘记住用户名’复选框
function selectUserNot() 
{ 
	var userCheckBox = document.getElementsByName("login.rememberuser")[0].parentNode.children;
	for (var i=0;i<userCheckBox.length;i++){ 
		if(userCheckBox[i].type=="checkbox"){
			userCheckBox[i].checked=false;
			var userobj=document.getElementsByName("login.rememberuser")[0];
			userobj.value="";
		}
	}
}


//未选中‘记住密码’复选框
function selectPasswordNot() 
{ 
	var passwordCheckbox=document.getElementsByName("login.rememberpassword")[0].parentNode.children;
	for (var i=0;i<passwordCheckbox.length;i++){ 
		if (passwordCheckbox[i].type=="checkbox"){ 
			passwordCheckbox[i].checked=false;
			var pswobj=document.getElementsByName("login.rememberpassword")[0];
			pswobj.value="";
		}
	}
}


//窗体加载事件
window.onload=function(){
	var userIdObj=document.getElementsByName("login.userid")[0];
	var userId1Obj=document.getElementsByName("login.userid1")[0];
	userIdObj.value=userId1Obj.value; 
	var passwordObj=document.getElementsByName("login.password")[0];
	var password1Obj=document.getElementsByName("login.password1")[0];
	passwordObj.value=password1Obj.value;
  
	var username = document.getElementsByName("login.rememberuser")[0];
	var obj = document.getElementsByName("login.rememberpassword")[0];
	if(userId1Obj.value!=""){
	  selectUser();
	  username.value="1";
	 }
	if((userId1Obj.value!="")&&(password1Obj.value!="")){
		selectUser();
		selectPassword();
	username.value="1";
	obj.value="2";
	}
}