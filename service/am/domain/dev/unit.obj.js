var Status={DOWN:1,MOVE:2,UP:3};
function Action(){
	this.status=Status.UP;
	this.shadow=null;
	this.copy=null;
	this.target=null;
	this.targetArea=null;
}
Action.prototype.reset=function(){
	this.status=Status.UP;
	this.target=null;
	this.targetArea=null;
	if(this.copy){this.unmove();this.copy=null;}
	if(xs){
		this.shadow=getShadow(xs);this.markShadow();
	}else{
		this.shadow=null;
	}
}
Action.prototype.selectOnClick=function(){
	var src=event.srcElement;
	if(src.id!="_elements"){
		if(src.tagName=="SPAN"){this.unselectArea();this.selectShadow();
		}else{this.unselectShadow();this.selectArea();}
	}
}
Action.prototype.selectOnMove=function(){
	var src=event.srcElement;
	if(src.id!="_elements"){
		if(src.tagName=="SPAN"){
			if(src==this.shadow){
				this.unselectArea();
				this.unselectTarget();
			}else if(src!=this.copy && !(src.get('area')==this.shadow.get('area') && src.get('group')==this.shadow.get('group') && parseInt(src.get('order'))==parseInt(this.shadow.get('order'))+2)){
				this.unselectArea();
				this.selectTarget();
			}
		}else{
			this.unselectTarget();
			this.selectArea();
		}
	}
}
Action.prototype.selectShadow=function(){if(this.shadow != event.srcElement){this.unselectShadow();this.shadow=event.srcElement;this.markShadow();this.selectElement();}}
Action.prototype.unselectShadow=function(){
	if(this.shadow){this.shadow.style.borderColor="";this.shadow=null;}
	if(xs){xs.show(false);xs=null;}
}
Action.prototype.markShadow=function(){this.shadow.style.borderColor=Color.SELECTED;}
Action.prototype.selectElement=function(){for(var i=0;i<all.length;i++){var e=all[i];if(e.area==this.shadow.get('area') && e.group==this.shadow.get('group') && e.order==this.shadow.get('order')){xs=e;e.show(true);break;}}}
Action.prototype.selectArea=function(){var src=event.srcElement;if(this.targetArea != src){this.unselectArea();this.targetArea=$fDiv(src);this.targetArea.style.borderColor=Color.TARGET;}}
Action.prototype.unselectArea=function(){if(this.targetArea){this.targetArea.style.borderColor="";this.targetArea=null;}}
Action.prototype.selectTarget=function(){var src=event.srcElement;if(this.target != src){this.unselectTarget();this.target=event.srcElement;this.target.style.borderLeftWidth=3;this.target.style.borderLeftColor=Color.TARGET;}}
Action.prototype.unselectTarget=function(){if(this.target){this.target.style.borderLeftWidth=1;this.target.style.borderLeftColor="";this.target=null;}}
Action.prototype.clone=function(){
	this.selectShadow();
	var pane=$('_elements');
	this.copy= document.createElement('span');
	this.copy.appendChild(document.createTextNode(this.shadow.innerText));
	pane.getElement('td').appendChild(this.copy);
	pane.style.cursor='move';
  this.copy.addClass('cloneElement');
	listener.removeOverEvents();
}
Action.prototype.move=function(e){var left=document.body.scrollLeft+e.page.x;var top=document.body.scrollTop+e.page.y;this.copy.setStyles({left:left,top:top});this.selectOnMove();}
Action.prototype.unmove=function(){
	var pane=$('_elements');
	if(this.copy){pane.getElement('td').removeChild(this.copy);this.copy=null;}
	pane.style.cursor='default';
	this.unselectTarget();
	this.unselectArea();
	listener.addOverEvents();
}
Action.prototype.up=function(){
	if(this.target){xs.setArea(this.target.get('area'));xs.setGroup(this.target.get('group'));xs.setOrder(parseInt(this.target.get('order'))-1);this.unmove();layout();
	}else if(this.targetArea){xs.setArea(this.targetArea.get('area'));xs.setGroup(this.targetArea.get('group'));xs.setOrder(999);this.unmove();layout();
	}else{this.unmove();}
}
function Listener(){}
Listener.prototype.addEvents=function(){
	var pane=$('_elements');
	pane.style.cursor='default';
	pane.addEvent("mousedown",this.onmousedown);
	pane.addEvent("mouseup",this.onmouseup);
	pane.addEvent("mousemove",this.onmousemove);
	pane.addEvent("contextmenu",this.oncontextmenu);
	this.addOverEvents();
}
Listener.prototype.addOverEvents=function(){var pane=$('_elements');pane.addEvent("mouseover",this.onmouseover);pane.addEvent("mouseout",this.onmouseout);}
Listener.prototype.removeOverEvents=function(){var pane=$('_elements');pane.removeEvent("mouseover",this.onmouseover);pane.removeEvent("mouseout",this.onmouseout);}
Listener.prototype.oncontextmenu=function(){showMenu();window.event.returnValue=false;}
Listener.prototype.onmousedown=function(){xa.status=Status.DOWN;if(xa.copy){xa.unmove();}}
Listener.prototype.onmousemove=function(e){
	if(xa.status==Status.DOWN && event.srcElement.tagName!="SPAN"){xa.status=Status.UP;
	}else if(xa.status==Status.DOWN && event.srcElement.tagName=="SPAN"){xa.clone();xa.move(e);xa.status=Status.MOVE;
	}else if(xa.status==Status.MOVE){xa.move(e);}
}
Listener.prototype.onmouseup=function(){
	if(xa.status==Status.MOVE){xa.up();
	}else{xa.selectOnClick();}
	xa.status=Status.UP;
}
Listener.prototype.onmouseover=function(e){var src=e.target;if(src.tagName=="SPAN" && src!=xa.shadow && src!=xa.copy && src!=xa.target){src.style.borderColor=Color.OVER;}}
Listener.prototype.onmouseout=function(e){var src=e.target;if(src.tagName=="SPAN" && src!=xa.shadow && src!=xa.copy && src!=xa.target){src.style.borderColor='';}}
function Unit(){
	this.toolbarGroup=new _Group();
	this.hiddenGroup=new _Group();
	this.invisibleGroup=new _Group();
	this.groups=new Array();
	this.load();
}
Unit.prototype.load=function(){
	this.loadGroups();
	for(var i=0;i<all.length;i++){
		var e=all[i];
		if(e.isRemoved()) continue;
		if(e.area=="i"){this.invisibleGroup.es.push(e);
		}else if(e.area=="t"){this.toolbarGroup.es.push(e);
		}else if(e.area=="h"){this.hiddenGroup.es.push(e);
		}else{this.getGroup(e.group).es.push(e);}		
	}
	for(var i=0;i<this.groups.length;i++){this.groups[i].loadRows();}
}
Unit.prototype.loadGroups=function(){
	this.groups.push(new _Group());
	var g=document.all("unit.g").value;
	if(!isList && document.all("unit.la").value!="0" && g){
		var configs=g.split(";");
		for(var i=0;i<configs.length;i++){var config=configs[i].split(",");if(config.length>1){this.groups.push(new _Group(config[0],config[1]));}}	
	}
}
Unit.prototype.getGroup=function(groupId){
	if(!groupId || groupId=='null') return this.groups[0];
	for(var i=1;i<this.groups.length;i++){if(groupId==this.groups[i].id)return this.groups[i];}
	return this.groups[0];
}
Unit.prototype.writeToolbar=function(){this.toolbarGroup.sort();return write(this.toolbarGroup.es);}
Unit.prototype.writeHidden=function(){this.hiddenGroup.sort();return write(this.hiddenGroup.es);}
Unit.prototype.writeInvisible=function(){this.invisibleGroup.sort();return write(this.invisibleGroup.es);}
Unit.prototype.writeGrid=function(){if(this.groups.length==0){return "";}return write(this.groups);}
function _Group(id,name){
	this.id=id;
	this.name=name;
	this.es=new Array();
	this.rows=new Array();
}
_Group.prototype.sort=function(){this.es.sort(sort);for(var i=0;i<this.es.length;i++){this.es[i].setOrder(i*2);}}
_Group.prototype.loadRows=function(){
	this.sort();
	for (var i=0; i < this.es.length; i++){
		var e=this.es[i];
		if(e.isAddLabel()){this.add(e.clone());}
		this.add(e);
	}
}
_Group.prototype.add=function(e){
	var rowIndex=this.getRowIndex(e.cs);
	this.rows[rowIndex].add(e);
	if( e.rs>1 && e.cs>0){for (var i=1; i < e.rs; i++){this.getRow(rowIndex+i).column+=e.cs;}}
}
_Group.prototype.getRowIndex=function(cs){
	if(this.rows.length== 0){
		this.rows.push(new Row(this));
		return 0;
	}else if(column <= 0){
		return 0;
	}else{
		for (var i=0; i < this.rows.length; i++){if((cs==0 && this.rows[i].column==column && i<this.rows.length-1 && this.rows[i+1].cells.length==0) || this.rows[i].column<column){return i;}}
		if(cs > 0){this.rows.push(new Row(this));}
		return this.rows.length - 1;
	}
}
_Group.prototype.getRow=function(rowIndex){
	if(rowIndex >= this.rows.length){var row=new Row(this);this.rows.push(row);return row;
	}else{return this.rows[rowIndex];}
}
_Group.prototype.toString=function(){
	if(!this.id && this.es.length<1){return "";}
	var html="<div area=\"g\" group=\""+this.id+"\"><table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\">";
	html+=getColgroups();
	if(this.id){html+= "<tr group=\""+this.id+"\" onclick=\"showGroup('"+this.id+"')\" class=\"group\"><td unselectable=\"on\" colspan=\""+column+"\">"+this.name+"</td></tr>";}
	return html+write(this.rows)+"</table></div>";
}
function Row(group){
	this.group=group;
	this.column=0;
	this.cells=new Array();
}
Row.prototype.add=function(e){if(e.cs>0 || this.cells.length==0){var cell=new Cell();cell.add(e);this.cells.push(cell);this.column+=e.cs==0?1:e.cs;}else{this.cells[this.cells.length-1].add(e);}}
Row.prototype.toString=function(){
	var html="<tr"+(this.group.id?" id=\""+this.group.id:"")+"\">";
	html+=write(this.cells);
	var count=column-this.column;
	for(var i=0;i<count;i++){html+="<td>&nbsp;</td>";}
	return html+"</tr>";
}
function Cell(){
	this.es=new Array();
}
Cell.prototype.add=function(e){this.es.push(e);}
Cell.prototype.toString=function(){
	var html="<td";
	var first=this.es[0];
	if(first){if(first.rs>1){html+=" rowspan=\""+first.rs+"\"";}if(first.cs>1){html+=" colspan=\""+first.cs+"\"";}}
	return html+">"+write(this.es)+"</td>";
}
function $fe(obj){
	this.grid;
	this.wrapper;
	this.id;
	this.name;
	this.c="0";
	this.rs=1;
	this.cs=1;
	this.area;
	this.group;
	this.order;
	this.au;
	this.action;
	this.r;
	this.message;
	if(obj){this.load(obj);}
}
$fe.prototype.load=function(obj){
	if(obj){this.grid=_getGridWithoutId(obj);this.wrapper=_getCardWrapper(this.grid);}
	this.c=this.grid.all("e.c").value;
	this.id= this.grid.all("e.eid").value;
	this.name=this.grid.all("e.n").value;
	var rs=this.grid.all("e.wrs").value;
	if(_isInt(rs) && rs>0){this.rs=parseInt(rs);
	}else{this.rs=1;this.grid.all("e.wrs").value=1;}
	var cs=this.grid.all("e.wcs").value;
	if(_isInt(cs)){this.cs=cs<0?0:parseInt(cs);
	}else{this.cs=1;this.grid.all("e.wcs").value=1;}
	if("0"==this.grid.all("e.s").value){this.area="i";
	}else if("0"==this.grid.all("e.b").value){this.area="t";
	}else if(this.c=="2"){this.area="h";
	}else{this.area="g";}
	this.group=this.grid.all("e.g").value;
  if(!this.group){this.group='null';}
	var order=this.grid.all("e.o").value;
	this.order=order?parseInt(order):1;
	this.action=this.grid.all("e.a").value=="1"?"1":"0";
	this.au=this.grid.all("e.au").value=="1"?"1":"0";
	this.r=this.grid.all("e.r").value;
	$(this.wrapper).getElement('ul li a[id=w]').setStyle('display',((this.area=='g' || this.area=='i') && this.cs>0)?'inline-block':'none');
	$(this.wrapper).getElement('ul li a[id=x]').setStyle('display',this.action=='1'?'inline-block':'none');
	$(this.wrapper).getElement('ul li a[id=y]').setStyle('display',this.action=='1'?'inline-block':'none');
}
$fe.prototype.isAddLabel=function(){return !isList && "0"!=this.grid.all("e.al").value && this.cs>0;}
$fe.prototype.clone=function(){var e=new $fe();e.name=this.name;return e;}
$fe.prototype.isRemoved=function(){
	if(!this.grid) return true;//removed
	var o=this.grid.all("e.");
	return o && o.value=="3";
}
$fe.prototype.isInvalid=function(){
	if(!this.id){
		this.message="'元素编号'不能为空!";
		return true;
	}else if(!this.r && (this.c=="3" || this.c=="6" || this.c=="43" || this.c=="44" || this.c=="45" || this.c=="48" || this.c=="53" || this.c=="56")){
		this.message="没有在'资源编号'处指定枚举编号!";
		return true;
	}else if(!this.r && this.c=="80"){
		this.message="没有在'资源编号'处指定单元编号!";
		return true;
	}else if(!this.r && this.c=="85"){
		this.message="没有在'资源编号'处指定树编号!";
		return true;
	}else if(!this.r && this.c=="87"){
		this.message="没有在'资源编号'处指定统计图编号!";
		return true;
	}else if(!this.grid.all("e.l").value && (this.c=="11" || this.c=="12" || this.c=="13" || this.c=="30")){
		this.message="没有指定'链接'!";
		return true;
	}else if(this.c=="60" && !this.grid.all("e.cu").value){
		this.message="没有在'定制'处指定排序字段!";
		return true;
	}
	return false;
}
$fe.prototype.toString=function(){
	if(this.area){
		var html="<span unselectable=\"on\" area=\""+this.area+"\" group=\""+this.group+"\" order=\""+this.order+"\" class=\"e"+this.c+this.action+this.au+"\"";
		if(this.isInvalid()){html+=" title=\""+this.message+"\" style=\"color:"+Color.ALERT+"\"";
		}else if(this.name){html+=" title=\""+this.name+"\"";}
		return html+">"+this.id+"</span>";
	}else {
		return this.name;
	}
}
//---------------------------------
$fe.prototype.remove=function(){
	var o=this.grid.all("e.");
	if(!o) return ;
	if(o.value=="1"){this.wrapper.removeNode(true);this.grid=null;
	}else{this.grid.all("e.").value="3";this.setOrder(-2);this.show(false);}
}
$fe.prototype.setAddLabel=function(){var obj=this.grid.all("e.al");obj.value=(obj.value=="1"?"0":"1");}
$fe.prototype.setIsAction=function(value){var obj=this.grid.all("e.a");obj.value=value?value:(obj.value=="1"?"0":"1");}
$fe.prototype.setAuth=function(value){var obj=this.grid.all("e.au");obj.value=value?value:(obj.value=="1"?"0":"1");}
$fe.prototype.setGroup=function(group){this.group=group?group:'null';this.grid.all("e.g").value=this.group;}
$fe.prototype.setOrder=function(order){this.order=parseInt(order);this.grid.all("e.o").value=order;}
$fe.prototype.show=function(visible){this.wrapper.style.display=visible?"block":"none";}
$fe.prototype.setComponent=function(c){this.c=c;this.grid.all("e.c").value=c;configComponent(this);}
$fe.prototype.setId=function(id){this.id=id;this.grid.all("e.eid").value=id;}
$fe.prototype.setName=function(name){this.name=name;this.grid.all("e.n").value=name;}
$fe.prototype.setLink=function(link){this.grid.all("e.l").value=link;}
$fe.prototype.setArea=function(area){
	this.area=area;
	if(area=="i"){
		this.grid.all("e.s").value="0";
	}else{
		this.grid.all("e.s").value="1";
		if(area=="t"){
			this.grid.all("e.b").value="0";
		}else{
			this.grid.all("e.b").value="1";
			if(area=="h"){
				this.setComponent("2");
			}else	if(this.c=="2"){
				this.setComponent("1");
			}
		}
	}
	this.load();
}
