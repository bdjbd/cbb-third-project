/***
*我的收藏选项卡
**/
var action={
	name : "am.plugins.web.collectionUnit"
	,head : function(ac)
	{
		
		//获取界面上下文信息
        var tUnitID=ac.UnitID;
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		var t=ac.Tool;
        
        //接收菜单参数
		var menucode = psm.getPageParamer(pageName).get("menu_code");
		var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
		var $Page=$(pageName);

		var params=psm.getPageParamer(pageName);
		var differentPage = ac.param.param.differentPage;//与手持端界面不同的详情界面
		var types = params.get("type_list").split(",");
		var type = types[0];
		var typeObj = {TYPE:type};
		//绑定监听
		$(document).off("click","#member_collection_car_ul");
		$(document).on("click","#member_collection_car_ul",function(e)
		{
			if($("#member_collection_car_ul li[active='true']")){
			typeObj.TYPE=$("#member_collection_car_ul li[active='true']").attr("data-am-value");
		}
		});

		
		//监听页面跳转
		$(document).off("click", "#am_frame_plugins_WEBmemberCollection_contentList_item");
		$(document).on("click", "#am_frame_plugins_WEBmemberCollection_contentList_item", function (e)
		{
			//alert();
			
			var thisPageName = params.get("page_name_" + typeObj.TYPE) + "";
			if(differentPage.indexOf(thisPageName)<= -1)
			{
				//alert(thisPageName);
				var p = psm.createMenuParameter();
				p.put("ID", $(this).attr("data-collectionid"));
				psm.toMenu(thisPageName, p);
			}else
			{
				//alert("web_" + thisPageName);
				var p = psm.createMenuParameter();
				p.put("ID", $(this).attr("data-collectionid"));
				psm.toMenu("web_" + thisPageName, p);
			}
		});
	}
	,show : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.web.ButtonUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.web.ButtonUnitt]远程方法 event v02...');
	}

}