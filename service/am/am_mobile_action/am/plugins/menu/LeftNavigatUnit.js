var action={
    name:"am.plugins.menu.LeftNavigatUnit"
    ,head:function(ac)
    {    
    }
    ,show : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.menu.ADUnit]远程方法 shwo v02...');
	}
	,event : function(ac)
	{
		//ac.Tool.debug('我是[am.plugins.menu.ADUnit]远程方法 event v02...');
        //获取界面上下文信息
        var tUnitID=ac.UnitID;
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
        
        //接收菜单参数
		var menucode = psm.getPageParamer(pageName).get("menu_code");
		var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
		var upid = ac.param.param.upid;
		var p = psm.createMenuParameter();
		var cordova = ac.CordovaManager;
        //查询左侧导航菜单 data-role="left-Navigation" 暂时写死
        // var queryNavSQL =  "select menuname,menucode from am_mobliemenu where upid='" + upid + "'";
        psm.Menus.MenuJson($,upid,function(data,tThisMenu)
		{
			//ac.Tool.debug("leftUnit" + ac.Tool.jsonToStr(data.data));
			var len = data.size();     
			var entrys = new Array();     
			
			var rowNumber=0;
			for (var i = 0; i < len; i++) 
			{
				//菜单设置为显示时，才添加该菜单至数组
				if(data.getIndex(i).ISSHOW==="1")
				{
					entrys[rowNumber] =data.getIndex(i);
					rowNumber++;
				}
			}
			//ac.Tool.debug("leftUnit" + ac.Tool.jsonToStr(entrys));
			var list = {"DATA":entrys};
			var temHtml = template("webLeftNavigationUnit",list);
            var leftUnit = $(pageName+'left-Navigation');
            leftUnit.empty();
            leftUnit.append(temHtml).trigger('create');
            $(document).off("click",".webLeftNavigationUnit");
            $(document).on("click",".webLeftNavigationUnit",function(e)
            {
				var toMenuCode = $(this).attr("data-id");
				psm.toMenu(toMenuCode);
				// psm.changeMenu(toMenuCode);
            });
			//退出
			$(document).off("click","#leftUnitExitImag");
			$(document).on("click","#leftUnitExitImag",function(e)
			{
				cordova.Confim('','您确定要离开吗?',function (buttonIndex)
				{
					if(buttonIndex===1)
					{
						psm.toMenu("mall_activity");
						ac.SysConfig.LogOut();
						
					}
						//navigator.app.exitApp();
				});
			});

		});

        //左侧用户信息,"left-userInfUniu"
        var userInfUnit = $(pageName + 'left-userInfUniu');
        var userInfHtml = template("webLeftUserInf",ac.SysConfig.ThisMember());
        //ac.Tool.debug("远程加载userHtml=" + ac.Tool.jsonToStr(ac.SysConfig.ThisMember()));
        userInfUnit.empty();
        userInfUnit.append(ac.Tool.ReplaceImgPath(userInfHtml)).trigger('create');  
        if(undefined===ac.SysConfig.ThisMember().WXHEADIMG||ac.SysConfig.ThisMember().WXHEADIMG==='')
        {
            var img = $(pageName + 'left-userInfUniu').children("img.headImgStyle");
            //如果未上传头像，则设置默认图片
            if(ac.SysConfig.ThisMember().MEMBERSEX==='女')
            {
                
                img.attr("src","static/img/famalehead.jpg");
            }else
            {
                img.attr("src","static/img/malehead.jpg");
            }
        }
		//头像编辑
		$(document).off("click",".headImgStyle");
		$(document).on("click",".headImgStyle",function(e)
		{
			var param = psm.getPageParamer(pageName);
			psm.changePage("#am_member_headImagEdit",param);
		});
	}
}