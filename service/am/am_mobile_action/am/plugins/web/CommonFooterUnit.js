var action={

	name : "am.plugins.web.CommonFooterUnit"
	,head : function(ac)
	{
		
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		var config=ac.SysConfig;
		var action=ac.ActionManager;
		var t=ac.Tool;
		

		var str={};
		var tempId=ac.$Unit.attr("data-template-id");
		var tHtml=template(tempId,{});
			
		ac.$Unit.html(tHtml);
	}
	,show : function(ac)
	{
		// ac.Tool.debug('我是[am.plugins.web.CommonHeaderUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		var height=window.screen.height-(175+140);
		ac.$("div[data-role=content]").css("min-height",height+"px");
		
	}

}