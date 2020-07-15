function setPath(path){
	document.all("input.path").value=path;
	doSubmit();
}
function onOver(){
	event.srcElement.className = "over";
}
function onOut(){
	event.srcElement.className = "active";
}
