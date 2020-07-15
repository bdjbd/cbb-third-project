window.addEvent('beforeunload', function(e) {
	var f = document.forms[0];
	if(f){
		$(f).setStyle('display','none');
		var b = $(document.body);
		b.setStyle('height',$(document).getSize().y-10);
		b.addClass('LOADING');
	}
});
