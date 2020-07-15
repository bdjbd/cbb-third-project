var _config_unit_id_ = "t";
var _item_unit_id_ = "i";

var _id_ = "i.id";
var _parent_id_ = "i.pid";
var _order_ = "i.o";
var _name_ = "i.n";//choice

_showId = false;

TreeSettings.containerAttribute="style='height:500px;width:300px;border:buttonface 1px solid;background-color:#ffffcc;overflow:auto;'";

function preview(){
	var treeId = $fnv('t.tid');
	var view = $('tree.preview');
	view.src='/dev/tree.preview.do?treeid='+treeId;
}
