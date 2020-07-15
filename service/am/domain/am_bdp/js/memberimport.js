function memberimport(url, uid) {
	var table = $fGrid(uid);
	var action = false;
	var fileCtl = document.getElementsByName("am_member_list.importfile")[0];
	fileCtl.click();
	var filePath = fileCtl.value;
	action = true;

	if (action) {
		document.forms[0].action = url;
		document.forms[0].submit();
	}
}