/**
*内容详情
**/

//更新浏览次数
function updateBrowseTime($,contentID,ajax,action){
    //amount=(amount-COALESCE("+counts+",0))
    var updateSQL="UPDATE am_content SET count=COALESCE(count,0)+1 WHERE id='"+contentID+"' ";
    ajax.actionJSON($, action.Update(updateSQL), function () {});
}

var action={
	name : "am.plugins.web.ContentDetailsUnit"
	,head : function(ac)
	{
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		var config=ac.SysConfig;
		var action=ac.ActionManager;
		var t=ac.Tool;
		var HtmlArray=ac.HtmlArray;
		var cordova=ac.CordovaManager;
	    var HtmlList=ac.DBHtmlListManager;
	    var unitId=ac.UnitID;
	    var $unit=ac.$Unit;
	    var ajax=ac.AjaxManager;

	    var MenuUpID = psm.getPageParamer(pageName).get("menu_up_id");
	    var menucode = psm.getPageParamer(pageName).get("menu_code");
	    var tempId=$unit.attr("data-template-id");
	    var param = psm.getPageParamer(pageName);

	    var p = psm.createMenuParameter(); 

	    //获取内容ID
	    var ID =p.get("ID");//内容ID

	    var contentSql = "select ID,am_mobliemenuid,TITLE,ABSTRACT,COUNT,DETAILED,CONSULTATIONPHONE"
			+ ",to_char(CREATEDATE,'yyyy-mm-dd') AS CREATEDATE, '" + config.ServerHost + "' || listimgage as LISTIMAGE"
			+ ", DETAILEDIMAGES from am_content where ID = '" + ID + "'";
		
		//设置content——body的min-height的高度
		var height=window.screen.height-(175+140+100);
		ac.$(".content_contents_body").css("min-height",height+"px");
			
		action.excQuery($,contentSql,function(ds)
		{
			var data=ds.DATA;
			//t.debug("  ClickEvent() data=" + t.jsonToStr(data));
			
			var hltaList=new HtmlArray({$ : $
				,Name : "am_content_detail_ContentList"
				,$Container : $("#am_content_detail_Content")
				,ItemStartEvent : function($,row,templet)
				{
					var detailImgs = row.DETAILEDIMAGES.split(",")
						,tHtml=""
						,tTemplet="";
					for(var i=0;i<detailImgs.length;i++)
					{
						tHtml += "<img str='" + config.ServerHost + detailImgs[i] + "' width='100%'/>";
					}
					
					tTemplet=templet.replace(new RegExp("#DETAILEDIMAGES#", "g"), tHtml);
					tTemplet=tTemplet.replace(new RegExp("#DETAILED#", "g"), t.ReplaceImgPath(row.DETAILED));
					
					return tTemplet;
				}
			});
			hltaList.show(data);
			
			var hltaFooter=new HtmlArray({$ : $
				,Name : "am_content_detail_AcceptFooter"
				,$Container : $("#am_content_detail_Accept")
			});
			hltaFooter.show(data);
		});
			
		$(document).off("click", "#am_content_detail_collection");
		$(document).on("click", "#am_content_detail_collection", function (e)
		{
			if (t.isEmpty(config.ThisMember())) {
				cordova.Popup($, "am_content_detail", "请先登录");
			}
			else {
				var actionData = action.MemberCollection(config.ThisMember().ID, "content", ID);
				ajax.actionJSON($, actionData, function (data) {
					t.print(t.jsonToStr(data) + (data.CODE!=="2"));
					if(data.CODE!=="2")
					{
						cordova.Popup($, "am_content_detail", data.MSG);
					}else if(data.CODE==="2")
					{
						cordova.Popup($, "am_content_detail", "您的网络不良，请重试！");
					}
					
				});
			}
		});
            
            //更新浏览次数
        updateBrowseTime($,ID,ajax,action);

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