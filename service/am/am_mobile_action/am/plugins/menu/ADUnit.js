

var action={
	name : "am.plugins.menu.ADUnit"
	,head : function(ac)
	{
		//HtmlUnit ID
		var tUnitID=ac.UnitID;
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		
		//接收菜单参数
		var menucode = psm.getPageParamer(pageName).get("menu_code");
		var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
		var show_ad = psm.getPageParamer(pageName).get("show_ad");
		//广告详情界面菜单参数
		var showMenu = ac.psm.getPageParamer(pageName).get("show_menu_code");
		if(showMenu === undefined)
		{
			showMenu = "am_content_detail";
		}
		var adName="." + tUnitID;
		//设置界面元素 Name
		var UnitName="#" + tUnitID;
		var ListName="#" + tUnitID + "_list";
		var ListItemName="#" + tUnitID + "_list_item";

		if(show_ad==="true")
		{
			$(UnitName).show();
			//广告插件
			var whereStr = "";

			if (menucode !== undefined) 
			{
				whereStr = " and am_mobliemenuid = '" + menucode + "'";
			}
			var advertSql = "select ID,AM_MOBLIEMENUID,AM_CONTENTID,TITLE, '" + ac.SysConfig.ServerHost 
					+ "' || listimgage as LISTIMAGE from am_advert where (1=1) " + whereStr 
					+" AND orgcode= '"+ac.SysConfig.ThisOrgCode() +"' "
					+" ORDER BY sort DESC " ;
			//ac.Tool.error("  advertSql=" + advertSql);
			var dbhl = new ac.DBHtmlListManager($, tUnitID+"_dblist", advertSql, $(ListName), -1);
			dbhl.HtmlOverEvent = function ($, data) 
			{
				$(document).off("click", ListItemName);
				$(document).on("click", ListItemName, function (e) 
				{
					var p = psm.createMenuParameter();
					p.put("ID", $(this).attr("data-am_contentid"));
					p.PrePageName=menucode;
					p.PreUpID=MenuUpID;
					p.PrePageParammeter=psm.getPageParamer(pageName);
					psm.toMenu(showMenu,p);
				});
				//$(ListName).show();
				if($("#"+tUnitID).attr("data-adv_init")=="false"){
					
					$("#"+tUnitID).attr("data-adv_init","true");

					var swiperGallery=$("#"+tUnitID).swiper({
						pagination: '#'+tUnitID+'_pagination',
						paginationClickable: true,
						mode: 'horizontal',
						loop:true,
						autoplay: 10000,
						calculateHeight:true,
						updateFormElements:true
					});

				}
				
			};

			if($("#"+tUnitID).attr("data-adv_init")=="false"){
				dbhl.show(advertSql);
			}
		}
		else
			$(UnitName).hide();
		

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