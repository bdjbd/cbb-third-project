function TreeNode(){
this.index="";
this.parent=null;
this.childs=new Array();
this.key=null;
this.value=null;
this.isChild=false;
}
TreeNode.prototype.getChilds=function(){
	if(this.childs.length<1){
		return false;
	}else{
		return this.childs;
	}
}
TreeNode.prototype.addChild=function(node){
	console.log("添加节点前数组长度:"+this.childs.length);
	if(node!=null&&node!='undefined'&&node.constructor===TreeNode){
		console.log("合法的子节点.\n Hehe!");
		this.childs.push(node);
	}else{
		console.log("不合法的子节点.\n opuss!");
	}
	console.log("添加节点后数组长度:"+this.childs.length);
}
TreeNode.prototype.removeChild=function(node){
	console.log("删除节点前数组长度:"+this.childs.length);
	if(this.childs.indexOf(node)>-1){
		console.log(this.childs.indexOf(node));
		console.log(this.childs.splice(this.childs.indexOf(node),this.childs.indexOf(node)));
	};
	console.log("删除节后前数组长度:"+this.childs.length);
}
var obj=[{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"55","CREATE_TIME":"","CLASS_NAME":"手机数码","PARENT_ID":"1","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"56","CREATE_TIME":"","CLASS_NAME":"图书音像","PARENT_ID":"1","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"57","CREATE_TIME":"","CLASS_NAME":"运动户外","PARENT_ID":"1","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"21","CREATE_TIME":"","CLASS_NAME":"珠宝饰品","PARENT_ID":"1","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"60","CREATE_TIME":"2014-01-09 14:52:45","CLASS_NAME":"android","PARENT_ID":"55","ORGID":"wwd_002","EXPLAIN":"android手机"},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"61","CREATE_TIME":"2014-01-09 14:52:56","CLASS_NAME":"IOS","PARENT_ID":"55","ORGID":"wwd_002","EXPLAIN":"IOS"},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"62","CREATE_TIME":"2014-01-09 14:53:15","CLASS_NAME":"索尼爱立信","PARENT_ID":"55","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"64","CREATE_TIME":"2014-01-09 17:16:13","CLASS_NAME":"戒指","PARENT_ID":"21","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"65","CREATE_TIME":"2014-01-09 17:16:25","CLASS_NAME":"耳环","PARENT_ID":"21","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"66","CREATE_TIME":"2014-01-09 17:16:33","CLASS_NAME":"手环","PARENT_ID":"21","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"67","CREATE_TIME":"2014-01-09 17:16:51","CLASS_NAME":"登上鞋","PARENT_ID":"57","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"68","CREATE_TIME":"2014-01-09 17:16:57","CLASS_NAME":"冲锋衣","PARENT_ID":"57","ORGID":"wwd_002","EXPLAIN":""},{"DATA_STATUS":"","LAST_MODIFY_TIME":"","COMDY_CLASS_ID":"69","CREATE_TIME":"2014-01-09 17:17:04","CLASS_NAME":"帐篷","PARENT_ID":"57","ORGID":"wwd_002","EXPLAIN":""}];
var tree=new Array();
function buileTree(data){
	data.forEach(function(item,index){
		var node=new TreeNode();
		node.index=index;
		node.key=item.COMDY_CLASS_ID;
		node.value=item.CLASS_NAME;
		node.parent=item.PARENT_ID;
		tree.push(node);
	});
	console.log(tree.length);
	tree.forEach(function(item,index){
		if(item.parent==1){
			tree.forEach(function(itemC,indexC){
				if(itemC.parent==item.key){
					item.addChild(itemC);
					itemC.isChild=true;
				}
			});
		}
	});
	tree.forEach(function(item,index){
		
		if(item.getChilds().length>-1){
			console.log("有子节点的父节点:"+item.value);
			$("#comdityClassListView").append("<li class=\"shopClassItemParent\" index=\""+
					item.key+"\" treeindex=\""+index+"\"><span >"+
					item.value+"</span></li>").listview("refresh");
			item.getChilds().forEach(function(ic,ind){
				console.log("子分类ID："+ic.key+" 子分类名称:"+ic.value);
			});
		}else{
			if(!item.isChild){
				console.log("没有子节点的父节点:"+item.value);
				$("#comdityClassListView").append("<li class=\"shopClassItemChild\" index=\""+
						item.key+"\"><span >"+
						item.value+"</span></li>").listview("refresh");
			}
		}
	});
	/*$("#comdityClassListView").append("<li class=\"shopClassItem\" index=\""+
			data[i].COMDY_CLASS_ID+"\"><span class=\"comdityClassItemStyle\">"+
			data[i].CLASS_NAME+"</span></li>").listview("refresh");
	*/
}
