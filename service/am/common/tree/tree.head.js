var deleteIcon={ENABLED:'/common/images/tool/remove.png',DISABLED:'/common/images/tool/remove_d.png'};
function move(){
	var item = all[tree.getSelectedId()];
	var parentItem = all[item.tree.p.id];
	item.setParentId(parentItem.id);
	smallOrder=0;
	setOrder(tree);
}
TreeSettings.containerAttribute="style='height:560px;width:300px;border:buttonface 1px solid;background-color:#ffffcc;'";//overflow:auto;
TreeSettings.moveAfterInterceptor=move;
TreeSettings.moveable=true;
TreeSettings.addible=true;

var _icon = null;
var _showId = true;
var _showName = true;
var _action = null;
var _context = null;
var _mode = null;//操作模式

//--------------------
function add(){
	var selected = tree.getSelectedTree();
	selected.setOpen(true);
	var parentId = all[selected.id].id;
	doFormAdd(_item_unit_id_);
	loadItems();
	var last = items[items.length-1];
	last.setParentId(parentId);
	tree = getTree();
	writeTree();
	th.ae.select(last.tree.getHtmlNode());
	selectItem();
}
function remove(){
	var selected = tree.getSelectedTree();
	selected.removeAndRewrite();
	var item = all[selected.id];
	item.remove();
}
var itemMenu = new Tree('im0','im');
itemMenu.add(new Tree('im1','新增',add,'/common/images/tool/add.png'));
var im2 =new Tree('im2','删除',remove,'/common/images/tool/remove.png');
itemMenu.add(im2);
function showItemMenu(){
	var c = itemMenu.writeMenu();
	var selected = th.all[th.ae.s.id];
	if(!selected.p || selected.cs.length>0){
		setMenuEnabled(c,im2,deleteIcon.DISABLED,null,false);
	}else{
		setMenuEnabled(c,im2,deleteIcon.ENABLED,remove,true);
	}
	window.event.returnValue = false;
}
