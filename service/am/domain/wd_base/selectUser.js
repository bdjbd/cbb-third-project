function selectUser(){
	var SelRadio=$("u_u_auser.list.tc").getElements("input[name=_s_auser.list.tc]");
//获取当前页面选择的用户
	var userid=$('u_u_auser.list.tc').getElements('input[name=auser.list.tc.userid]');
	var username=$('u_u_auser.list.tc').getElements('input[name=auser.list.tc.username]');
	var index=document.getElementsByName('auser.list.tc.len')[0].value;
	for(var i=0;i<SelRadio.length;i++){
		if(SelRadio[i].value==1){
			window.opener.doListAdd('tc_circlemembers.list');
			window.opener.document.getElementsByName("tc_circlemembers.list.userid")[index].value=userid[i].value;
			window.opener.document.getElementsByName("tc_circlemembers.list.username")[index].value=username[i].value;
			index++;
		}
	}
	window.close();
}