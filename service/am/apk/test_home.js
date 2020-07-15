

var action={
	name : "test1.action.home"
	,show : function(ac)
	{
		

		var tUnitID=ac.UnitID;
		var tItemName=tUnitID + ac.param.param.ItemName;

		ac.Tool.debug('test home v02... tUnitID=' + tUnitID + " | tItemName=" + tItemName);

		//测试DBHtmlListManager类
		var tSql=ac.param.param.SQL;
		var s=ac.psm.getPageParamer("#am_frame_home").get("menu_code");
		
		var dbhl=new ac.DBHtmlListManager(ac.$,"am_frame_home_dblist",tSql,ac.$("#" + tUnitID),0);
		dbhl.HtmlOverEvent=function($,data)
		{
			//首先移除该事件，然后增加事件，防止多次绑定
			$(document).off("click","#" + tItemName);
			$(document).on("click","#" + tItemName,function(e)
			{
				var p = ac.psm.createMenuParameter();
				
				ac.Tool.debug("" + tUnitID + "_item.field_id=" + $(this).attr("field_id"));
			});
		};
		dbhl.show(tSql);
	}
	,event : function(ac)
	{
		ac.Tool.debug('我是远程方法v02...');
	}

}