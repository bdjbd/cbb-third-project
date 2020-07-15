/**
*关于更多左边侧边栏内容
**/

var action={
	name : "am.plugins.web.AbuotSideLeftUnit"
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
        var upid="b3ae0b9e-40b0-478a-a89b-6247495dbf37";
        var templateData={};
        var defaultId="";

        psm.Menus.MenuJson($,upid,function(data,tThisMenu){
            
            //templateData.title[templateData.title.length]=
            for(var i=0;i<data.keys.length;i++){
                var pmenu=data.data[data.keys[i]];
                var cmenus=data.data[data.keys[i]].CHILD_MENUS;
                
                var cdata={};
                cdata[i]={};
                cdata.title=pmenu['PAGETITLE'];

                var jj=0;
                for(var j=0;j<cmenus.length;j++){
                    var cmenu=cmenus[j];
                    if(cmenu&&cmenu['PAGETITLE']){
                        cdata[jj]={title:cmenu['PAGETITLE']};

                        var tempDefId="";
                        var isDefault=false;
                        for(var l=0;l<cmenu['MENU_PARAME'].length;l++){

                            if(cmenu['MENU_PARAME'][l]['PARAMNAME']==='id'){
                                cdata[jj].parmvalue=cmenu['MENU_PARAME'][l]['PARAMVALUE'];
                                tempDefId=cdata[jj].parmvalue;
                            }
                            if(cmenu['MENU_PARAME'][l]['PARAMNAME']==='default'){
                                isDefault=cmenu['MENU_PARAME'][l]['PARAMVALUE'];
                            }
                        }

                        if(isDefault){
                            defaultId=tempDefId;
                        }
                        jj++;
                    }
                }
                if(cdata){
                    templateData[i]=cdata;
                }
                
            }

            var tdat={};
            tdat.data=templateData;
            if(templateData){
                var tHtml=template(tempId,tdat);
                $unit.html(tHtml).trigger('create');

                $(document).off("click",".side_left_ul li");
                $(document).on("click",".side_left_ul li",function(){
                    var id=$(this).attr("data-content-id");
                    var p = psm.createMenuParameter();
                    p.put("ID",id);
                    ac.psm.toMenu("am_content_detail",p);
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