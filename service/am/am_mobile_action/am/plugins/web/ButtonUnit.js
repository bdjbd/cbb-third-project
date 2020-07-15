/**
* 原来右键配置的跳转现在配置为 button
* 配置属性 btn-role='action'
* 配置属性 to-page 值为要跳转到的页面 menuCode
* 配置属性 am-data-buntype 跳转监控的按钮的类型
*/

var action={
	name : "am.plugins.web.ButtonUnit"
	,head : function(ac)
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
		var btnType = ac.$Unit.attr("am-data-buntype");
		var btnClt
		if(btnType===undefined)
		{
			btnClt = $Page.find("button[btn-role='action']");
		}else
		{
			btnClt = $Page.find(btnType + "[btn-role='action']");
		}
		    
        var toPage = btnClt.attr("to-page");
		var btnID = btnClt.attr("id");
		ac.Tool.debug('我是[am.plugins.web.ButtonUnit]远程方法' + toPage + btnID);
		if(btnID==='' || btnID===undefined)
		{
            return;
        }else
        {
            $(document).off("click","#" + btnID);
            $(document).on("click","#" + btnID,function(e)
            {
				var p = psm.createMenuParameter();
                p.PrePageName=menucode;
                p.PreUpID=MenuUpID;
                p.PrePageParammeter=psm.getPageParamer(pageName);
                psm.toMenu(toPage,p); 
            });
        }
			
	}
	,show : function(ac)
	{
		ac.Tool.debug('我是[am.plugins.web.ButtonUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		ac.Tool.debug('我是[am.plugins.web.ButtonUnitt]远程方法 event v02...');
	}

}