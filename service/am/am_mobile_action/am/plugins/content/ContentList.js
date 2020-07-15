

var action={
	name : "am.plugins.content.ContentList"
	,show : function(ac)
	{
	
		var tUnitID=ac.UnitID;
		var pageName=ac.PageName;
		
		var menucode = ac.psm.getPageParamer(pageName).get("menu_code");
		var orgCode=ac.SysConfig.ThisOrgCode();		
		
		//设置界面元素 Name
		var UnitName="#" + tUnitID;
		var ListItemName="#" + tUnitID + "_list_item";
		
		var whereStr = " and orgcode='" + orgCode + "' ";
		if (menucode !== undefined) {
			whereStr += " and am_mobliemenuid = '" + menucode + "'";
		}

		var picSql = "SELECT id,am_mobliemenuid,title,abstract,count,detailed,consultationphone" 
			+ ",TO_CHAR(createdate,'yyyy-mm-dd') as createdate, '" + ac.SysConfig.ServerHost + "' || listimgage AS listimage, detailedimages "
			+ ",oldprice,nowprice "
			+ "FROM am_content where (1=1) " + whereStr + " ORDER BY sort DESC";

		var dbh2 = new ac.DBHtmlListManager(ac.$, tUnitID+"dbList", picSql, ac.$(UnitName), 0);

		//ac.Tool.debug("列表输出sql=" + picSql);

		dbh2.HtmlOverEvent = function ($, data) 
		{

			ac.Tool.debug("ListItemName:"+ListItemName);
			//首先移除该事件，然后增加事件，防止多次绑定
			ac.$(document).off("click", ListItemName);
			ac.$(document).on("click", ListItemName, function (e) 
			{
				var p = ac.psm.createMenuParameter();
                p.put("ID", ac.$(this).attr("field_id"));
				p.PreUpID=ac.psm.getPageParamer(pageName).get("menu_up_id");
				p.PrePageParammeter=ac.psm.getPageParamer(pageName);
                p.PrePageName=menucode;

				ac.Tool.debug('ID:'+ac.$(this).attr("field_id")
					+",preUpID:"+ac.psm.getPageParamer(pageName).get("menu_up_id")
					+",pageName:"+ac.psm.getPageParamer(pageName)+",menuCode:"+menucode);

                ac.psm.toMenu("am_content_detail",p);
			});
		};

		dbh2.show(picSql);

	}
	,event : function(ac)
	{
		//ac.Tool.debug('我是远程方法v02...');
	}

}