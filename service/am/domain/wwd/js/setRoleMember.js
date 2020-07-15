/**
*  功能：进行菜单相应信息的维护和操作
**/

var deleteIcon={ENABLED:'/common/images/tool/delete.png',DISABLED:'/common/images/tool/delete_d.png'};
var editIcon={ENABLED:'/common/images/tool/edit.png',DISABLED:'/common/images/tool/edit_d.png'}

var menu = new Tree('menu','menu');

menu.add(new Tree('m7','新增',addTerminalMenu,'/common/images/tool/add.png'));
var m8=menu.add(new Tree('m8','修改',editTerminalMenu,'/common/images/tool/edit.png'));
var m9=menu.add(new Tree('m9','删除',deleteTerminalMenu,'/common/images/tool/delete.png')); 


//点击事件
function setRole(){
  var id=th.ae.s.id;
  if(id=='1'){
      window.parent.document.getElement('iframe[id=goodsorts.frame.e2]').src='about:blank';
  }else{
      window.parent.document.getElement('iframe[id=goodsorts.frame.e2]').src='/wwd/ws_commodity.form.do?m=s&parent_id='+th.ae.s.id;
  }
}

function newcase(){
    window.parent.document.getElement('iframe[id=goodsorts.frame.e2]').src='/wwd/ws_commodity_edit.do?m=e&parent_id='+th.ae.s.id;
}

//添加终端菜单信息
function addTerminalMenu(){
     window.parent.document.getElement('iframe[id=goodsorts.frame.e2]').src='/wwd/ws_commodity_edit.do?m=a&parent_id='+th.ae.s.id;
}
//修改终端信息
function editTerminalMenu(){
     window.parent.document.getElement('iframe[id=goodsorts.frame.e2]').src='/wwd/ws_commodity_edit.do?m=e&parent_id='+th.ae.s.id;
}
//删除终端信息
function deleteTerminalMenu(){
	if (confirm("确定要删除'"+th.ae.s.innerText+"'吗？")){
		var id = th.ae.s.id;
		if(id == '1'){
		alert("此目录为固定目录，不可删除");
		return false;
		}
		var str = "/wwd/goodsorts.tree/delete.do?parent_id="+th.ae.s.id;
       doAjaxSubmit(str);
	}
}

function showMenu(){
	var c = menu.writeMenu();
	var selected = th.all[th.ae.s.id];
	//如果所选择为根节点，不可以编辑
	if(th.ae.s.id=='1'||selected.cs.length>0){
	    setMenuEnabled(c,m8,editIcon.ENABLED,null,false);
	}else{
	     setMenuEnabled(c,m8,editIcon.ENABLED,editTerminalMenu,true);
	}
	//如果该节点有子节点并且不是根节点，不可被删除
	if(!selected.p || selected.cs.length>0){
		
		setMenuEnabled(c,m9,deleteIcon.DISABLED,null,false);
		
	}else{
		setMenuEnabled(c,m9,deleteIcon.ENABLED,deleteTerminalMenu,true);
	}
}

