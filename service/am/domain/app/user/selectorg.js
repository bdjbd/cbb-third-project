function initRadio(){
	var unitId = $fnv("selectorg.uid");
	var valueId = $fnv("selectorg.vid");
	var nameId = $fnv("selectorg.nid");
	
	//取父页面的默认人员ID
	var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//取当前页面多选框
	var orgSel=$('u_u_selectorg.list').getElements('input[name=_s_selectorg.list]');
	//取当前页面的orgid
	var getIds=$('u_u_selectorg.list').getElements('input[name=selectorg.list.id]');
	
	//初始化人员选中状态
	if(set_id!="" && set_id.length>0){
		for(var i=0;i<orgSel.length;i++){
			if(set_id==getIds[i].value){
					orgSel[i].value = '1';
					orgSel[i].parentNode.childNodes[1].checked=true;
			}
		}
	}
}

function setOrg(){
	
	var unitId = $fnv("selectorg.uid");
	var valueId = $fnv("selectorg.vid");
	var nameId = $fnv("selectorg.nid");
	
	//取当前页面多选框
	var orgSel=$('u_u_selectorg.list').getElements('input[name=_s_selectorg.list]');
	//取当前页面的orgid
	var getIds=$('u_u_selectorg.list').getElements('input[name=selectorg.list.id]');
	var getNames=$('u_u_selectorg.list').getElements('input[name=selectorg.list.name]');
	
	var orgid="",orgname="";
	//需要赋值的部门ID 部门名称
	//var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//var set_name = $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value

	//获取选中的部门ID 部门名称
	for(var i=0;i<orgSel.length;i++){
		if(orgSel[i].value==1){
			orgid=getIds[i].value;
			orgname = getNames[i].value;
		}
	}
	
	//如果没有选择任何部门 设为空
	if(orgid.length==0){
		orgid="";
		orgname="";
	}

	 $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value= orgid;
     $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value = orgname;
	window.close();	
}

function setOrgs(){
	
	var unitId = $fnv("selectorgs.uid");
	var valueId = $fnv("selectorgs.vid");
	var nameId = $fnv("selectorgs.nid");

	//取当前页面多选框
	var orgSel=$('u_u_selectorgs.list').getElements('input[name=_s_selectorgs.list]');
	//取当前页面的orgid
	var getIds=$('u_u_selectorgs.list').getElements('input[name=selectorgs.list.id]');
	var getNames=$('u_u_selectorgs.list').getElements('input[name=selectorgs.list.name]');
	
	var orgid,orgname;
	var orgids="",orgnames="";
	//需要赋值的部门ID 部门名称
	//var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//var set_name = $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value

	//获取选中的部门ID 部门名称
	for(var i=0;i<orgSel.length;i++){
		if(orgSel[i].value==1){
			orgids+=getIds[i].value+",";
			orgnames+= getNames[i].value+",";
		}
	}
	
	//如果没有选择任何部门 则设置空
	if(orgids.length==0){
		orgid="";
		orgname="";
	}else{
		orgid = orgids.substr(0,orgids.length-1);
		orgname = orgnames.substr(0,orgnames.length-1);
	}
	 $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value= orgid;
     $(window.opener.document).getElement("input[name="+unitId+"."+nameId+"]").value = orgname;
	  window.close();	
}
function initCheckbox(){
	
	var unitId = $fnv("selectorgs.uid");
	var valueId = $fnv("selectorgs.vid");
	var nameId = $fnv("selectorgs.nid");
	//取父页面的默认人员ID
	var set_id = $(window.opener.document).getElement("input[name="+unitId+"."+valueId+"]").value
	//取当前页面多选框
	var orgSel=$('u_u_selectorgs.list').getElements('input[name=_s_selectorgs.list]');
	//取当前页面的orgid
	var getIds=$('u_u_selectorgs.list').getElements('input[name=selectorgs.list.id]');
	var es = new Array();
	//初始化人员选中状态
	if(set_id!="" && set_id.length>0){
	 es = set_id.split(",");
	 for(var j=0;j<es.length;j++){
		for(var i=0;i<orgSel.length;i++){
			if(es[j]==getIds[i].value){
					orgSel[i].value = '1';
					orgSel[i].parentNode.childNodes[1].checked=true;
				}
			}
		 }
	}
}
