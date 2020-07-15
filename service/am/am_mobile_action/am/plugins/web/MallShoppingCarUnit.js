/**
*购物车类别界面
**/

var deleteNopayMentTrting = function($,action,config)
{
    var deleteSQL = "delete from  mall_MemberOrder where isgroupsale='1'"
                + " and OrderState='1' and memberid='"
                + config.ThisMember().ID + "'";
   action.excUpdate($,deleteSQL,function(data)
   {
       if(Number(data.COUNT) > -1)
       {
           return true;
       }
   });
};

//初始化列表
var onloadList = function($,psm,p,commodIDs,config,HtmlList,action,cordova)
{
    deleteNopayMentTrting($,action,config);
    //id组合       
    //将合计设置为0
    $('#web_mall_MallShoppingCar_sum').html(0);
    //查询当前登录人的,订单状态为1的订单 1表示商品加入购物车
    var memberid = config.ThisMember().ID;
    var queryOrderSQL = "select mm.id,mm.CommodityID,mm.SalePrice,mc.Abstract,'"
        + config.ServerHost+"'||mc.listimage as MainImages,"
		+ "'" + config.ServerHost + "'||mc.pclistimage as pclistimage,"
		+ "mc.name,mm.SalePrice,mm.salenumber "
        + " from mall_Commodity mc "
        + "inner join mall_MemberOrder mm on mm.CommodityID=mc.ID "
        + "where mm.OrderState='1' and mm.MemberID='" + memberid + "'";
    var dbh = new  HtmlList($
        ,"web_mall_MallShoppingCar_list_list"
        ,queryOrderSQL,$("#web_mall_MallShoppingCar_list")
        ,0); 
              
    p.PreUpID=psm.getPageParamer("#plugins_mall_MallShoppingCar").get("menu_up_id");
    p.PrePageParammeter=psm.getPageParamer("#plugins_mall_MallShoppingCar");
    p.PrePageName="mall_shoppingcar";
    //对选中商品计算合计价格
    dbh.HtmlOverEvent = function($,data)
    {                
        $("input[name='web_mall_MallShoppingCar_check']").unbind('click');
        $("input[name='web_mall_MallShoppingCar_check']").bind('click',function()
        {
            var checkEle = $("input[name='web_mall_MallShoppingCar_check']:checked");
            var sumPrice = 0;         

            for(var i=0;i<checkEle.length;i++)
            {
                commodIDs.IDS += "'" +$(checkEle[i]).attr('data-id') + "'," ;
                var salenumber = $(checkEle[i]).attr('data-number');
                if(salenumber==undefined || salenumber==='')
                {
                    sumPrice += Number($(checkEle[i]).attr('data-value'));

                }else{
                    sumPrice += (Number($(checkEle[i]).attr('data-value'))*Number(salenumber));
                }

            }
             commodIDs.IDS += "''"          

            $('#web_mall_MallShoppingCar_sum').html(sumPrice);
        });

        //监听点击图片时间
        $(document).off("click","#web_mall_MallShoppingCar_img");
        $(document).on("click","#web_mall_MallShoppingCar_img",function(e)
        {
            var thisCommidId = $(this).attr("data-id");
            
            p.put("ID",thisCommidId);
            psm.toMenu("web_mall_details",p);
        });

        $(".delete_operation").unbind("click");
        $(".delete_operation").bind("click",function(e){

          var commodiId=$(this).attr("data-id");
          if(commodiId==="" ||commodiId==="''")
          {
              cordova.Popup($,'plugins_mall_MallShoppingCar','请选择要删除的商品');
          }else{
              cordova.Confim('','您确定删除所选商品?',function (buttonIndex)
              {
                  if(buttonIndex===1)
                  {
                      var deleteSQL = "delete from mall_MemberOrder where id in ('" + commodiId + "')";
                      action.excUpdate($,deleteSQL,function(data)
                      {
                          if(Number(data.COUNT)>0)
                          {
                              cordova.Popup($,'plugins_mall_MallShoppingCar','删除商品成功');
                              commodIDs.IDS = "";
                              onloadList($,psm,p,commodIDs,config,HtmlList,action,cordova);
                          }
                      });
                  }
              });
              
          }
        
        });


    };
    dbh.show(queryOrderSQL);    
    
}

var action={
	name : "am.plugins.web.MallShoppingCarUnit"
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
    var commodIDs = {"IDS":""};

    onloadList($,psm,p,commodIDs,config,HtmlList,action,cordova);// function($,psm,p,commodIDs,config,HtmlList,action)

    //结算
    $('#web_mall_MallShoppingCar_checkout').unbind('click');
    $('#web_mall_MallShoppingCar_checkout').bind('click',function()
    {
        t.debug("plugins_mall_MallShoppingCar 传递的id" + commodIDs);
        if(commodIDs.IDS==="" || commodIDs.IDS==="''")
        {
            cordova.Popup($,'plugins_mall_MallShoppingCar','请选择要结算的商品');
        }else
        {
           p.put("IDS",commodIDs.IDS);
           psm.toMenu("mall_placeOrder",p); 
        }

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