var action={
	name : "am.plugins.web.mall.MallGoodListUnit"
	,head : function(ac)
	{
		
	}
	,show : function(ac)
	{
		
	}
	,event : function(ac)
	{
		//获取界面上下文信息
        var tUnitID=ac.UnitID;
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
        
        //接收菜单参数
		var menucode = psm.getPageParamer(pageName).get("menu_code");
		var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
		var $Page=$(pageName);
		var Croppic = ac.Croppic;
		//隐藏规格          plugins_mall_MallGoofList_Filter_div
		var filterDIV = $("#plugins_mall_MallGoofList_Filter_div");
		filterDIV.hide();
        
		$(document).off("click","#plugins_mall_MallGoofList_Filter_Open");
		$(document).on("click","#plugins_mall_MallGoofList_Filter_Open",function(e)
		{
			filterDIV.show();
		});
		$(document).off("click","#plugins_mall_MallGoofList_Filter_showClt");
		$(document).on("click","#plugins_mall_MallGoofList_Filter_showClt",function(e)
		{
			filterDIV.hide();
		});	
	}

}