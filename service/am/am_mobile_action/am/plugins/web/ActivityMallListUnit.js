/**
*活动商品列表Action
**/
var action={
	name : "am.plugins.web.ActivityMallListUnit"
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

    //活动ID
    var activityId=param.get('ID');        
    ajax.actionJSON($,action.GetActivityMallListData(activityId),function(data){
      var html=template(tempId,data);
      html = ac.Tool.ReplaceImgPath(html);
      $unit.html(html).trigger("create");

      $(".activity_mall_item_template_item").off("click");
      $(".activity_mall_item_template_item").on("click",function(){

          var id=$(this).attr("data-commodity-id");
          var p = psm.createMenuParameter();
          
          p.put("ID",id);
          p.PreUpID=MenuUpID;
          p.PrePageName=menucode;
          p.PrePageParammeter=psm.getPageParamer(pageName);

          psm.toMenu("web_mall_details",p); //plugins_mall_ActitityDetails
      });
    
    });

	}
	,show : function(ac)
	{
		
	}
	,event : function(ac)
	{
		
	}

}