var unitC=$fnv('unit.c');
var isList=('2'==unitC);
var currentDomain=$fnv('unit.domain');
//preview
function preview(){
	var publishMode=$fnv('u.pp');
	var unitId=$fnv('u.uid');
	var view=$('unit.preview');
	if(unitId && publishMode=='0'){
		view.src='/dev/unit.preview.do?domain='+currentDomain+'&uid='+unitId;
	}else if(unitId){
		view.src='/'+currentDomain+'/'+unitId+'.do?m=e';
	}
}
//action
function xn(){
	xs.load();
	layout();
}
function xr(){
	xs.load();
	layout();
}
function xc(){
	xs.load();
	layout();
}
//menu
function onAutoLabelClick(){
	xs.setAddLabel();
	layout();
}
function onIsActionClick(){
	xs.setIsAction();
	re(xs);
}
function onAccessControlClick(){
	xs.setAuth();
	re(xs);
}
function onRemoveClick(){
	xs.remove();
	xs=null;
	layout();
}
function addTextFieldElement(){
	add('1');
}
function addUnitElement(){
	add('80');
}
var emenu=new Tree('em','m');
_bc.action= addTextFieldElement;
_ac.action= addUnitElement;
emenu.add(_bc);
emenu.add(_cc);
emenu.add(_ac);
emenu.add(_pc);
emenu.add(new MenuSeparator());
var em6=new Tree('em6','自动标签',onAutoLabelClick);
var em7=new Tree('em7','需要授权',onAccessControlClick);
var em8=new Tree('em8','Action',onIsActionClick);
if(!isList) emenu.add(em6);
emenu.add(em7);
emenu.add(em8);
emenu.add(new MenuSeparator());
emenu.add(_bx);
emenu.add(_cx);
emenu.add(_ax);
var em9=new Tree('em9','删除',onRemoveClick,deleteIcon.ENABLED);
emenu.add(new MenuSeparator());
emenu.add(em9);
function showMenu(){
	var c=emenu.writeMenu();
	if(!isList){
		if(xs && xa.shadow.get('area')=='g' && xs.cs>0){
			setMenuSelected(c,em6,'',onAutoLabelClick,'0'!=xs.grid.all('e.al').value);
		}else{
			setMenuSelected(c,em6,MenuNaming.CSS_DISABLED,null,false);
		}
	}
	if(xs){
		setMenuSelected(c,em7,'',onAccessControlClick,'1'==xs.au);
		setMenuSelected(c,em8,'',onIsActionClick,'1'==xs.action);
		setMenuEnabled(c,em9,deleteIcon.ENABLED,onRemoveClick,true);
		setMenuEnabled(c,_bx,changeIcon.ENABLED,null,true);
		setMenuEnabled(c,_cx,changeIcon.ENABLED,null,true);
		setMenuEnabled(c,_ax,changeIcon.ENABLED,null,true);
	}else{
		setMenuSelected(c,em7,MenuNaming.CSS_DISABLED,null,false);
		setMenuSelected(c,em8,MenuNaming.CSS_DISABLED,null,false);
		setMenuEnabled(c,em9,deleteIcon.DISABLED,null,false);
		setMenuEnabled(c,_bx,changeIcon.DISABLED,null,false);
		setMenuEnabled(c,_cx,changeIcon.DISABLED,null,false);
		setMenuEnabled(c,_ax,changeIcon.DISABLED,null,false);
	}
}
//datamode
function setDataMode(mode){
	$fShowRow($fTr($fn('u.d')),(mode=='1' || mode=='0'));
}
function getDataMode(){
	var objs= $$('input[name=u.dm]');
	for(var i=0;i<objs.length;i++){
		if(objs[i].checked) return objs[i].value;
	}
	return null;
}
if(unitC!='3'){setDataMode(getDataMode());}
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
  if(tab.id=='b' && unitC!='3'){setDataMode(getDataMode());}
}
//page
function setPage(){
	var isPage = $fnv('u.pp')=='1';
	var u = $fUnit('u');
	u.getElement('ul li a[id=p]').setStyle('display',isPage?'inline-block':'none');
	u.getElement('input[name=u.au]').getParent().setStyle('display',isPage?'block':'none');
}
function onPageClick(){
	setTimeout(setPage, 100);
}
setPage();
//list card
function setListHeadMode(mode){
	$fShowRow($fTr($fn('u.v25')),mode=='2');
	$fShowRow($fTr($fn('u.i12')),mode=='1');
}
function getListHeadMode(){
	var objs= $$('input[name=u.lsh]');
	for(var i=0;i<objs.length;i++){
		if(objs[i].checked) return objs[i].value;
	}
	return null;
}
function setListPageSize(){
	var ls = $fnv('u.ls');
	if(!ls || ls>0){
		$fShowCell($('listpage'),true);
		setListNavOption();
	}else{
		$fShowCell($('listpage'),false);
		$fShowCell($('listpage.nav'),false);
	}
}
function setListNavOption(){var nav=$fnv('u.ln');$fShowCell($('listpage.nav'),!(nav==0 || nav==3));}
function initListCard(){setListHeadMode(getListHeadMode());setListPageSize();}
