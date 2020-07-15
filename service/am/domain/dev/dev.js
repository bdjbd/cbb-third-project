function reloadTree(){
	var obj = window.parent;
	if(obj && obj.component){
		obj.component.location='/dev/component.do';
	}
}
function lock(obj,u){
	if(obj.className=="lock"){
		obj.className="unlock";
	}else{
		obj.className="lock";
	}
  document.all(u+".l").value=(obj.className=="lock")?"1":"0";
}
