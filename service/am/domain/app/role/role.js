function selectPurview(obj){
	var number=obj.value;
	var nodes = $$('input[name=_treecheck]');//所有选择框
	for(var i=0;i<nodes.length;i++){
	  if(nodes[i].checked){
		 var treeId = nodes[i].value;
		 var a = $(th.all[treeId].getHtmlNode()).getElement('a');
	//TODO：先把a.get('html')包含的括号部分去掉
		var menuName= a.get('html');
		
		var currentIndex=menuName.indexOf("(");
		//alert(menuName.substring(0,currentIndex));
		if(currentIndex!=-1){
			 a.set('html',menuName.substring(0,currentIndex));
			}
			//不可用权限
			if(number==0){
				 a.set('html',a.get('html')+'(不可用)');
			}
			//查看权限
			if(number==1){
				 a.set('html',a.get('html')+'(查看)');
			}
			//编辑权限
			if(number==2){
				 a.set('html',a.get('html')+'(编辑)');
			}
			//配置权限
			if(number==3){
				 a.set('html',a.get('html')+'(配置)');
			}
		}
	}

	var selectNodeId0='';
	var selectNodeId1='';
	var selectNodeId2='';
	var selectNodeId3='';
	for(var i=0;i<nodes.length;i++){
		 var treeId = nodes[i].value;
		 var a = $(th.all[treeId].getHtmlNode()).getElement('a');
		var menuName= a.get('html');
		var currentIndex0=menuName.indexOf("(不可用)");
		var currentIndex1=menuName.indexOf("(查看)");
		var currentIndex2=menuName.indexOf("(编辑)");
		var currentIndex3=menuName.indexOf("(配置)");
		if(currentIndex0!=-1)
		{
			selectNodeId0=selectNodeId0+treeId+',';
		}
		else if(currentIndex1!=-1)
		{
			selectNodeId1=selectNodeId1+treeId+',';
		}
		else if(currentIndex2!=-1)
		{
			selectNodeId2=selectNodeId2+treeId+',';
		}
		else if(currentIndex3!=-1)
		{
			selectNodeId3=selectNodeId3+treeId+',';
		}
	}
	document.getElementsByName("abdp_roleterminalmenu.form.p0")[0].value=selectNodeId0;
	document.getElementsByName("abdp_roleterminalmenu.form.p1")[0].value=selectNodeId1;
	document.getElementsByName("abdp_roleterminalmenu.form.p2")[0].value=selectNodeId2;
	document.getElementsByName("abdp_roleterminalmenu.form.p3")[0].value=selectNodeId3;
}