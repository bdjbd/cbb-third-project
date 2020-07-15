function showSelected(){
	var select = $$('input[name=_s_p2p_badgetemplate.list]');
	var id = $$('input[name=p2p_badgetemplate.list.id.k]');
	var msg='';
	for(var i=0;i<select.length;i++){
		if(select[i].value=='1'){
			msg=id[i].value;
		}
	}
	if(msg){
		doAjax("/wwd/p2p_badgetemplate.list/stoprun.do?id="+msg)
	}else{
		alert('没有选择数据');
	}
}

function fillBadgeInfo(e){
	var tempId=e.options[e.selectedIndex].value;
	doAjax('/am_bdp/p2p_enterprisebadge.form/badgetemplateid.do?tempId='+tempId);
}

function changeBadgeValue(data){
	document.getElementById('badgeName').value=data.NAME;
	document.getElementById('badgeparams').value=data.BADGEPARAME;
	document.getElementById('ent_badge_code').value=data.ENT_BADGE_CODE;
}