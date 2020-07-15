var container = $('_tc');
var u_config = $('u_u_'+_config_unit_id_);
var u_item = $('u_u_'+_item_unit_id_);
var all={};
var items;
var tree;
var selectedItem;
var _suffix =0;
function getId(){
	while(all['i'+_suffix]){
		_suffix++;
	}
	return 'i'+_suffix;
}
function showConfig(){
	setConfigVisible(true);
	u_item.setStyle('display','none');
	th.ae.unselect();
	unselectItem();
}
function setConfigVisible(visible){
	if(u_config){
		u_config.setStyle('display',visible?'block':'none');
	}
}
function selectItem(){
	var item = all[tree.getSelectedId()];
	if(selectedItem!=item){
		setConfigVisible(false);
		u_item.setStyle('display','block');
		unselectItem();
		item.show(true);
		selectedItem = item;
	}
}
function unselectItem(){
	if(selectedItem){
		selectedItem.show(false);
	}
	selectedItem = null;
}
function Item(obj){
	this.grid;
	this.tree;
	this.parentId;
	this.order;
	this.grid = _getGridWithoutId(obj);
	this.id = this.grid.all(_id_).value;
	if(!this.id){
		this.id = getId();
		this.grid.all(_id_).value=this.id;
	}
	this.tree = new Tree(this.id);
	this.tree.title=this.id;
	if(_icon){
		this.tree.icon=_icon;
	}
	this.tree.action = selectItem;
	if(TreeSettings.moveable){
		this.tree.attribute = 'oncontextmenu=showItemMenu()';
	}
	this.load();
}
var bigOrder=99999;
Item.prototype.load = function (){
	this.order = this.grid.all(_order_).value;
	if(!this.order){
		this.setOrder(bigOrder++);
	}
	this.parentId = this.grid.all(_parent_id_).value;
	var nameObj = this.grid.all(_name_);
	this.tree.text = getTreeText(this.id,nameObj);
}
Item.prototype.show = function (visible) {
	 this.grid.style.display=visible?"block":"none";
}
Item.prototype.setOrder = function (order) {
	this.order=order;
	this.grid.all(_order_).value=order;
}
Item.prototype.setParentId = function (parentId) {
	this.parentId = parentId;
	this.grid.all(_parent_id_).value=parentId;
}
Item.prototype.remove = function () {
	var operator = this.grid.all(_item_unit_id_+".");
	if(operator.value=="2" || operator.value=="0"){
		operator.value="3";
		this.show(false);
	}else if(operator.value=="1"){
		this.grid.removeNode(true);
	}
	showConfig();
}
//init-----------------------------
function loadItems(){
	_suffix =0;
	all={};
	selectedItem=null;
	items = new Array();
	var idArray = $$('input[name='+_id_+']');
	if(idArray.length==0){
		addRoot();
		idArray = $$('input[name='+_id_+']');
	}
	for(var i=0;i<idArray.length;i++){
		var item = new Item(idArray[i]);
		if(item.grid.all(_item_unit_id_+".").value!="3"){
			item.show(false);
			items.push(item);
			all[item.tree.id]=item;
		}
	}
	items.sort(sort);
}
function getTree(){
	var index=0;
	for(var i=0;i<items.length;i++){
		if(!items[i].parentId){
			index= i;
			break;
		}
	}
	return createTree(items,index);
}
function createTree(items,index) {
	var item = items[index];
	var tree = item.tree;
	for (var i = 0; i < items.length; i++) {
		if (items[i].parentId==item.id) {
			tree.add(createTree(items,i));
		}
	}
	return tree;
}
function writeTree() {
	tree.writeTree(container);
	smallOrder = 0;
	setOrder(tree);
}
u_item.setStyle('display','none');
loadItems();
tree = getTree();
writeTree();
function Listener() {
}
Listener.prototype.attachEvents = function () {
	container.attachEvent("onmousedown", this.onmousedown );
	container.attachEvent("oncontextmenu", this.oncontextmenu );
}
Listener.prototype.oncontextmenu = function () {
	window.event.returnValue = false;
}
Listener.prototype.onmousedown = function () {
	if(event.srcElement.tagName=="DIV"){
		showConfig();
	}
}
var listener = new Listener;
listener.attachEvents();

//----------
var smallOrder = 0;
function setOrder(tree){
	all[tree.id].setOrder(smallOrder++);
	if(tree.cs && tree.cs.length){
		for (var i = 0; i < tree.cs.length; i++) {
			setOrder(tree.cs[i]);
		}
	}
}
//--------------
function addRoot(){
	doFormAdd(_item_unit_id_);
	$(_id_).value = "root";
	$(_name_).value = "root";
}
function reloadItem(){
	var selected = tree.getSelectedTree();
	var item = all[selected.id];
	if(event.srcElement.name==_id_){
		var newParentId = event.srcElement.value;
		changeParentId(item.id,newParentId);
	}
	item.load();
	tree.writeTree();
	th.ae.select(selected.getHtmlNode());
}
function changeParentId(oldParentId,newParentId){
	for (var i = 0; i < items.length; i++) {
		if (items[i].parentId==oldParentId) {
			items[i].setParentId(newParentId);
		}
	}
}
function sort(i1,i2){
	return parseInt(i1.order)-parseInt(i2.order);
}
function getTreeText(id,nameObj){
	var name = nameObj?(nameObj.value || ""):"";	
	if(_showId && _showName){
		return name+" ("+id+")";
	}else if(_showName){
		return name?name:id;
	}else{
		return id;
	}
}
