<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="com.fastunit.jdbc.DB,com.fastunit.jdbc.DBFactory,com.fastunit.jdbc.exception.JDBCException" %>
<%@ page import="org.json.JSONArray,com.fastunit.MapList,com.fastunit.Page
,com.fastunit.Row,com.fastunit.context.ActionContext
,com.fastunit.jdbc.DB,com.fastunit.jdbc.DBFactory
,com.fastunit.jdbc.Type,com.fastunit.jdbc.exception.JDBCException,com.fastunit.support.PageInterceptor
,com.fastunit.util.Checker" %>
<%

System.out.println("数据测试");
    	DB db=null;
		try{
			//企业简介
			String intsText="";
			//介绍ID
			String id=null;
			
			//成员设置
			String storeId_01="";
			//组织活动设置
			String storeId_02="";
			
			String orgName="理性农业";
						
			db=DBFactory.newDB();
			
			//获取当前机构的几个ID
			String orgid=request.getParameter("orgid");



			String queryOrgSQL="SELECT * FROM aorg WHERE orgid=? ";

			MapList orgMap=db.query(queryOrgSQL, orgid, Type.VARCHAR);
			if(!Checker.isEmpty(orgMap)){
				orgName=orgMap.getRow(0).get("orgname");
			}

			String querySQL="SELECT * FROM am_org_introduce WHERE org_id=? ";
			MapList map=db.query(querySQL, orgid, Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				intsText=map.getRow(0).get("introduce_info");
				id=map.getRow(0).get("id");
				
				storeId_01=id+"_01";
				storeId_02=id+"_02";
			}
			
%>
<html style="width:100%;height:100%;">
    <head>
        <style>
            .inter{
                width:746px;
            }
            
            body {
                background:#fff;
                font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
                font-size: 14px;
                color:#000;
                margin: 0;
                padding: 0;
            }
            .swiper-container {
                width: 746px;
                height: auto;
            }
            .swiper-slide {
                /* text-align: center; */
                font-size: 18px;
                background: #fff;

            }
            .swiper-container .swiper-slide {
                height: 100%;
                line-height: 300px;
            }
            .swiper-container .swiper-slide:nth-child(2n) {
                height: 500px;
                line-height: 500px;
            }
    
    
        </style>
        <link rel="stylesheet" href="/common/Swiper/css/swiper.min.css">
		<style>
			#swiper-pagination-class1{
				    right: 10px;
					top: 50%;
					position: absolute;
					-webkit-transform: translate3d(0,-50%,0);
					-moz-transform: translate3d(0,-50%,0);
					-o-transform: translate(0,-50%);
					-ms-transform: translate3d(0,-50%,0);
					transform: translate3d(0,-50%,0);

			}
			#swiper-pagination-class1 span{
				margin: 5px 0;
				display: block;
			}
			#swiper-pagination-class2{
				    right: 10px;
					top: 50%;
					position: absolute;
					-webkit-transform: translate3d(0,-50%,0);
					-moz-transform: translate3d(0,-50%,0);
					-o-transform: translate(0,-50%);
					-ms-transform: translate3d(0,-50%,0);
					transform: translate3d(0,-50%,0);

			}
			#swiper-pagination-class2 span{
				margin: 5px 0;
				display: block;
			}
			.img_title{
					position:absolute;
					bottom: -22%;
					left: 50%;
					-webkit-transform: translate3d(-50%,0,0);
					-moz-transform: translate3d(-50%,0,0);
					-o-transform: translate(-50%,0);
					-ms-transform: translate3d(-50%,0,0);
					transform: translate3d(-50%,0,0);
					}
			    
		</style>
        <title>简介</title>
    </head>
    <body>
		<div style="text-align: center;width:100%;padding:7px;font-size:22px;"><%=orgName%></div>
        <div class="swiper-container swiper-container-horizontal " id="maiSwiperContent" style="height:92%;">
            <div class="swiper-wrapper" style="height:100%; transition-duration: 0ms; transform: translate3d(0px, 0px, 0px);width: 746px;">
                <div class="swiper-slide swiper-slide-active" style="width: 746px;line-height:20px;overflow-x: scroll;">
					<div style="padding-left:35px;padding-right:35px;">
						<%=intsText%>
					</div>
                </div>
                <!--class 1 start-->
                <div class="swiper-slide swiper-slide-active" style="width: 100%;height:100%;">
                    <div class="swiper-container swiper-container-v" id="intClass1">
                        <div class="swiper-wrapper" style="height:100%; transition-duration: 0ms; transform: translate3d(0px, 0px, 0px);">
                            <%
                            //2,获取成员设置数据集合
                            String queryUserSetSQL="SELECT * FROM mall_StoreImageList WHERE store_id=? ";
                            MapList userSetMap=db.query(queryUserSetSQL,storeId_01,Type.VARCHAR);
                            if(!Checker.isEmpty(userSetMap)){
                                    for(int i=0;i<userSetMap.size();i++){
                                            //id,classname,imagelist,store_id,cretetime
                                            //imagelist "[{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/0f13467c-c545-4fd2-ad2f-f08e5daeb4b4.png","name":"0f13467c-c545-4fd2-ad2f-f08e5daeb4b4.png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/0f13467c-c545-4fd2-ad2f-f08e5daeb4b4_(1).png","name":"0f13467c-c545-4fd2-ad2f-f08e5daeb4b4_(1).png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/50.png","name":"50.png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/83d3393e-e0cc-451c-a657-48ec0a289273.png","name":"83d3393e-e0cc-451c-a657-48ec0a289273.png"}]"
                                            Row row=userSetMap.getRow(i);
                                            String imagelist=row.get("imagelist");
                                            //分类名
                                            String className=row.get("classname");

                                            if(!Checker.isEmpty(imagelist)){
                                                    JSONArray imagelists=new JSONArray(imagelist);

                                                    for(int j=0;j<imagelists.length();j++){
                                                            //图片路径
                                                            String paths=imagelists.getJSONObject(j).getString("path");

                                                            %>
                                                             <div class="swiper-slide swiper-slide-active" style="width:100%;line-height:300px;">
                                                                 <img src="<%=paths%>" style="width:100%;height:100%"/>
																<span class="img_title"><%=className%></span>
                                                            </div>
                                                            <%
                                                    }
                                            }

                                    }
                            }
                            %>
                        </div>
                    </div>
                     <div id="swiper-pagination-class1" class="swiper-pagination swiper-pagination-v"></div>
                </div><!--class 1 end-->
                
               
                
                
                <!--class 2 start-->
                 <div class="swiper-slide swiper-slide-active" style="width: 100%;">
                    <div class="swiper-container swiper-container-v" id="intClass2">
                        <div class="swiper-wrapper" style="height:100%; transition-duration: 0ms; transform: translate3d(0px, 0px, 0px);">
                            
                            <%
                            //3，获取组织活动设置数据集合
                            MapList activitySetMap=db.query(queryUserSetSQL,storeId_02,Type.VARCHAR);
                            if(!Checker.isEmpty(activitySetMap)){
                                    for(int i=0;i<activitySetMap.size();i++){
                                            Row row=activitySetMap.getRow(i);
											
                                            String imagelist=row.get("imagelist");
                                            //分类名
                                            String className=row.get("classname");

                                            if(!Checker.isEmpty(imagelist)){
                                                    JSONArray imagelists=new JSONArray(imagelist);

                                                    for(int j=0;j<imagelists.length();j++){
                                                            //图片路径
                                                            String paths=imagelists.getJSONObject(j).getString("path");
														 %>
                                                       <div class="swiper-slide swiper-slide-active" style="width:100%;line-height:300px;">
															 <img src="<%=paths%>" style="width:100%;height:100%;"/>
															<span style="bottom: -23%;" class="img_title"><%=className%></span>
                                                       </div>      
														<%
                                                    }
                                            }
                                    }
                            }
                            %>
                        </div>
                    </div>
                    <!-- <div id="swiper-pagination-class2" class="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets"></div> -->
					<div id="swiper-pagination-class2" class="swiper-pagination swiper-pagination-v"></div>
                 </div>
                <!--class 2 end-->
            </div>
        <!-- Add Pagination -->
        <div id="swiper-pagination-main"  class="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets"></div>
        <!-- Add Pagination -->
        <div class="swiper-button-next"></div>
        <div class="swiper-button-prev swiper-button-disabled"></div>
        </div>
    </body>
    <%
    
	}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
    
    %>
    <script src="/downloadapp/jquery.min.js"></script>
    <script src="/common/Swiper/js/swiper.min.js"></script>
    <script src="/common/Swiper/js/swiper.min.js"></script>
    <script src="/common/Swiper/js/swiper.jquery.min.js"></script>
    
    <script>
    var swiper = new Swiper('#maiSwiperContent', {
        pagination: '#swiper-pagination-main',
        paginationClickable: true,
        nextButton: '.swiper-button-next',
        prevButton: '.swiper-button-prev',
        autoHeight: false, //enable auto height
    });
    var swiperIntClass1 = new Swiper('#intClass1', {
        pagination: '#swiper-pagination-class1',
        paginationClickable: true,
        direction: 'vertical'
    });
    var swiperIntClass2 = new Swiper('#intClass2', {
        pagination: '#swiper-pagination-class2',
        paginationClickable: true,
        direction: 'vertical'
    });
    </script>
    
    
</html>
