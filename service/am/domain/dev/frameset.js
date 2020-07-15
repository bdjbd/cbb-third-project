//preview
function preview(){
	var domain = document.all("frameset.domain").value;
	var unitId = document.all("frameset.uid").value;
	if(unitId) $('frameset.preview').src='/'+domain+'/'+unitId+'.do';
}
