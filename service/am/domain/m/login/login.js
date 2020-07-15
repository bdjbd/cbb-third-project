function openapp() {
	window.location="/m/index.do";
}

$(document).addEvent('keyup',function(e){
	if(e.code==13 && $fnv('login.userid')!='' && $fnv('login.password')!=''){
		doSubmit('/m/login/login.do');
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

