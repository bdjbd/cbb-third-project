$$('input[type=text]').addEvent('blur',function(e){
	var o = e.target;
	if(o.value){
		o.value=o.value.trim();
	}
});