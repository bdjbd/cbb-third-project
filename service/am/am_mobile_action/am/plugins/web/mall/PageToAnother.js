/**
* 界面跳转
* @param am-data-cltid 需要监控的控件id
* @param am-topage 跳转的页面Menucode
* @param am-paramKey 传递参数的key
* @param am-paramVal 传递参数值(触发跳转控件的属性名称)
*/

var action={
	name : "am.plugins.web.mall.PageToAnother"
	,head : function(ac)
	{	
		var tUnitID=ac.UnitID;
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
        
        //接收菜单参数
		var menucode = psm.getPageParamer(pageName).get("menu_code");
		var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
		var $Page=$(pageName);
		var cltid = "#" + ac.$Unit.attr("am-data-cltid");
		var changeMenuCode = ac.$Unit.attr("am-topage");
		var paramKey = ac.$Unit.attr("am-paramKey")
		var cltAttr = ac.$Unit.attr("am-paramVal")
		//界面跳转
		$(document).off("click",cltid);
		$(document).on("click",cltid,function(e)
		{
			var p = psm.createMenuParameter();
			//alert(paramKey + "," +  cltAttr);
			if(paramKey!==undefined)
			{
				var paramVal = $(this).attr(cltAttr);
				p.put(paramKey,paramVal);
			}
			p.PrePageName=menucode;
			p.PreUpID=MenuUpID;
			p.PrePageParammeter=psm.getPageParamer(pageName);
			psm.toMenu(changeMenuCode,p); 
		});
	}
	,show : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.menu.ADUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.menu.ADUnit]远程方法 event v02...');
	}

}