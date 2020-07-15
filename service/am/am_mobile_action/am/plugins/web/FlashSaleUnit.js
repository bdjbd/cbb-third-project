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
	name : "am.plugins.web.FlashSaleUnit"
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

		// var p = psm.createMenuParameter(); 
  //       p.put('name',mallName);
  //       p.PreUpID=psm.getPageParamer("#plugins_mall_activity").get("menu_up_id");
  //       p.PrePageParammeter=psm.getPageParamer("#plugins_mall_activity");
  //       p.PrePageName="mall_activity";
  //       psm.toMenu("mall_goodsList",p);
		//当前登录人机构id var memberid = config.ThisMember().ID
        var queryMallSql = "SELECT id, title, content, activitiestype, recommendvalue, "
		            +"commodityclassid,createdate, to_char(startdate,'yyyy-MM-dd HH:mm:ss') as startdate, "
		            +"to_char(enddate,'yyyy-MM-dd HH:mm:ss') as enddate, orgcode, activitiesstate, "
		            +"menucode  FROM mall_activities "
		            +"WHERE ActivitiesState='1' and orgcode='"
		            + config.ThisOrgCode()
	            	+ "'  AND activitiestype='1'  ORDER BY createdate,RecommendValue DESC";

	    action.excQuery($,queryMallSql,function(ds)
            {
                var data=ds.DATA;
                var hltaList=new HtmlArray({$ : $
					,Name : "am_content_detail_ContentList_flashSale"
					,$Container : $("#am_plugins_web_FlashSaleUnit")	
					,ItemStartEvent : function($,row,templet)
					{
						console.error("row.CONTENT:\n"+row.CONTENT);
						var tContent=t.ReplaceImgPath(row.CONTENT);
						var result=t.ReplaceAll(templet,"#CONTENT#",tContent);
						console.error("result:\n"+result);
						return result;
					}
                    ,ItemEndEvent : function($,row)
                    {
                        var timeCid = 'plugins_mall_activity_remaining_flashSale_' + row.ID;
                        var divID = 'plugins_mall_activity_content_flashSale_' + row.ID;
                        var startTime = row.STARTDATE;
                        var endTime = row.ENDDATE;
                        //普通卖场类
                        if(row.ACTIVITIESTYPE==="0")
                        {
                            $(document).off("click", "#"+divID);
                            $(document).on("click", "#"+divID, function (e){                                    
                               //界面跳转至商品列表
                                var p = psm.createMenuParameter();                           
                                p.put("ID",row.ID);
                                p.PreUpID=psm.getPageParamer("#plugins_mall_activity").get("menu_up_id");
                                p.PrePageParammeter=psm.getPageParamer("#plugins_mall_activity");
                                p.PrePageName="mall_activity";
                                psm.toMenu("mall_ActitityDetails",p);                            
                            });
                        }else
                        {
                            if((new Date(endTime) - new Date())>0)
                            {
                                //判断活动是否开始
                                var rTime = new Date(startTime) - new Date();
                                //未开始
                                if(rTime>0)
                                {
                                    //t.debug("未开始" + rTime);
                                    var time = getSurplusTime(startTime);                                                
                                    $('#'+timeCid).html("距开始："+time.TIME);
                                    setInterval(function(){
                                        var time = getSurplusTime(startTime);                        
                                        //t.debug("-----"+time.TIME + "cid" + timeCid);
                                        $('#'+timeCid).html("距开始："+time.TIME);
                                     },1000); 
                                }
                                else
                                {
                                    var time = getSurplusTime(endTime);                                              
                                    $('#'+timeCid).html("距结束："+time.TIME);
                                    setInterval(function(){
                                        var time = getSurplusTime(endTime);                        
                                        //t.debug("-----"+time.TIME + "cid" + timeCid);
                                        $('#'+timeCid).html("距结束："+time.TIME);
                                    },1000); 

                                    $(document).off("click", "#"+divID);
                                    $(document).on("click", "#"+divID, function (e){                                    
                                       //界面跳转至商品列表
                                        var p = psm.createMenuParameter();                           
                                        p.put("ID",row.ID);
                                        p.PreUpID=psm.getPageParamer("#plugins_mall_activity").get("menu_up_id");
                                        p.PrePageParammeter=psm.getPageParamer("#plugins_mall_activity");
                                        p.PrePageName="mall_activity";
                                        psm.toMenu("mall_ActitityDetails",p);                            
                                    });
                                } 

                            }else
                            {
                                $('#'+timeCid).html("活动已经结束");
                                $(document).off("click", "#"+divID);
                                $(document).on("click", "#"+divID, function (e){                                    
                                   //界面跳转至商品列表
                                   cordova.Popup($,'plugins_mall_activity','活动已经结束');                       
                                });
                                
                            }
                        }
                        
                    }
				});
                hltaList.show(data);

            });


	    //head function end
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