/*商品套餐Unit
 */
var action={
	name : "am.plugins.web.CommodityGroupSale"
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
        var param = psm.getPageParamer(pageName);

        var p = psm.createMenuParameter();

        var param = psm.getPageParamer(pageName);

        var tempId=$unit.attr("data-temple-id");

        //商品ID
        var id = param.get('ID');

        //商品套餐
        var tryingComSQL = "select mca.title,mca.ID from mall_CommodityGroupSale mca "
                        +"left join mall_CommodityGroupsSaleSet mcs on mcs.GroupCommodityID=mca.GroupCommodityID "
                        +" where mca.ThisCommodityID='"
                        +id +"'"; 

        
        ajax.actionJSON($,action.GetGroupSaleComdityByCid(id),function(data){

            if(data==={}||!data.DATA){
                $unit.hide();
                return;
            }

            var tHtml=template(tempId,data);

            tHtml = ac.Tool.ReplaceImgPath(tHtml);
            $unit.html(tHtml).trigger("create");

            if(data.DATA.length<1){
                $unit.hide();
            }else{

                $unit.show();
                $(".group_commodity_ul").hide();
                $(".group_commodity_ul:first").show();

                $(document).off('click',".group_sale_title_ul li");
                $(document).on('click',".group_sale_title_ul li",function(e){

                    var groupId=$(this).attr("data-groupid");//groupid;
                    
                    //套装名称
                    $("#buy_group_name_div").html($(this).find("a").html());

                    $(".group_commodity_ul").hide();
                    $(".group_commodity_ul[data-groupid='"+groupId+"']").show();

                    $("#buy_group_btn").attr("data-groupid",groupId);
                
                });

                //绑定购买按钮事件
                $(document).off("click","#buy_group_btn");
                $(document).on("click","#buy_group_btn",function(){

                    var groutId=$(this).attr("data-groupid");

                    if(config.ThisMember()&&config.ThisMember().ID){

                        ajax.actionJSON($,action.addToShopCar("","",groutId,"TRUE"),function (data){
                            if(data.ERRCODE==0)
                            {
                                t.debug("回调值" + t.jsonToStr(data));
                                var p = psm.createMenuParameter();   
                                p.put("IDS",data.ORDERID);                        
                                psm.toMenu("mall_placeOrder",p);  
                            }
                        });
                    }else{
                        var p = psm.createMenuParameter();                      
                        psm.toMenu("am_frame_member_login",p);  
                    }

                });
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