function $fNavList(e){
	var node =$(e.target.parentNode);
	if(node.tagName=="LI" && e.target.href){
		var box = $fNavListBox(node);
		var ons = box.getElements('li[class=on]');
		if(ons && ons.length){
			for(var i=0;i<ons.length;i++){
				ons[i].removeClass("on")
			}
		}
		node.addClass("on");
	}
}
function $fNavListBox(node){
	var parent=$(node.parentNode);	
	while(parent.tagName!="DIV" && !parent.hasClass("nav-box") && parent.parentNode){
		parent = $(parent.parentNode);
	}
	return parent;
}