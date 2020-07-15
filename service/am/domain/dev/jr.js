//preview
function preview(){
	var domain = document.all("jr.domain").value;
	var unitId = document.all("jr.uid").value;
	if(unitId)	$('jr.preview').src='/'+domain+'/'+unitId+'.do';
}
