/* Tree */
var TreeSettings={
	imagesPath  : '/common/tree/i/',
	defaultOpen : false,
	showToggle  : true,
	showIcons   : true,
	showLines   : true,
	moveable : false,
	addible	: false,
	insertPercent : 0.7,
	supportCheck    : false,
	supportCheckAll	: false,
	containerAttribute : '',
	blankIcon   : '0.png',
	closedFolderIcon   : 'cf.png',
	openFolderIcon : 'of.png',
	fileIcon : 'i.png',
	defaultText : 'node',
	defaultTarget : null,
	defaultAttribute      : '',
	moveBeforeInterceptor : null,
	moveAfterInterceptor  : null
};
var TreeNaming={
	CONTAINER_ID : '_tcid',
	CHECK_NAME : '_treecheck',
	CSS     : 'tree',
	A_COLOR : 'ac',
	CHECKBOX_CSS : 'cc',
	TOGGLE_CSS   : 'tc',
	CLONE_CSS    : 'clone',
	ADD_CSS      : 'add',
	INSERT_CSS   : 'insert'
}
var TreeAction={
	NONE : 0,	
	DOWN : 1,
	MOVE : 2,
	UP   : 3
};
var th={
	all : {},
	t   : null,
	c   : null,
	ae  : new ActionExecutor(),
	l   : new TreeListener(),
	set : function(tree){if(!this.c){this.c=document.getElementById(TreeNaming.CONTAINER_ID);this.l.attachEvents();}this.t=tree;this.ae=new ActionExecutor();},
	getHtmlNode: function(e){return e.target.parentNode},
	getNode	: function(e){return this.all[this.getHtmlNode(e).id]},
  getBlank: function(){return '<img src="'+TreeSettings.imagesPath+TreeSettings.blankIcon+'">';},
  getA  : function(hn){return hn.getElementsByTagName("A")[0];},
  write : function(){this.t.writeTree();}
};
function ActionExecutor(){
	this.s=null;//selected
	this.t=null;//target
	this.c=null;//clone
	this.a=TreeAction.NONE;
	this.h=0;
	this.up=true;
	this.offset=0;//mouse between the top/bottom of selected
	this.add=false;
	this.height=0;
	this.end=false;
	this.ms='';//multi select
}
ActionExecutor.prototype.select=function(news){
	if(this.s){
		th.getA(this.s).className=th.all[this.s.id].css || "";
	}
	this.s=news;
	th.getA(this.s).className="active";
}
ActionExecutor.prototype.unselect=function(news){
	if(this.s){
		th.getA(this.s).className="";
		this.s=null;
	}
}
ActionExecutor.prototype.target=function(){
}
ActionExecutor.prototype.checkedAll=function(node,isChecked){
	if(TreeSettings.supportCheckAll){
		node.checkedAll(!isChecked);
	}
	this.setChecked(node.getHtmlNode(),isChecked); //three times!
}
ActionExecutor.prototype.check=function(htmlNode,isChecked){
	/*var str=","+htmlNode.id+",";
	var index=this.ms.indexOf(str);
	if(index>=0){
		this.ms=this.ms.substring(0,index)+this.ms.substring(index+str.length,this.ms.length);
	}
	if(isChecked){
		this.ms+=str;
	}*/
	this.setChecked(htmlNode,isChecked); 
}
ActionExecutor.prototype.setChecked=function(htmlNode,isChecked){
	htmlNode.childNodes[htmlNode.childNodes.length-2].checked=isChecked;
}
ActionExecutor.prototype.resetTarget=function(){
	if(this.t){
		th.getA(this.t).className="";
		this.t=null;
	}
}
ActionExecutor.prototype.reset=function(){
	th.c.removeChild(this.c);
	this.c=null;
	this.setCursor('');
}
ActionExecutor.prototype.setCursor=function(c){
	th.c.style.cursor=c;
	var as=th.c.getElementsByTagName("A");
	for(var i=0;i<as.length;i++){
		as[i].style.cursor=c;
	}
}
ActionExecutor.prototype.clone=function(e){
	this.h=e.client.y;
	if(this.c==null){
		this.c=document.createElement('div');
		this.c.appendChild(document.createTextNode(th.getA(this.s).innerText));
		th.c.appendChild(this.c);
		this.c.className=TreeNaming.CLONE_CSS;
	}
	this.setCursor('move');
}
ActionExecutor.prototype.move=function(e){
	var overflow=th.c.style.overflow;
	var left=document.body.scrollLeft+(overflow=="auto"?e.page.x:e.client.x);
	var top=document.body.scrollTop+(overflow=="auto"?e.page.y:e.client.y);
  this.c.setStyles({left:left,top:top});
	var target=e.target;
	if(target.tagName!="DIV"){
		target=target.parentNode;
	}
	if(target.id && target.id!=TreeNaming.CONTAINER_ID){
		target.className="div";
		if(this.up!=(e.client.y<this.h)){
			this.up=!this.up;
			this.offset=0;
		}
		if(this.t==null && this.offset==0 && target!= this.s){
			this.offset=this.up?(this.h-e.client.y):(e.client.y-this.h);
		}
		this.resetTarget();
		if(target!= this.s){
			var moveable=true;
			this.t=target;
			this.setAdd(e);
			if(TreeSettings.moveBeforeInterceptor){
				moveable=TreeSettings.moveBeforeInterceptor();
			}
			if(moveable){
				if(TreeSettings.addible && this.add){
					th.getA(this.t).className=TreeNaming.ADD_CSS;
				}else if(!th.all[this.s.id].isLastElderBrother(th.all[this.t.id])){
					th.getA(this.t).className=TreeNaming.INSERT_CSS;
				}
			}else{
				this.t=null;
			}
		}
	}
}
ActionExecutor.prototype.setTarget=function(){
}
ActionExecutor.prototype.setAdd=function(e){
	var percent;
	var t=th.all[this.t.id];
	var s=th.all[this.s.id];
	var targetHeight=t.getHtmlNode().offsetHeight;
	var selectHeight=s.getHtmlNode().offsetHeight;
	var mouseOffset=this.h-e.client.y;
	var toTop=0;
	if(this.up){
		th.t.setHeight(t,s);
		toTop=((this.height+this.offset)-mouseOffset);
	}else{
		th.t.setHeight(s,t);
		toTop=(-mouseOffset-(this.height-selectHeight+this.offset));
	}
	percent=toTop/targetHeight;
	this.add=percent>TreeSettings.insertPercent;
	return this.add;
}
ActionExecutor.prototype.doAction=function(){
	if(this.t==null){
		return;
	}
	var stree=th.all[this.s.id];
	var ttree=th.all[this.t.id];
	if(TreeSettings.addible && this.add){
		if(ttree.validAdd(stree)){
			stree.remove();
			ttree.setOpen(true);
			ttree.addAndRewrite(stree);
			th.ae.select(stree.getHtmlNode());
			if(TreeSettings.moveAfterInterceptor){
				TreeSettings.moveAfterInterceptor();
			}
		}
	}else if(!stree.isLastElderBrother(ttree)){
		if(ttree.p.validAdd(stree)){
			stree.remove();
			ttree.p.addAndRewrite(stree,ttree.getIndex());
			th.ae.select(stree.getHtmlNode());
			if(TreeSettings.moveAfterInterceptor){
				TreeSettings.moveAfterInterceptor();
			}
		}
	}else{
		this.resetTarget();
	}
}
function TreeListener(){
}
TreeListener.prototype.attachEvents=function(){
	$(th.c).addEvent("mousedown", function(e){
		var src=e.target;
		if(src.tagName=="A"){
			if(TreeSettings.moveable){
				th.ae.a=TreeAction.DOWN;
			}
			var node=th.getNode(e);
			th.ae.select(th.getHtmlNode(e));
			if(!e.rightClick && node.action &&(typeof node.action=="function")){
				node.action();
			}
		}else if(src.className==TreeNaming.TOGGLE_CSS){
			th.getNode(e).toggle();	
		}else if(src.className==TreeNaming.CHECKBOX_CSS){
			th.ae.checkedAll(th.getNode(e),src.checked);		
		}
	});
	if(TreeSettings.moveable){
		$(th.c).addEvent("mousemove", function(e){
			if(th.ae.a==TreeAction.DOWN){
				th.ae.clone(e);
				th.ae.move(e);
				th.ae.a=TreeAction.MOVE;
			}else if(th.ae.a==TreeAction.MOVE){
				th.ae.move(e);
			}
		});
		$(th.c).addEvent("mouseup", function(e){
			if(th.ae.a==TreeAction.MOVE){
				th.ae.reset();
				th.ae.doAction();
			}
			th.ae.a=TreeAction.UP;
		});
	}
}
function Tree(id,text,action,icon,attribute){
	this.id=id;
	this.text=text || TreeSettings.defaultText;
	this.action=action;
	this.open;
	this.css=TreeNaming.A_COLOR;
	this.icon=icon;
	this.openIcon;
	this.target;
	this.title;
	this.attribute=attribute;
	this.checkValue;
	this.checkAttribute;
	this.p;
	this.cs =[];
	th.all[id]=this;
	this.selected;
	this.valid=true;
}
Tree.prototype.isLastElderBrother=function(node){
	return this.p==node.p && this.getIndex()==node.getIndex()-1;
}
Tree.prototype.setHeight=function(startNode,endNode){
	//alert(this.id);
	if(this==startNode){
		th.ae.end=false;
		th.ae.height =0;
	}
	if(this==endNode){
		th.ae.end=true;
		return;
	}
	if(!th.ae.end){
		th.ae.height +=this.getHtmlNode().offsetHeight-1;
	}
	for(var i=0;i<this.cs.length;i++){
		this.cs[i].setHeight(startNode,endNode);
	}
}
Tree.prototype.getHtmlNode=function(){
	return $(th.c).getElements('div[id='+this.id+']')[0];
}
Tree.prototype.checkedAll=function(isChecked){
	th.ae.check(this.getHtmlNode(),isChecked);
	for(var i=0;i<this.cs.length;i++){
		this.cs[i].checkedAll(isChecked);
	}
}
Tree.prototype.getSelected=function(){
	return th.ae.s;
}
Tree.prototype.getSelectedId=function(){
	return th.ae.s?th.ae.s.id:null;
}
Tree.prototype.getSelectedTree=function(){
	return th.ae.s?th.all[th.ae.s.id]:null;
}
Tree.prototype.getChecked=function(){
	var result=th.ae.ms.replace(/,,/g, ',');
	return(result.substring(1,result.length-1)).split(",");
}
Tree.prototype.last=function(){
	return this.p?this==this.p.cs[this.p.cs.length-1]:true;
}
Tree.prototype.root=function(){
	return !this.p;
}
Tree.prototype.getIndex=function(){
	for(var i=0; i < this.p.cs.length; i++){
		if(this==this.p.cs[i]){
			return i;
		}
	}
}
Tree.prototype.setOpen=function(c){
	this.open=c;
	Cookie.write(this.id,c?"1":"0");
}
Tree.prototype.toggle=function(){
	this.doToggle(!this.isOpen());
	th.ae.select(this.getHtmlNode());
}
Tree.prototype.doToggle=function(c){
	this.setOpen(c);
	if(this.cs.length>0){
		$(this.getHtmlNode()).getNext().setStyle('display',this.open?'block':'none');
	}	
	var hcs=this.getHtmlNode().childNodes;
	if(TreeSettings.showIcons){
		hcs[hcs.length-2-(TreeSettings.supportCheck?1:0)].src=this.getIcon();
	}
	if(!this.root()){
		var t=hcs[hcs.length-2-(TreeSettings.showIcons?1:0)-(TreeSettings.supportCheck?1:0)];
		if(t.tagName=="IMG"){
			t.src=TreeSettings.imagesPath+this.getToggleIcon()+".png";
		}	
	}
}
Tree.prototype.toggleAll=function(){
	this.doToggleAll(!this.isOpen());
	th.ae.select(this.getHtmlNode());
}
Tree.prototype.doToggleAll=function(c){
	this.doToggle(c);
	for(var i=0; i < this.cs.length; i++){
		if(this.cs[i].cs.length>0){
			this.cs[i].doToggleAll(c);
		}
	}
}
Tree.prototype.doToggleChild=function(c){
	for(var i=0; i < this.cs.length; i++){
		if(this.cs[i].cs.length>0){
			this.cs[i].doToggleAll(c);
		}
	}
}
Tree.prototype.getToggleIcon=function(){
	var icon="";
	if(this.cs.length>0 && TreeSettings.showToggle){icon=this.isOpen()?"0":"1";}
	if(TreeSettings.showLines){
		return icon+=(this.last()? "L":"T");
	}else{
		return icon+="0";
	}
}
Tree.prototype.getToggleHtml=function(){
	if(this.root()){
		return "";
	}
	var icon=this.getToggleIcon();
	if(icon=="0"){
		return th.getBlank();
	}else{
		return "<img"+((this.cs.length && TreeSettings.showToggle)>0?AA("class",TreeNaming.TOGGLE_CSS):"")+" src=\""+TreeSettings.imagesPath+icon+".png\"/>";
	}	 
}
Tree.prototype.getIcon=function(){
	if(this.cs.length>0){
		return icon=this.isOpen()?(this.openIcon || this.icon || TreeSettings.imagesPath+TreeSettings.openFolderIcon):(this.icon || TreeSettings.imagesPath+TreeSettings.closedFolderIcon);
	}else{
		return icon=this.icon || TreeSettings.imagesPath+TreeSettings.fileIcon;
	}
}
Tree.prototype.getIconHtml=function(){
	if(!TreeSettings.showIcons){
		return "";
	}
	var icon=this.getIcon();
	return "<img src=\""+icon+"\"/>";
}
Tree.prototype.getLines=function(){
	var lines=[];
	var index=0;
	var p=this;
	while(p.p){ 
		p=p.p; 
		if(p.root()){
			break;
		}
		if(!TreeSettings.showLines || p.last()){
			lines[index]=th.getBlank();
		}else{
			lines[index]="<img src=\""+TreeSettings.imagesPath+"1.png\"/>";
		}
		index+=1;
	}
	return lines.reverse().join("");
}
Tree.prototype.getText=function(){
	return this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
Tree.prototype.getAction=function(){
	var result="<a unselectable=\"on\""+AA("title",this.title)+AA("class",this.css)+(this.attribute?" "+this.attribute:"");
	if(this.action && !(typeof this.action=="function")){
		var target=this.target || TreeSettings.defaultTarget ;	 
		result+=AA("href",this.action) + AA("target",target);
	}
	result+=">"+this.getText()+"</a>";	
	return result;
}
Tree.prototype.getCheckBox=function(){
	if(TreeSettings.supportCheck){
		return '<input type="checkbox" name="'+TreeNaming.CHECK_NAME+'"'+(this.checkAttribute?' '+this.checkAttribute:'')+' class="'+TreeNaming.CHECKBOX_CSS+'" value="'+(this.checkValue?this.checkValue:this.id)+'"/>';
	}else{
		return '';
	}
}
Tree.prototype.validAdd=function(node){
	var valid=true;
	if(node==this){
		valid= false;
	}else{
		var p=this;
		while(p =p.p){ 
			if(node==p){
				valid= false;
				break;
			}
		}
	}
	if(!valid){
		//alert("invalid add : a node can't add to the tree which contain itself!");
	}
	return valid;
}
Tree.prototype.isOpen=function(){
	if(this.open != undefined){
		return this.open;
	}
	var open=Cookie.read(this.id);
	if(open){
		return open== '1';
	}else{
		return this.root()? true:TreeSettings.defaultOpen;
	}
}
Tree.prototype.doAdd=function(node,index){
	var length=this.cs.length;
	if(!index && index!=0) index=length;
	for(var i=length-1;i>=index;i--){
		this.cs[i+1]=this.cs[i];
	}
	this.cs[index]=node;
	node.p=this;
	return node;
}
Tree.prototype.add=function(node,index){
	if(!this.validAdd(node)){
		return node;
	}
	this.doAdd(node,index);
	return node;
}
Tree.prototype.addAndRewrite=function(node,index){
	if(!this.validAdd(node)){
		return node;
	}
	this.setOpen(true);
	this.doAdd(node,index);
	if(th.c){
		th.write();
		//th.ae.select(node.getHtmlNode());
	}
	return node;
}
Tree.prototype.removeAndRewrite=function(){
	this.remove();
	if(th.c) th.write();
}
Tree.prototype.remove=function(){
	if(this.root()){
		return;
	}
	var index=this.getIndex();
	for(var i=index; i < this.p.cs.length-1; i++){
		this.p.cs[i]=this.p.cs[i+1];
	}
	this.p.cs.length -= 1;
}
Tree.prototype.writeTree=function(c){
	if(th.c){
		th.c.innerHTML=this.toHtml();
	}else{
		var html="<div "+TreeSettings.containerAttribute+" id=\""+TreeNaming.CONTAINER_ID+"\" class=\""+TreeNaming.CSS+"\">"+this.toHtml()+"</div>";
		if(c){
			c.innerHTML=html;
		}else{
			document.write(html);
		}
	}
	th.set(this);
}
Tree.prototype.toHtml=function(){
	var result="<div id=\""+this.id+"\">"+this.getLines()+this.getToggleHtml()+this.getIconHtml()+this.getCheckBox()+this.getAction()+"</div>";
	if(this.cs.length>0){
		result+="<div style=\"display:"+(this.isOpen()?"block":"none")+"\">";
		var items=[];
		for(var i=0; i < this.cs.length; i++){
			items[i]=this.cs[i].toHtml();
		}
		result+=items.join("");
		result+="</div>";
	}
	return result;
}
function AA(name,value){
	return(name && value)? " "+name+"=\""+value+"\"":"";
}
function Suite(){
	//todo support muli tree ?
	this.cs={}; //set 'tree:container' pairs in it
}
//menu
var MenuSettings={
	selectIcon : 'select.png'
}
var MenuNaming={
	CONTAINER_ID_PREFIX : '_mcidp_',
	CSS : 'menu',
	CSS_INNER_BORDER : 'ib',
	CSS_SEPARATOR : 'separator',
	CSS_ARROW:'arrow',
	CSS_ICON:'icon',
	CSS_OVER:'over',
	CSS_DISABLED:'disabled',
	CSS_DISABLED_OVER:'disabled_over'
}
var mh={
	cs : {},
	gl  : null,
	ids : [],
	out : true,
	getC : function(t){return this.cs[t.id]},
	select : function(e){var node=this.getNode(e);if(node && node.p && this.getC(node.p)){node.p.select(e,node);}},//&& this.getC(node.p):过滤bar的一级节点
	unselect : function(e){if(e.target.tagName=="DIV"){return;}var node=this.getNode(e);if(node && node.p){node.p.unselect();}},
	clearAll : function(){
		for(var i=0;i<this.ids.length;i++){
			var c=this.cs[this.ids[i]];
			if(c){
				c.style.display="none";
		}}},
	getNode : function(e){
			var src=e.target; 
			if(src.tagName=="DIV"){
				return src.id?th.all[src.id.substring(MenuNaming.CONTAINER_ID_PREFIX.length,src.id.length)]:null;
			}else{
				return th.all[$fTr(src).id];
			}
		},
	contains : function(id){for(var i=0;i<ids.length;i++){if(dis[i]==id){return true;}} return false;},
	set  : function(t,c){if(this.gl==null){this.gl=new GlobalListener();}this.cs[t.id]=c;this.ids[this.ids.length]=t.id; var l=new MenuListener();l.attachEvents(c);}
}
function GlobalListener(){
	$(document).addEvent("mousedown",function(e){
		if(mh.out && bh.selected!=e.target){
			mh.clearAll();
			bh.unselect();
		}
	});
}
function MenuListener(){
}
MenuListener.prototype.attachEvents=function(c){
	$(c).addEvent("click", function(e){
		if(e.target.tagName!="DIV"){
			var node=mh.getNode(e);
			if(node && node.action){
				if(typeof node.action=="function"){
					node.action();mh.clearAll();bh.unselect();
				}else if(e.target.tagName!="A"){
					var row = e.target.getParent('tr');
					if(row){
						//alert(row.getElement('a').outerHTML);
						var a = row.getElement('a');
						if(a && a.get('href') && !a.get('target')){
							window.location=a.get('href');mh.clearAll();bh.unselect();
						}
					}
				}
			}
		}
	});
	$(c).addEvent("mouseover",function(e){
		mh.out=false;
		mh.select(e);
	});
	$(c).addEvent("mouseout", function(e){
		mh.out=true;
		mh.unselect(e);
	});
}
Tree.prototype.select=function(e,node){//与父同步
	if(this.selected!=node){
		this.unselect();
		this.selected=node;
		node.getMenuNode().className=this.selected.isDisabled()?MenuNaming.CSS_DISABLED_OVER:MenuNaming.CSS_OVER;
		if(node.valid && node.cs.length>0){
			node.writeMenu(e,null);
		}
		if(this.p && mh.getC(this.p)){//&& mh.getC(this.p):过滤bar的一级节点
			this.p.select(e,this);
		}
	}
}
Tree.prototype.getMenuNode=function(){
	var c = mh.getC(this.p?this.p:this);
	if(!c){c=mh.getC(this)};//对于bar,一级节点触发menu,但仍然有父节点(p,不存在于mh.cs中)
	return $(c).getElement('#'+this.id);
}
Tree.prototype.unselect=function(){//与子同步
	if(this.selected){
		this.selected.unselect();
		this.selected.getMenuNode().className=this.selected.isDisabled()?MenuNaming.CSS_DISABLED:"";
		var c=mh.cs[this.selected.id];
		if(c){
			c.style.display="none";
		}
		this.selected=null;
	}
}
Tree.prototype.writeMenu=function(e,htmlNode){
	if(this.cs.length<1){return null;}
	var c=mh.getC(this);
	if(!c){
		c=document.createElement('div');
		c.id=MenuNaming.CONTAINER_ID_PREFIX+this.id;
		c.className=MenuNaming.CSS;
		c.innerHTML=this.toMenuHtml();
		document.body.appendChild(c);
		mh.set(this,$(c));
	}
	if(!e) e= SearchEvent();
	this.showMenu(e,htmlNode);
	return c;
}
Tree.prototype.toMenuHtml=function(){
	var rows=[];
	for(var i=0; i < this.cs.length; i++){
		rows[i]=this.cs[i].getMenuHtml();
	}
	//id="+MenuNaming.CONTAINER_ID_PREFIX+this.id+" 
	return "<div class=\""+MenuNaming.CSS_INNER_BORDER+"\"><table cellspacing=\"0\" style=\"width:auto\">"+rows.join("")+"</table></div>";
}
Tree.prototype.getMenuHtml=function(){
	var result ="<tr id=\""+this.id+(this.isDisabled()?"\" class=\""+MenuNaming.CSS_DISABLED:"")+"\"><td class=\""+MenuNaming.CSS_ICON+"\">";
	result +=this.icon?("<img src=\""+this.icon+"\"/>"):th.getBlank();
	result +="</td><td>"+this.getAction()+"</td><td class=\""+MenuNaming.CSS_ARROW+"\">";
	if(this.cs.length>0){
		result +="<em></em>";
	}
	result +="</td></tr>";
	return result;
}
Tree.prototype.isDisabled=function(){
	return !this.valid ||(this.cs.length==0 && !this.action);
}
Tree.prototype.showMenu=function(e,htmlNode){
	var c=mh.cs[this.id];
	var d=$(document);
	var left;
	var fixLeft=0;
	var top;
	var fixTop=0;
	c.style.display="block";
	if(htmlNode){
		left=htmlNode.getPosition().x;
		top=htmlNode.getPosition().y+htmlNode.getSize().y;
	}else if(this.p){
		var pc=mh.cs[this.p.id];
		left=parseInt(pc.style.left)+pc.offsetWidth-5;
		fixLeft=parseInt(pc.style.left)- c.offsetWidth+5;
		var p=pc.getElement('tr[id='+this.id+']');
		top=pc.offsetTop+p.offsetTop;
		fixTop=this.getMenuNode().offsetHeight+4;
	}else{
		left=d.getScroll().x+(e?e.client.x:event.clientX);
		fixLeft=left - c.offsetWidth;
		top=d.getScroll().y+(e?e.client.y:event.clientY);
	}
	//fix
	if(left+c.offsetWidth > d.getScroll().x+d.getSize().x){
		left=fixLeft<0?0:fixLeft;
	}
	var cHeight=$(c).getSize().y;
	if(d.getSize().y+d.getScroll().y<top+cHeight){
		top=top-cHeight+fixTop;
		if(top<0) top=0;
	}
	c.setStyles({left:left,top:top});
	if(htmlNode && c.offsetWidth<htmlNode.offsetWidth){
		$(c).getElement("table").style.width=htmlNode.offsetWidth-5;//for bar
	}
}
Tree.prototype.getHeight=function(node){
	var height=0;
	for(var i=0; i < this.cs.length; i++){
		if(this.cs[i]==node){
			break;
		}
		var item=this.cs[i];
		if(!item.id){//Separator
			height+=4;
		}else{
			height+= item.getMenuNode().offsetHeight;
		}
	}
	return height;
}
function MenuSeparator(){
	this.tree=Tree;
	this.tree();
}
MenuSeparator.prototype=new Tree;
MenuSeparator.prototype.getMenuHtml=function(){
	return "<tr><td colspan=\"3\" class=\""+MenuNaming.CSS_SEPARATOR+"\"><div>&nbsp;</div></td></tr>";
}
//bar
var BarNaming={
	CONTAINER_ID_PREFIX : '_bcidp_',
	CSS_IE : 'bar barie',
	CSS_FF : 'bar barff',
	CSS_SEPARATOR1:'separator1',
	CSS_SEPARATOR2:'separator2',
	CSS_OVER:'over',
	CSS_SELECTED:'selected'
}
var bh={
	selected : null,
	select : function(e){
		if(this.selected && this.selected!=e.target){
			this.unselect();
		}
		this.selected = e.target;
		this.selected.addClass(BarNaming.CSS_SELECTED);
		this.selected.removeClass(BarNaming.CSS_OVER);
		this.getNode(this.selected).writeMenu(e,this.selected);
	},
	unselect : function(){if(this.selected){this.selected.className='';mh.clearAll();}this.selected=null;},
	getNode : function(htmlNode){return th.all[htmlNode.id];},
	set : function(tree){var c=document.getElementById(BarNaming.CONTAINER_ID_PREFIX+tree.id);(new BarListener()).attachEvents(c);}
}
function BarListener(){
}
BarListener.prototype.attachEvents=function(c){
	$(c).getElements('a').addEvent("click", function(e){
		var node=bh.getNode(e.target);
		if(node && typeof node.action=="function"){bh.unselect();node.action();}
	});
	$(c).getElements('a').addEvent("mousedown", function(e){
		if(bh.selected!=e.target){
			bh.select(e);
		}else{
			bh.unselect();
			e.target.className=BarNaming.CSS_OVER;
		}
	});
	$(c).getElements('a').addEvent("mouseover", function(e){
		if(bh.selected){
			bh.select(e);
		}else{
			e.target.className=BarNaming.CSS_OVER;
		}
	});
	$(c).getElements('a').addEvent("mouseout", function(e){
		if(!bh.selected){
			e.target.className='';
		}
	});
}
Tree.prototype.writeBar=function(){
	var html="<table "+TreeSettings.containerAttribute+" id=\""+BarNaming.CONTAINER_ID_PREFIX+this.id+"\" class=\""+(Browser.ie?BarNaming.CSS_IE:BarNaming.CSS_FF)+"\" cellspacing=\"0\"><tr><td class=\"start\"></td>"+this.toBarHtml()+"<td>&nbsp;</td></tr></table>";
	document.write(html);
	bh.set(this);
}
Tree.prototype.toBarHtml=function(){
	var rows=[];
	for(var i=0; i < this.cs.length; i++){
		rows[i]=this.cs[i].getBarHtml();
	}
	return rows.join("");
}
Tree.prototype.getBarHtml=function(){
	var result ="<td style=\"width:1px;\">";
	if(this.css==BarNaming.CSS_SEPARATOR1 || this.css==BarNaming.CSS_SEPARATOR2){
		result+="<span class=\""+this.css+"\"></span>";
	}else{
		result+="<a id=\""+this.id+"\" unselectable=\"on\""+AA("title",this.title)+(this.attribute?" "+this.attribute:"");
		if(this.action && !(typeof this.action=="function")){
			var target=this.target || TreeSettings.defaultTarget ;	 
			result+=AA("href",this.action) + AA("target",target);
		}
		if(this.icon){
			result+=" style=\"background-image:url("+this.icon+");padding-left:24px;\"";	
		}
		result+=">"+this.getText()+"</a>";
	}
	result+="</td>";	
	return result;
}
//util
function setMenuEnabled(c,node,icon,method,valid){
	var htmlNode=$(c).getElement('#'+node.id);
	htmlNode.className=valid?'':MenuNaming.CSS_DISABLED;
	htmlNode.getElementsByTagName("IMG")[0].src=icon;
	node.action=method;
	node.valid=valid;
}
function setMenuSelected(c,node,css,method,selected){
	node.action=method;
	var htmlNode=$(c).getElement('#'+node.id);
	htmlNode.className=css;
	var checkedItem=htmlNode.cells[0].childNodes[0];
	checkedItem.src=TreeSettings.imagesPath+(selected?MenuSettings.selectIcon:TreeSettings.blankIcon);
}
function SearchEvent(){
	if(window.event) return new Event(window.event);
	//the following can't find event in ff
    var func=SearchEvent.caller;
    while(func!=null){
      var arg0=func.arguments[0];
      if(arg0 && arg0.constructor==Event) return arg0;
      func=func.caller;
    }
    return null;
}