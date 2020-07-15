function preview(url){
	$$('td.preview')[0].getElement('img').src=url;
}
//datamode
function setDataMode(mode){
	if(!mode) return ;
	$fShowRow($fTr($fn("cf.d")),mode=="1" || mode=="0");
	$fShowRow($fTr($fn("cf.qu")),mode=="1" || mode=="0");
}
setDataMode($fnv("cf.dm"));
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
  if(tab.id=='b'){setDataMode($fnv("cf.dm"));}
}