var submitted=false;
function doSubmit(url){if(submitted){alert('已提交，请等待系统处理。');return;}if(url){document.forms[0].action=url;}submitted=true;if($fn('encrypted')){encrypt();}document.forms[0].submit();}
function doQueryWithCheckBoxSet(obj,url){$f53($(obj).getParent('span'));doSubmit(url);}
function doConfirm(url,msg){if(msg){if(confirm(msg)){doSubmit(url);}}else if(confirm(Lang.Confirm)){doSubmit(url);}}
function doLink(url){if(url){window.location=url;}}
function doLinkConfirm(url,msg){if(msg){if(confirm(msg)){doLink(url);}}else if(confirm(Lang.Confirm)){doLink(url);}}
function doAjax(url,id,domain,uid,index){$fAjax(true,null,url,id,domain,uid,index);}
function doAjaxSubmit(url,id,domain,uid,index){$fAjax(true,document.forms[0],url,id,domain,uid,index);}
function doSyncAjax(url,id,domain,uid,index){$fAjax(false,null,url,id,domain,uid,index);}
function doSyncAjaxSubmit(url,id,domain,uid,index){$fAjax(false,document.forms[0],url,id,domain,uid,index);}
function doBack(){doLink($fnv('backurl'));}
function doDownload(url){if(!url){alert('please set download url');}var form=document.forms[0];var oldUrl=form.action;window.removeEvents('beforeunload');form.action=url;form.submit();form.action=oldUrl;}
function doActionMsgConfirm(msg){if(confirm(msg)){doMsgYes();}else{doMsgNo();}}
//把列表删除
function doListDelete(url,uid,msg){
	var table=$fGrid(uid);//获取列表单元
	if(!table){alert('[doListDelete] invalid unitId : '+uid);return;}
	var action=false;
	var _s =table.getElements('input[name=_s_'+uid+']');//获取行隐藏域（存储选中状态）
	for(var i=0;i<_s.length;i++){if(_s[i].value=='1'){action=true;break;}}//判断是否有选中行
	if (!action){alert(Lang.NoChoiceList);} //为选择数据行进行提示
	else if (action && confirm(msg?msg:Lang.ConfirmDelete)){_doRemoveSelected(uid,false);doSubmit(url+(url.indexOf('?')>=0?'&':'?')+'_delete=1');}
}
function doFormDelete(url,uid){
	var table=$fGrid(uid);
	if(!table){alert('[doFormDelete] invalid unitId : '+uid);return;}
	var _o=table.getElements('input[name='+uid+'.]');
	if (_o && _o.length==1 && confirm(Lang.ConfirmDelete)){
		_o[0].value=3;
		document.forms[0].action=url;
		doSubmit();
	}
}
function doListAdd(uid){
	var insertRow=$('u_i_'+uid);
	if(insertRow){
		var tBody=$fGrid(uid).getElement("tbody");
    var rows = new Element('table',{'html': $f46n(restoreTags(insertRow.value))}).getElements('tr[id=temp]');
	  for(var i=0;i<rows.length;i++){
     tBody.adopt(rows[i]);
    }
		var grid=new Grid(uid);
		grid.setId();
		//grid.setIndex();
		$fResetCheck(uid);
	}
}
function doFormAdd(uid){
	var insertRow=$('u_i_'+uid);
	if(insertRow){$('u_w_'+uid).adopt(new Element('span',{'html': $f46n(restoreTags(insertRow.value))}).getChildren()[0]);}
}
function doRemove(uid){_doRemoveSelected(uid,true);}
function select2file(url){
	var unitId = url.substring(1,url.length);
	unitId = unitId.substring(unitId.indexOf('/')+1);
	unitId = unitId.substring(0,unitId.indexOf('/'));

	var selected = false;
	var _s =$$('input[name=_s_'+unitId+']');
	for(var i=0;i<_s.length;i++){
		if(_s[i].value=='1'){
			selected=true;break;
		}
	}

	if(selected){
		doDownload(url);
	}else{
		alert('没有选择数据');
	}
}
/* selectors */
function $fUnit(uid){return $('u_u_'+uid);}
function $fGrid(uid){return $('u_g_'+uid);}
/* system */
var $fSwitchIcon={
	Minus:'/common/images/unit/minus.png',
	Plus:'/common/images/unit/plus.png'
};
function $fSwitchHidden(uid){
	var obj=$fUnit(uid).getElement('img.SW');
	$fSwitch(obj,uid);
}
function $fSwitch(obj,uid){
	var show ;
	if(obj.src.indexOf($fSwitchIcon.Minus)>=0){
		obj.src =$fSwitchIcon.Plus;
		show='none';
	}else{
		obj.src =$fSwitchIcon.Minus;
		show='block';
	}
	var nodes=$fUnit(uid).getElements('div');
	for(var i=1;i<nodes.length;i++){
		nodes[i].setStyle('display',show);
	}
}
function $fLogin(url){$fTopWindow().location=url;}
function $fSelectAll(uid,obj){
	var _s=$$('input[name=_s_'+uid+']');
	for(var i=0;i<_s.length;i++){if(_s[i].getParent('tr').getStyle('display')=='none'){continue;}var box = _s[i].parentNode.childNodes[1];if(!box.disabled){_s[i].value=(obj.checked?'1':'0');box.checked=obj.checked;}}
}
function $fResetCheck(uid){
	var obj=$fGrid(uid).getElement('input[id=_sa_]');
	if(obj){var grid=new Grid(uid);obj.checked=grid.allSelected() && !grid.allInvisible();}
}
function _doRemoveSelected(uid,makeInvisible){
	var table=$fGrid(uid);
	var _s=table.getElements('input[name=_s_'+uid+']');
	var _o=table.getElements('input[name='+uid+'.]');
	if(_s.length==0 || _s.length!=_o.length){
		return;
	}
	var grid=new Grid(uid);
	for(var i=0;i<_s.length;i++){
		grid.groups[i].doRemove(_s[i],_o[i],makeInvisible);
	}
	grid=new Grid(uid);
	//grid.setIndex();
	$fResetCheck(uid);
}
function $fCard(uid,gid){
	_showCard($('u_c_'+uid).getElement('a[id='+gid+']'),uid);
}
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
}
function _showGroup(groupId){var grid=_getGridWithoutId();_showGroupWithGrid(grid,groupId);}
function _showGroupWithGrid(grid,groupId){var group=new Group(grid.rows,groupId);group.show();}
/* nav */
function _go(obj,currentPageNumber,maxPageNumber){
	var fields=$(obj).getParent('div').getElements('input');
	var pageNumber=fields[1].value;
	if(!_isInt(pageNumber) || parseInt(pageNumber)<1 || parseInt(pageNumber)>maxPageNumber || parseInt(pageNumber)==currentPageNumber){
		fields[1].value='';
		return ;
	}else{
		fields[0].value=pageNumber;
		doSubmit();
	}
}
function _to(obj,pageNumber){$(obj).getParent('div').getElement('input').value=pageNumber;doSubmit();}
/* Grid */
function Grid(uid){
	this.id=uid;
	this.index=1;
	this.groups=new Array();
	var rows=$fGrid(uid).getElements('tr');
	if(!rows){return;}
	var lastRowId='x';
	var rowId;
	for(var i=0;i<rows.length ;i++){
		rowId=rows[i].id;
		if(rowId && rowId!=lastRowId){this.groups[this.groups.length]=new Group(rows,rowId,this);}
		lastRowId=rowId;
	}
}
Grid.prototype.getIndex=function(){return this.index++;}
Grid.prototype.setId=function(){for(var i=0;i<this.groups.length ;i++){ this.groups[i].setId(i);}}
Grid.prototype.setIndex=function(){for(var i=0;i<this.groups.length ;i++){ this.groups[i].setIndex();}}
Grid.prototype.allSelected=function(){
	for(var i=0;i<this.groups.length ;i++){if(!this.groups[i].selected()){return false;}}
	return true;
}
Grid.prototype.allInvisible=function(){
	for(var i=0;i<this.groups.length ;i++){if(!this.groups[i].invisible()){return false;}}
	return true;
}
function Group(rows,rowId,parent){
	this.id=rowId;
	this.parent=parent;
	this.rows= new Array();
	for(var i=0;i<rows.length ;i++){
		var row=rows[i];
		if(row.id==rowId){this.rows.push(row);}
	}
}
Group.prototype.setCss=function(css){for(var i=0;i<this.rows.length ;i++){this.rows[i].className=css;}}
Group.prototype.setId=function(rowId){for(var i=0;i<this.rows.length ;i++){ this.rows[i].id=rowId;}}
Group.prototype.setIndex=function(){
	var indexCell=this.rows[0].getElement('td[id=_i_]');
	if(indexCell && indexCell.getParent().getStyle('display')!='none'){indexCell.innerHTML=this.parent.getIndex();}
}
Group.prototype.doRemove=function(select,o,makeInvisible){
	if(select.value=='1'){
		if(o.value=='2' || o.value=='0'){
			o.value='3';
			if(makeInvisible){this.show(false);}
		}else if(o.value=='1'){
			this.remove();
		}
	}
}
Group.prototype.show=function(){
	if(this.rows.length>0){
		var show=this.rows[0].style.display=='none';
		for(var i=0;i<this.rows.length ;i++){$fShowRow(this.rows[i],show);}
	}
}
Group.prototype.remove=function(){for(var i=0;i<this.rows.length ;i++){this.rows[i].destroy();}}
Group.prototype.selected=function(){var s=this.rows[0].getElement('input[name=_s_'+this.parent.id+']');return s?s.parentNode.childNodes[1].checked:true;}
Group.prototype.invisible=function(){return this.rows[0].style.display=='none';}
Group.prototype.getIndex=function(cell){
	for(var i=0;i<this.rows.length ;i++){
		var cells=this.rows[i].cells;
		for(var j=0;j<cells.length ;j++){
			if(cells[j]==cell){return new Array(i,j);}
		}
	}
	return null;
}
Group.prototype.getValue=function(index,name){
	var cell=this.rows[index[0]].cells[index[1]];
	var obj=cell.all(name);
	return obj?obj.value:cell.innerHTML;
}
function _getGridWithoutId(obj){
	var p=obj?obj.parentNode:event.srcElement.parentNode;	
	while((p.tagName!='TABLE' || !p.id || p.id.indexOf('u_g_')!=0) && p.parentNode){p=p.parentNode;}
	return p;
}
function _getCardWrapper(obj){return $(obj).getParent('div[id^=u_c_]');}
/* components */
function $f25a(area,id){$(area).adopt(new Element('div',{'html': $(id).value}));}
function $f25d(src,area,name,fileName){$(area).adopt(new Element('input',{'type':'hidden','name':name,'value':fileName}));$(src).getParent().destroy();}
function $f25o(src){var obj=$(src).getParent('span');var p=obj.getPrevious();if(p){obj.inject(p,'before');}}
function $f41(src){
	var obj =$(src).getPrevious();
	var items=$(src).getElements('option');
	var value ='';
	for(var i=0;i<items.length ;i++){if(items[i].selected){value += ','+items[i].value;}}
	obj.value=value.length>0?value.substring(1,value.length):'';
}
var _46n=0;
function $f46n(html){_46n++;return html.replace(/\$46n\$/g,_46n.toString());}
function $f46(src){var objs=$(src).getElements('input');for(var i=1;i<objs.length ;i++){if(objs[i].checked){objs[0].value=objs[i].value;break;}}return objs[0];}
function $f48(src){var obj=$f46(src);objs=$$('input[name='+obj.name+']');for(var i=0;i<objs.length ;i++){if(objs[i]!=obj){objs[i].value='';}}}
function $f52(src){var objs=src.childNodes;objs[0].value=objs[1].checked?'1':'0';}
function $f53(src){var objs=$(src).getElements('input');var value ='';for(var i=1;i<objs.length ;i++){if(objs[i].checked){value += ','+objs[i].value;}}objs[0].value=value.length>0?value.substring(1,value.length):'';}
/* Validator */
function _isInt(value){var i=parseInt(value);return !isNaN(i) && i.toString().length==value.length;} 
function isOverLength(value){if(value){return encodeURI(value).length>126;}return false;} 
/* util */
function getSelectedCount(unitId){return $$('input[name=_s_'+unitId+'][value=1]').length;}
function $fAjax(async,data,url,id,domain,uid,index){
  url+=url.indexOf('?')>0?'&ajax=1':'?ajax=1';
	if(id && domain && uid){url+='&ajaxd='+domain+'&ajaxu='+uid;}
	var request;
	if(id){
		var obj=index>=0?$$('[id='+id+']')[index]:$(id);
		request=new Request.HTML({url:encodeURI(url),async:async,update:obj});
	}else{
		request=new Request.HTML({url:encodeURI(url),async:async});
	}
	data?request.post(data):request.post();
}
function $fShowRow(obj,show){obj.style.display=show?(Browser.firefox||Browser.name=='chrome'?'table-row':'block'):'none';}
function $fShowCell(obj,show){obj.style.display=show?(Browser.firefox||Browser.name=='chrome'?'table-cell':'block'):'none';}
function $fEvent(obj,type,fn){obj.removeEvents(type);if(fn){obj.addEvent(type,fn);}}
function $fX(target,obj,offset){return $fXY(true,target,obj,offset);}
function $fY(target,obj,offset){return $fXY(false,target,obj,offset);}
function $fXY(x,target,obj,offset){
	var tp=target.getPosition();
	var ts=target.getSize();
	var os=obj.getSize();
	var d=$(document);
	var dsc=d.getScroll();
	var dsi=d.getSize();
	var scroll=x?dsc.x:dsc.y;
	var xy=x?((dsi.x+scroll<tp.x+ts.x+os.x)?(tp.x-os.x-offset):(tp.x+ts.x+offset)):((dsi.y+scroll<tp.y+ts.y+os.y)?(tp.y-os.y-offset):(tp.y+ts.y+offset));
	return xy-scroll<0?scroll:xy;
}
function $fPrint(){var bar=$('PrintBar');if(bar){bar.style.display='none';window.setTimeout('window.print();',1);window.setTimeout("$('PrintBar').style.display='block';",1);}else{window.print();}}
function $fns(name,p){return p?$(p).getElements('[name='+name+']'):$$('[name='+name+']');}
function $fn(name,p){return $fns(name,p)[0];}
function $fnv(name,p){return $fn(name,p).value;}
function $fTopWindow(){var obj=window;var i=0;while(obj.parent && i<5){obj=obj.parent;i++;}return obj;}
/* find first parent */
function $fDiv(obj){return $fObj(obj,'DIV');}
function $fSpan(obj){return $fObj(obj,'SPAN');}
function $fTd(obj){return $fObj(obj,'TD');}
function $fTr(obj){return $fObj(obj,'TR');}
function $fTable(obj){return $fObj(obj,'TABLE');}
function $fObj(obj,tagName){
	if(obj.tagName==tagName){return obj;}
	var p=obj.parentNode;
	while(p.tagName!=tagName && p.parentNode){p=p.parentNode;}
	return p.tagName==tagName?p:null;
}
function selectUser(unitId,valueId,nameId){selectX('selectuser',unitId,valueId,nameId);}
function selectUsers(unitId,valueId,nameId){selectX('selectusers',unitId,valueId,nameId);}
function selectOrg(unitId,valueId,nameId){selectX('selectorg',unitId,valueId,nameId);}
function selectOrgs(unitId,valueId,nameId){selectX('selectorgs',unitId,valueId,nameId);}
function selectX(x,unitId,valueId,nameId){
	var iWidth=500;                         //弹出窗口的宽度;
	var iHeight=450;                        //弹出窗口的高度;
	//window.screen.height获得屏幕的高，window.screen.width获得屏幕的宽
	var iTop = (window.screen.height-30-iHeight)/2;       //获得窗口的垂直位置;
	var iLeft = (window.screen.width-10-iWidth)/2;        //获得窗口的水平位置;
	window.open('/app/'+x+'.do?clear=app.'+x+'&uid='+unitId+'&vid='+valueId+'&nid='+nameId,
	'select',
	'height='+iHeight+',innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',location=no,scrollbars=yes,resizable=1');
}
function replaceTags(html){html=html.replace(/\</g,'&lt;');return html.replace(/\>/g,'&gt;');}
function restoreTags(html){html=html.replace(/&lt;/g,'<');return html.replace(/&gt;/g,'>');}
/* list-mark (todo: using tr class is faster?)*/
var _lastOverRowId ;
var _lastOverRowClassName ;
var _lastSelectedRowId ;
var _lastSelectedRowClassName ;
//on row over
function $fRO(event){
	var row=$fTr(event.srcElement);
	if(!_isValidRow(row) || row.id==_lastSelectedRowId){return;}
	_lastOverRowId=row.id;
	_lastOverRowClassName=row.className;
	var group=new Group($fTable(row).rows,row.id);
	group.setCss('RO');
}
//on row out
function $fRX(event){
	var row=$fTr(event.srcElement);
	if(!_isValidRow(row) || !_lastOverRowId || _lastOverRowId ==_lastSelectedRowId){return;}
	var group=new Group($fTable(row).rows,_lastOverRowId);
	group.setCss(_lastOverRowClassName);
}
//on row click
function $fRC(event){
	var row=Browser.firefox?event.target.getParent('tr'):$fTr(event.srcElement);
	if(!_isValidRow(row) || row.id==_lastSelectedRowId){return;}
	var rows=$fTable(row).rows;
	if(_lastSelectedRowId){
		var group=new Group(rows,_lastSelectedRowId);
		group.setCss(_lastSelectedRowClassName);
	}
	_lastSelectedRowId=row.id;
	//_lastOverRowClassName will be used while move mark enabled
	_lastSelectedRowClassName =_lastOverRowClassName || row.className;
	var group=new Group(rows,row.id);
	group.setCss('RS');
}
function _isValidRow(row){return  row.id && row.id!='x';}
/* list-sort */
var $fSortIcon={None:'/common/images/list/s0.png',Up:'/common/images/list/s1.png',Down:'/common/images/list/s2.png'};
function $fSort(cell,name,type){
	var img=cell.getElementsByTagName('IMG')[0];
	//set img and desc
	var desc =false;
	if(img.src.indexOf($fSortIcon.None)>0){
		img.src=$fSortIcon.Up;
	}else if(img.src.indexOf($fSortIcon.Up)>0){
		img.src=$fSortIcon.Down;
		desc=true;
	}else if(img.src.indexOf($fSortIcon.Down)>0){
		img.src=$fSortIcon.Up;
	}	
	//set index
	var gridTable=$fTable(cell);
	var uid=gridTable.id.substring(4,gridTable.id.length);
	var head=new Group(gridTable.rows,'x');
	var index=head.getIndex(cell);
	//sorting
	var grid=new Grid(uid);
	var groups=grid.groups;
	groups.sort(_compareGroup(name,type,desc,index));
	//set index
	grid.setIndex();
	//updating
	var tBody=gridTable.tBodies[0];
	for(var i=0; i < groups.length; i++){
		var rows=groups[i].rows;
		for(var j=0; j < rows.length; j++){tBody.appendChild(rows[j]);}
	}
}
function _compareGroup(name,type,desc, index){
	var formatter=String;	
	if (type=='1'){
		formatter=Number;
	}else if (type=='3'){
		formatter=function(s){return s.toUpperCase();};
	}else if (type=='4'){
		formatter=function(s){return Date.parse(s.replace(/\-/g, '/'));};
	}
	return function(group1, group2){
		var value1=formatter(_getCellValue(group1.rows[index[0]].cells[index[1]],name));
		var value2=formatter(_getCellValue(group2.rows[index[0]].cells[index[1]],name));
		if(value1<value2){
			return desc ? 1 : -1;
		}else if(value1>value2){
			return desc ? -1 : 1;
		}else{
			return 0;
		}
	};
}
function _getCellValue(cell,name){
	var obj=cell.all(name);
	return obj?obj.value:cell.innerText;
}
/*42*/
function $fLink(obj,link){
	var index=link.indexOf('.');
	var domain=link.substring(0,index);
	var id=link.substring(index+1,link.length);
	var objIndex= -1;
	var nextObjs=$$('[name='+id+']');
	if(nextObjs[0].getParent('table')==$$('select[name='+obj.name+']')[0].getParent('table')){
		objIndex=$fLinkIndex(obj);
	}
	for(var i=0;i<nextObjs.length;i++){
		if(objIndex>=0 && i!=objIndex){
			continue;
		}
		var url='/adm/common/link.do?src='+obj.name+'&value='+obj.value+'&domain='+domain+'&id='+id+'&index='+i;
		doSyncAjaxSubmit(url,id,null,null,i);
		var nextObj=$$('[name='+id+']')[i];
		var html=nextObj.getParent().get('html');
		index=html.indexOf('$fLink(');
		if(index>0){
			html=html.substring(index+7);
			html=html.substring(html.indexOf('\'')+1);
			link=html.substring(0,html.indexOf('\''));
			$fLink(nextObj,link);
		}
	}
}
function $fLinkIndex(obj){
	var objs=$$('select[name='+obj.name+']');
	if(objs){for(var i=0;i<objs.length;i++){if(objs[i]==obj) return i;}}
	return 0;
}
/*43*/
var $f43x;
function $f43(obj,match,height){
	var open = true;
	if($f43x){open=$f43x.obj!=obj;$f43x.close();}
	if(open){$f43x=new $f43o($(obj),match,height);}	
}
function $f43o(obj,match,height){
	this.obj=obj;
	this.match=match;
	this.height=height;
	this.active=false;
	var id=obj.id.substring(0,obj.id.length-3);
	this.hidden=obj.getParent().getElement('input[name='+id+']');
	this.c=$(id+'_43c');
	this.items=this.c.getElements('div');
	$fEvent(this.c,'mouseover',function(){$f43x.active=true;});
	$fEvent(this.c,'mouseout',function(){if($f43x){$f43x.active=false;}});
	$fEvent(obj,'blur',function(){if($f43x){$f43x.close();}});
	$fEvent(obj,'keyup',obj.readonly?null:function(e){if($f43x){if(e.code==13){$f43x.close();}else{$f43x.filter();$f43x.show();}}});
	this.init();
	this.show();
}
$f43o.prototype.init=function(){
	for(var i=0;i<this.items.length;i++){
		var item=this.items[i];
		var selected=item.id==this.hidden.value;
		item.setStyle('display','block');
		$fEvent(item,'click',function(e){$f43x.set(e.target);});
		$fEvent(item,'mouseover',selected?null:function(e){e.target.addClass('E43O');});
		$fEvent(item,'mouseout',selected?null:function(e){e.target.removeClass('E43O');});
		if(selected){
			item.addClass('E43S');
		}else{
			item.removeClass('E43S');
		}
	}
}
$f43o.prototype.show=function(){
    this.c.setStyle('display','block');
	if(this.c.getParent().get('tag')!='body'){this.c.inject($(document.body));}
	var h=0;
	for(var i=0;i<this.items.length;i++){h+=this.items[i].getSize().y;}
	if(h>0){
		this.c.setStyle('height',(this.height && this.height<h)?this.height:h);
		var op=this.obj.getPosition();
		var os=this.obj.getSize();
		var top=$fY(this.obj,this.c,1);
		this.c.setStyles({left:op.x,top:top,width:os.x-4});
	}else{
		this.c.setStyle('display','none');
		if(!this.match){
			this.hidden.value = this.obj.value;
		}
	}
}
$f43o.prototype.filter=function(){for(var i=0;i<this.items.length;i++){this.items[i].setStyle('display',this.items[i].get('text').toLowerCase().indexOf(this.obj.value.toLowerCase())==0?'block':'none');}}
$f43o.prototype.close=function(){
	if(this.active){return;}
	this.filter();
	var item=null;
	if(this.obj.value){for(var i=0;i<this.items.length;i++){if(this.items[i].getStyle('display')=='block'){item=this.items[i];break;}}}
	this.set(item);
}
$f43o.prototype.set=function(item){
	this.obj.value=item?item.get('text'):(this.match?'':this.obj.value);
	this.hidden.value=item?item.id:(this.match?'':this.obj.value);
    this.c.setStyle('display','none');
	this.obj.blur();
	$f43x=null;
}
/*56*/
var $f56C = {};
function $f56(obj,domain,enumId){
	var o = $(obj);
	var f56 = $f56C[o.uid];
	if(!f56){
		$f56C[o.uid]=new $f56o({obj:o,domain:domain,enumId:enumId});
	}else{
		f56.load();
	}
}
var $f56o = new Class({
	Implements: Options,	
	options: {
		obj:null,
		c:null,
		oldValue:'',
		targetValue:'',
		targetText:''
	},
	initialize: function(options){
		this.setOptions(options);
		var o = options.obj;
		var c = new Element('div',{'class':'E56C','styles':{'width':o.getSize().x-2,'display':'none'}});
		o.getParent().adopt(c);
		this.options.c=c;
		o.addEvent('keyup',function(){
			this.load();
		}.bind(this));
		o.addEvent('blur',function(e){
			this.close(true);
		}.bind(this));
		this.load();
	},
	load: function(){
		var url = '/adm/common/suggestion.do?domain='+this.options.domain+'&enumid='+this.options.enumId+'&key=';
		var key = this.options.obj.value;
			/*if(!key){
				this.close(false);
				return;
			}*/
			if(key==this.options.oldValue && key!=''){
				return;
			}
			this.options.oldValue = key;
			new Request.HTML({url:encodeURI(url+key),async:false,update:this.options.c}).post(document.forms[0]);
			if(this.options.c.get('html')){
				this.options.c.getElements('div').addEvents({
					'mouseover':function(e){
						e.target.addClass('E56O');
						this.options.targetText=e.target.get('text');
						this.options.targetValue=e.target.getElement('input').value;
					}.bind(this),
					'mouseout':function(e){
						e.target.removeClass('E56O');
						this.options.targetValue='';
						this.options.targetText='';
					}.bind(this)
				});
				this.show();
			}else{
				this.close(false);
			}
	},
	show: function(){
		this.options.c.setStyles({'display':'block','top':$fY(this.options.obj,this.options.c,1),'left':this.options.obj.getPosition().x});
	},
	close: function(select){
		this.options.c.setStyle('display','none');
		if(select && this.options.targetValue){
			this.set(this.options.targetText,this.options.targetValue);
		}else if(!this.options.obj.value){
			this.set('','');
    }
		this.options.targetValue='';
		this.options.targetText='';
		this.options.oldValue = '';
		if(select){
			$f56blur(this.options.obj);
		}
	},
	set:function(text,value){
		this.options.obj.value=text;
		var name = this.options.obj.name.substring(0,this.options.obj.name.length-3);
		this.options.obj.getParent('td').getElement('input[name='+name+']').value=value;
	}
});
function $f56blur(obj){}
/*92*/
var $f92x;
function $f92(obj,name){
	if($f92x){$f92x.close();}
	$f92x =new $f92o({obj:obj,name:name});
}
var $f92o = new Class({
	Implements: Options,	
	options: {
		obj:null,
		hidden:null,
		c:null,
		dialog:null
	},
	initialize: function(options){
		this.options.obj=$(options.obj);
		this.options.hidden=$fn(options.name);
		this.options.c =new Element('span',{'class':'E92C'});
		this.options.obj.getParent().adopt(this.options.c);
		this.options.c.setStyles({'top':$fY(this.options.obj,this.options.c,1),'left':this.options.obj.getPosition().x});
		new Request.HTML({url:'/adm/common/stamp.do',async:false,update:this.options.c}).post();
		this.options.c.getElements('img').addEvents({
			'mouseover':function(e){
				e.target.addClass('E92O');
			}.bind(this),
			'mouseout':function(e){
				e.target.removeClass('E92O');
			}.bind(this),
			'click':function(e){
				this.link=e.target.src;
				if(this.link && (this.link.indexOf('http://')==0 || this.link.indexOf('https://')==0)){
					this.link=this.link.substring(8);
					this.link=this.link.substring(this.link.indexOf('/'));
				}
				this.dialog=new MooDialog.Iframe('/adm/stamppassword.do',{'class': 'MooDialog E92D'});
			}.bind(this)
		});
	},
	set:function(){
		this.options.obj.src=this.link;
		this.options.hidden.value=this.link;
		this.close();
	},
	close:function(){
		this.options.c.destroy();
		if(this.dialog){this.dialog.close();}
		$f92x=null;
	}
});
$(document).addEvent('mouseup',function(e){
	if($f92x && !e.target.hasClass('E92C') && !e.target.hasClass('E92I')){
		$f92x.close();
	}
});



//获取登录终端
function getPlatformName(){
  var returnplatfromName='';
  var u = navigator.userAgent, app = navigator.appVersion;
  var platform = {trident: u.indexOf('Trident') > -1 //IE内核
	,opera: u.indexOf('Presto') > -1   //opera内核
	,"safari/chrome": u.indexOf('AppleWebKit') > -1  //苹果、谷歌内核
	,firefox: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1//火狐内核
	,mobile: !!u.match(/AppleWebKit.*Mobile.*/)//是否为移动终端
	,ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/) //ios终端
	,android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1  //android终端或者uc浏览器
	,iPhone: u.indexOf('iPhone') > -1  //是否为iPhone或者QQHD浏览器
	,iPad: u.indexOf('iPad') > -1 //是否iPad
	,webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
	,weixin: u.indexOf('MicroMessenger') > -1 //是否微信 （2015-01-22新增）
	,qq: u.match(/\sQQ/i) == " qq" //是否QQ
  };
  for(var item in platform)
  {
	if(platform[item])
	{
	  returnplatfromName=item;
	}
  }
  return returnplatfromName;
}

/***
*回到主页 pad
**/
function toHome(){
	location.reload();
}
