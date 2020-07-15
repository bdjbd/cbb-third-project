/*计算剩余时间函数
     * @param endTime 结束时
     * @param cid 控件id
     * @returns 剩余时间字符串和 剩余毫秒数
     */
    var getSurplusTime=function(endTime)
    {
        var rMillisecond = new Date(endTime)- new Date();//剩余毫秒数
        rMillisecond = Math.abs(rMillisecond);
        var rDay = parseInt(rMillisecond/1000/60/60/24,10);//剩余天数
        var rHour = parseInt(rMillisecond / 1000 / 60 / 60 % 24, 10);//计算剩余的小时数  
        var rMinute = parseInt(rMillisecond / 1000 / 60 % 60, 10);//计算剩余的分钟数  
        var rSecond = parseInt(rMillisecond / 1000 % 60, 10);//计算剩余的秒数  
        var remainingTime =  rDay + "天 " + rHour + "小时" + rMinute + "分钟 " + rSecond + "秒";
        return  {"TIME":remainingTime,"MILLIS":rMillisecond};
    };


var action={
	name : "am.plugins.web.GeneralStoreUnit"
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

        
	//普通卖场类
    ajax.actionJSON($,action.GetMallGeneralActivitiesCommodity(""),function(data){

      var tempId=$unit.attr("data-template-id");
      var html=template(tempId,data);
      html = ac.Tool.ReplaceImgPath(html);
      $unit.html(html).trigger("create");

      $(".mall_general_item").off("click");
      $(".mall_general_item").on("click",function(){

          var id=$(this).attr("data-commodity-id");

          var p = psm.createMenuParameter();

          p.put("ID",id);

          psm.toMenu("web_mall_details",p); 
      });

	//点击更多
	  $(".gereral_activity_unit").off("click");
      $(".gereral_activity_unit").on("click",function(){
		  var id=$(this).attr("data-class-id");

		  var p = psm.createMenuParameter();

          p.put("ID",id);

          psm.toMenu("mall_goodsList",p); 

	  });


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