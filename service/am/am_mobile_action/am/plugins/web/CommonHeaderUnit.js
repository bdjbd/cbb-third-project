	

var action={

	name : "am.plugins.web.CommonHeaderUnit"
	,head : function(ac)
	{
		
		var $=ac.$;
		var psm=ac.psm;
		var pageName=ac.PageName;
		var config=ac.SysConfig;
		var action=ac.ActionManager;
		var t=ac.Tool;
		var ajax=ac.AjaxManager;

		//1，获取菜单数据，
		var menusData={};
		
		ac.cm.MenuJson($,"1",function(data)
		{
			var str="";
			var count=0;
			var imgPath="",tArray;
			var menu_code=ac.psm.getPageParamer().get("menu_code");


			ajax.actionJSON($,action.GetMallClass(""),function(mallCalssData)
            {          
            	
            	//构建菜单模板需要的json对象
				str+="{\"list\" : [";

				for(var i=0;i<data.size();i++)
				{
					if(data.getIndex(i).ISSHOW==="1")
					{
						if(count>0)
							str+=",";
						
						imgPath=data.getIndex(i).MENUICON;
						if(menu_code===data.getIndex(i).MENUCODE)
						{
							//构建选择的图片
							tArray=imgPath.split(".");
							imgPath=tArray[0] + "_select." + tArray[1];

							//移出样式
							$('li#am_frame_FooterBtn').removeClass("active");
							$("li[menucode=\""+menu_code+"\"]").addClass("active");
					
						}
						
						str += "{\"MENU_CODE\" : \"" + data.getIndex(i).MENUCODE + "\"";
						str += ",\"IMG_PATH\" : \"" + imgPath + "\"";
						str += ",\"TITLE\" : \"" + data.getIndex(i).MENUNAME + "\"";
						str += ",\"MENU_PAGE_NAME\" : \"" + pageName + "\"";
						str += "}";
						count++;
					}
				}

				str+="],"+"\"MALLCLASS\":"+JSON.stringify(mallCalssData.DATA)+"}";

				var tempId=ac.$Unit.attr("data-template-id");

				var tHtml=template(tempId,t.strToJson(str));

				ac.$Unit.html(tHtml);
			
				//绑定商品分类菜单点击事件
				
                $(".mcate-item-bd a").off('click');
                $(".mcate-item-bd a").on('click',function(e){

                	var thridID =$(this).attr("data-id");
                    t.debug('跳转传参thridID='+thridID)
                    var p = psm.createMenuParameter();
                    p.put("ID", thridID);
                    p.PreUpID=psm.getPageParamer("#plugins_mall_MallClass").get("menu_up_id");
                    p.PrePageParammeter=psm.getPageParamer("#plugins_mall_MallClass");
                    p.PrePageName="mall_class";
                    psm.toMenu("mall_goodsList",p);
                });


				//$(".nav_menu_bar li:first").addClass("active");
				//绑定菜单事件
				$('.nav_menu_bar li').off().on('click', function(e) 
				{
					var thisMenuCode=psm.getPageParamer().get("menu_code");
					var MenuCode=$(this).attr("MenuCode");
					//alert(MenuCode);
					if(thisMenuCode!==MenuCode)
					{
						psm.toMenu(MenuCode);
					}
				});

				$(document).ready(function () {

					if(!ac.$Unit.find('#Z_TMAIL_SIDER_V2').attr("data-menu-init")){
						ac.$Unit.find('#Z_TMAIL_SIDER_V2').Z_TMAIL_SIDER_V2();
						ac.$Unit.find('#Z_TMAIL_SIDER_V2').attr("data-menu-init",true);
					}
					
               	});

				/**处理搜索功能**/
				ac.$Unit.find('#plugins_mall_activity_query_comhead').val("");
		        //绑定查询事件
		        ac.$Unit.find('#plugins_mall_activity_querybtn_comhead').unbind("click");
		        ac.$Unit.find('#plugins_mall_activity_querybtn_comhead').bind("click",function(){
		        	
		        	var mallName =ac.$Unit.find('#plugins_mall_activity_query_comhead').val();
		            if(mallName===''|| mallName==null )
		            {
		                return;
		            }
		            var p = psm.createMenuParameter(); 
		            p.put('name',mallName);
		           /* p.PreUpID=psm.getPageParamer("#plugins_mall_activity").get("menu_up_id");
		            p.PrePageParammeter=psm.getPageParamer("#plugins_mall_activity");*/
		            p.PrePageName="mall_activity";
		            psm.toMenu("mall_goodsList",p);
		        });

		        //检查是否登录
		        if(ac.SysConfig.ThisMember()){
		        	$(".userinfo_list li[data-menucode='am_frame_member_login'] span").html(ac.SysConfig.ThisMember().MEMBERNAME);
		        }
		        //绑定登录，我的宅可，购物车事件
		        $(".userinfo_list li").unbind("click");
		        $(".userinfo_list li").bind("click",function(){
		        	var menuCode=$(this).attr("data-menucode");
		        	//
		        	if(menuCode==='am_frame_member_login'&&ac.SysConfig.ThisMember()){//当为用户登录时，判断用户是否登录，如果已经登录，调整到我的宅可界面
		        		
	        			//已经登录，跳转到我的宅可---我的资料
	        			var p= psm.createMenuParameter();
	        			psm.toMenu("memberInf",p);

		        	}else{
		        		var p= psm.createMenuParameter();
		        		psm.toMenu(menuCode,p);
		        	}
		        	
		        });
				//给logo添加跳转时间跳转至首页
				$('#zk_index').unbind("click");
				$('#zk_index').bind('click',function()
				{
					window.location.href = config.ServerHost;
				});

            });
			
		},ac);

		//2，获取商品分类数据
		var shopClassData={};

		//获取模板
		
	}
	,show : function(ac)
	{
		// ac.Tool.debug('我是[am.plugins.web.CommonHeaderUnit]远程方法 shwo v02...');
		
	}
	,event : function(ac)
	{
	}

}