

var action={
	name : "am.plugins.web.HeaderSearchUnit"
	,head : function(ac)
	{
		
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		var config=ac.SysConfig;
		var action=ac.ActionManager;
		var t=ac.Tool;

		$('#plugins_mall_activity_query').val("");
			
            //绑定查询事件
            $('#plugins_mall_activity_querybtn').unbind("click");
            $('#plugins_mall_activity_querybtn').bind("click",function(){
                
                var mallName = $('#plugins_mall_activity_query').val();
                if(mallName===''|| mallName==null )
                {
                    return;
                }
                var p = psm.createMenuParameter(); 
                p.put('name',mallName);
                p.PreUpID=psm.getPageParamer("#plugins_mall_activity").get("menu_up_id");
                p.PrePageParammeter=psm.getPageParamer("#plugins_mall_activity");
                p.PrePageName="mall_activity";
                psm.toMenu("mall_goodsList",p);
            });
			
	}
	,show : function(ac)
	{
		// ac.Tool.debug('我是[am.plugins.web.HeaderSearchUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		// ac.Tool.debug('我是[am.plugins.web.HeaderSearchUnit]远程方法 event v02...');
	}

}