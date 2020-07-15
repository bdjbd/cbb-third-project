var Color={ALERT:'#ff0000',SELECTED:'#0000ff',TARGET:'#0000ff',OVER:'#000000'};
var deleteIcon={ENABLED:'/common/images/tool/remove.png',DISABLED:'/common/images/tool/remove_d.png'};
var changeIcon={ENABLED:'/domain/dev/t/change.png',DISABLED:'/domain/dev/t/change_d.png'};

var all;
var allid={};
var column;
var listener;
var xa;
var xs;

function init(){
	if(all)return;
	loadElements();
	for(var i=0;i<all.length;i++){configComponent(all[i]);}
	xa=new Action();
	layout();
	listener=new Listener();
	listener.addEvents();
}
function layout(){
	//var start=new Date;
	//var message="layout: ";
	if(xa){xa.unselectArea();}
	column=$fnv("unit.co");
	if(!_isInt(column) || (!isList && column<1)){column=4;$fn("unit.co").value=4;}
	var unit=new Unit();
  //message+=" unit:"+(new Date - start);
	$("_l_toolbar").innerHTML=unit.writeToolbar();
	$("_l_grid").innerHTML=unit.writeGrid();
	$("_l_hidden").innerHTML=unit.writeHidden();
	$("_l_invisible").innerHTML=unit.writeInvisible();
	xa.reset();
	//message+=" all:"+(new Date - start);
  //window.status=message;
}
//change c
function cc(){
	var id=$fTr(event.srcElement).id;
	var c=id.substring(3,id.length);
	xs.setComponent(c);
	xs.load();
	layout();
}
//add
var _suffix=1;
function getNewId(){
	var suffix=	_suffix++;
	while(allid['e'+suffix]){suffix=_suffix++;}
	return 'e'+suffix;
}
function add(c){
	if(!$fn("u.")) return;
	if(!c || c==null || c==""){
		var id=$fTr(event.srcElement).id;
		c=id.substring(3,id.length);
	}
	addElement(c);
	layout();
}
function addElement(c,id){
	if(!id || allid[id]){
		id=getNewId();
	}
	allid[id]=id;
	var name=(c=='0' && xs)?xs.name:id;
	var area;
	var group;
	var order=999;
	if(xs){
		area=xs.area;
		group= xs.group;
		order=parseInt(xs.order)-1;
	}else if(xa.targetArea){
		area=xa.targetArea.get('area');
		group= xa.targetArea.get('group');
	}else{
		area='g';
	}
	xa.unselectShadow();
	xa.unselectArea();
	doFormAdd('e');
	loadElements();
	xs=all[all.length-1];
	xs.setComponent(c);
	xs.setId(id);
	xs.setName(name);
	xs.setArea(area);
	xs.setGroup(group);
	xs.setOrder(order);
	xs.show(true);
}
function addControl(){
	var id=$fTr(event.srcElement).id;
	switch (id.substring(3,id.length)){
	case '1':addBackControl();break;
	case '2':addSaveControl();break;
	case '3':addNewControl();break;
	case '4':addDeleteControl();break;
	case '5':addAddControl();break;
	case '6':addRemoveControl();break;
	case '7':addClearControl();break;
	case '8':addQueryControl();break;
	case '9':addScanControl();break;
	case '10':addEditControl();break;
	case '31':addAjaxControl();break;
	case '51':addExcelControl();break;
	case '52':addPdfControl();break;
	case '71':addFlowControl('stop','暂存',false,true,'/adm/myflow.do');break;
	case '72':addFlowControl('next','提交',true,true,'/adm/myflow.do');break;
	case '73':addFlowControl('backward','驳回',true,false,'/adm/myflow.todo.do');break;
	case '74':addFlowControl('kill','中止',true,false,'/adm/myflow.todo.do');break;
	case '75':addFlowControl('forward','同意',true,false,'/adm/myflow.todo.do');break;
	}
	layout();
}
//load
function loadElements(){
	all=new Array();
	var idArray=$$('input[name=e.eid]');
	for(var i=0;i<idArray.length;i++){
		var e=new $fe(idArray[i]);
		all.push(e);
		allid[e.id]=e.id;
	}
}
//if layout,can't invoke this method!
function re(e){
	if(!e) e = getElement();
	e.load();
	var shadow = getShadow(e);
	shadow.className="e"+e.c+e.action+e.au;
	if(e.isInvalid()){
		shadow.style.color=Color.ALERT;
		shadow.title=e.message;
	}else{
		shadow.style.color='';
		shadow.title=e.name?e.name:"";
	}
	shadow.innerHTML= e.id;
}
