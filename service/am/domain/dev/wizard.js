function skip(obj,id){
	var checked = obj.checked;
	setSkip($fTr(obj),checked);
	if(id){
		setSkip($fTr(document.all(id)),checked);
	}
}
function setSkip(row,checked){
	var texts = $(row).getElements('input[type=text]');
	var necessary = row.getElementsByTagName("SPAN");
	for(var i=0;i<texts.length;i++){
		if(checked){
			texts[i].addClass("f2");
			texts[i].removeClass("d2");
			texts[i].disabled = false;
		}else{
			texts[i].addClass("d2");
			texts[i].removeClass("f2");
			texts[i].disabled = true;
		}
	}
	if(necessary){
		necessary[0].style.color=checked?"red":"black";
	}
}
function setIds1(){
	var suite = $fnv("wizard.suite");
	var table = $fnv("suite"+suite+".tablename").toLowerCase();
	var index = table.indexOf("|");
	if (index > 0) {
		table = table.substring(index + 1);
	}
	setAutoValue("suite"+suite+".groupid",table);
	setAutoValue("suite"+suite+".page1id",table);
	setAutoValue("suite"+suite+".queryunitid",table+".query");
	setAutoValue("suite"+suite+".listunitid",table+".list");
	setAutoValue("suite"+suite+".formunitid",table+".form");
}
function setIds2(){
	var suite = $fnv("wizard.suite");
	var father = $fnv("suite"+suite+".fathertablename").toLowerCase();
	var index = father.indexOf("|");
	if (index > 0) {
		father = father.substring(index + 1);
	}
	setAutoValue("suite"+suite+".groupid",father);
	setAutoValue("suite"+suite+".page1id",father);
	setAutoValue("suite"+suite+".queryunitid",father+".query");
	setAutoValue("suite"+suite+".listunitid",father+".list");
	setAutoValue("suite"+suite+".formunitid",father+".form");
	var son = $fnv("suite"+suite+".sontablename").toLowerCase();
	index = son.indexOf("|");
	if (index > 0) {
		son = son.substring(index + 1);
	}
	setAutoValue("suite"+suite+".page2id",son);
	setAutoValue("suite"+suite+".sonlistunitid",son+".list");
}
function setAutoValue(name,value){
	var obj = $fn(name);
	if(obj && obj.value==''){obj.value = value;}
}