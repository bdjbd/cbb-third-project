var action={
	name : "am.plugins.web.HeadIMagUnit"
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
		var Croppic = ac.Croppic;
		//清空控件
		$("#am_member_headImagEdit_cropContainerEyecandy").empty();
		//初始上传控件
		var croppic=new Croppic({id:"am_member_headImagEdit_cropContainerEyecandy"
			,tagName:"headImag",outputUrlId:"am_member_headImagEdit_url"
			,onAfterImgCrop:function()
				{
					var sqlManager = new ac.SqlManager('am_member');
					var imgUrl = $("#am_member_headImagEdit_url").val();
					//alert(imgUrl);
					sqlManager.add('wxheadimg',imgUrl);
					var updateSql = sqlManager.getUpdate('ID',ac.SysConfig.ThisMember().ID);
					var action = ac.ActionManager;
					action.excUpdate($,updateSql,function()
					{
						//提示头像更换成功
					});
				}
			});
		
					
	}
	,show : function(ac)
	{
		
	}
	,event : function(ac)
	{
		
	}

}