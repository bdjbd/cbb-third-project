var myDialog;
function opendManualDist(url){
	myDialog = new MooDialog.Iframe(url,{
		title: '',
		'class': 'MooDialog myDialog'
	});
}