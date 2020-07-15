function syncMember(memberCode){
	doAjax("/wwd/ws_member1.list/syncmember.do?membercode="+memberCode);
	}

function registerMember(orgid){
	alert(orgid);
	//window.open();
	window.open ('/domain/p2p/mnq.html#register','newwindow', 'height=800, width=400, top=0, left=0, toolbar=no, menubar=no, resizable=no,location=no, status=no')
}