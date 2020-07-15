function changePassword(){
	var newwin = window.open('/app/changepassword.do', 'newwin', 'toolbar=no,location=no,directories=no,status=no,menubar=0,scrollbars=no,resizable=no,top=150,left=360,width=400,height=230');
	if(newwin != null) {
		if (newwin.opener == null) {
			newwin.opener = self;
		}
	}
	newwin.focus();
}
