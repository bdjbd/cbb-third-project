//datamode
function setDataMode(mode){
	if(!mode) return ;
	$fShowRow($fTr($fn("enum.d")),mode=="2" || mode=="0");
	$fShowRow($fTr($fn("enum.de")),mode!="1");
}
function getDataMode(){
	var objs=$fns("enum.dm");
	for(var i=0;i<objs.length;i++){
		if(objs[i].checked) return objs[i].value;
	}
	return null;
}
setDataMode(getDataMode());
//overwrite
function _showCard(tab,uid){
	if($(tab).hasClass('C1')){
		return;
	}
	var tabs =$(tab).getParent('ul').getElements('a');
	for(var i=0;i<tabs.length;i++){
		if(tabs[i].id){$(tabs[i]).set('class',tabs[i].id==tab.id?'C1':'C0');}
	}
	var rows=_getCardWrapper(tab).getElement('table[id=u_g_'+uid+']').rows;
	for(var i=0;i<rows.length;i++){
		$fShowRow(rows[i],(!rows[i].id || rows[i].id==tab.id));
	}
  var initObj = $fn('card_init_'+uid);
  if(initObj){
    initObj.value=tab.id;
  }
  if(tab.id=='e'){setDataMode(getDataMode());}
}