function initRadio(){
	var unitId = $fnv("selectuser.uid");
	var valueId = $fnv("selectuser.vid");
	var nameId = $fnv("selectuser.nid");
	
	//取父页面的默认人员ID
	var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value;
	//取当前页面多选框
	var userSel=$('u_u_selectuser.list').getElements('input[name=_s_selectuser.list]');
	//取当前页面的userid
	var getIds=$('u_u_selectuser.list').getElements('input[name=selectuser.list.id]');
	
	//初始化人员选中状态
	if(set_id!="" && set_id.length>0){
		for(var i=0;i<userSel.length;i++){
			if(set_id==getIds[i].value){
					userSel[i].value = '1';
					userSel[i].parentNode.childNodes[1].checked=true;
			}
		}
	}
}

function setUser(){	
	var unitId = $fnv("selectuser.uid");
	var valueId = $fnv("selectuser.vid");
	var nameId = $fnv("selectuser.nid");

	//取当前页面多选框
	var userSel=$('u_u_selectuser.list').getElements('input[name=_s_selectuser.list]');
	//取当前页面的userid
	var getIds=$('u_u_selectuser.list').getElements('input[name=selectuser.list.id]');
	var getNames=$('u_u_selectuser.list').getElements('input[name=selectuser.list.name]');
	
	var userid="",username="";
	//需要赋值的用户ID 用户名称
	//var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//var set_name = $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value

	//获取选中的用户ID 用户名称
	
		for(var i=0;i<userSel.length;i++){
			if(userSel[i].value==1){
				userid=getIds[i].value;
				username = getNames[i].value;
			}
		}

	//如果没有选择任何用户 则设空
	if(userid.length==0){
		userid="";
		username="";
	}

	 $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value= userid;
     $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value = username;
	window.close();	
}

function setUsers(){
	
	var unitId = $fnv("selectusers.uid");
	var valueId = $fnv("selectusers.vid");
	var nameId = $fnv("selectusers.nid");

	//取当前页面多选框
	var userSel=$('u_u_selectusers.list').getElements('input[name=_s_selectusers.list]');
	//取当前页面的userid
	var getIds=$('u_u_selectusers.list').getElements('input[name=selectusers.list.id]');
	var getNames=$('u_u_selectusers.list').getElements('input[name=selectusers.list.name]');
	
	var userid,username;
	var userids="",usernames="";
	//需要赋值的用户ID 用户名称
	//var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//var set_name = $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value

	//获取选中的用户ID 用户名称
	for(var i=0;i<userSel.length;i++){
		if(userSel[i].value==1){
			userids+=getIds[i].value+",";
			usernames+= getNames[i].value+",";
		}
	}
	
	//如果没有选择任何用户 则设置空
	if(userids.length==0){
		userid="";
		username="";
	}else{
		userid = userids.substr(0,userids.length-1);
		username = usernames.substr(0,usernames.length-1);
	}
	 $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value= userid;
     $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value = username;
	  window.close();	
}
function initCheckbox(){
	
	var unitId = $fnv("selectusers.uid");
	var valueId = $fnv("selectusers.vid");
	var nameId = $fnv("selectusers.nid");
	//取父页面的默认人员ID
	var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//取当前页面多选框
	var userSel=$('u_u_selectusers.list').getElements('input[name=_s_selectusers.list]');
	//取当前页面的userid
	var getIds=$('u_u_selectusers.list').getElements('input[name=selectusers.list.id]');
	var es = new Array();
	//初始化人员选中状态
	if(set_id!="" && set_id.length>0){
	 es = set_id.split(",");
	 for(var j=0;j<es.length;j++){
		for(var i=0;i<userSel.length;i++){
			if(es[j]==getIds[i].value){
					userSel[i].value = '1';
					userSel[i].parentNode.childNodes[1].checked=true;
				}
			}
		 }
	}
}