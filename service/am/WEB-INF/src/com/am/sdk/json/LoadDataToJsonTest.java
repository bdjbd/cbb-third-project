package com.am.sdk.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

public class LoadDataToJsonTest implements IWebApiService 
{
	
	// http://127.0.0.1:8080/AmRes/com.am.sdk.json.LoadDataToJsonTest.do
	// http://127.0.0.1:8080/AmRes/com.am.mall.Activities.GetMallShelvesInfo.do?type=1
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) 
	{
//		String tSql="select * from am_mobliemenu where upid='[id]'";
//		String tTemplateSQL="select * from am_mobliemenu where upid='[id]'";
//		TableList mainTL=new TableList("am_mobliemenu","id","id",tSql,tTemplateSQL);
//		
//		tSql="select * from am_menuparame where am_mobliemenuid='[id]'";
//		tTemplateSQL="select * from am_menuparame where am_mobliemenuid='[id]'";
//		TableList childTL=new TableList("am_menuparame","id","",tSql,tTemplateSQL);
//		
//		
//		mainTL.addChildTable(childTL);
//		
//		tSql="select * from am_mobliemenu where upid='1'";
//		LoadDataToJson ldtj=new LoadDataToJson(mainTL,tSql);
		
		/*
		 * //获取布局
			select * from MALL_ACTIVITIES where page_type = '1'

			//字表 递归
			select * from MALL_COMMODITYCLASS as mcc where id=[commodityclassid] and upid=[id]
			
			select * from mall_commodityclassrelationship a,MALL_COMMODITY b where a.commodityid=b.id and a.commodityclassid=[id]
		 * */
		
		//货架布局表
		String tSql="select * from MALL_ACTIVITIES where page_type = '1'";
		String tTemplateSQL="";
		TableList mallActivitiesTl=new TableList("MALL_ACTIVITIES","id","",tSql,tTemplateSQL);
		
		//货架内容表-需要递归
		tSql="select * from MALL_COMMODITYCLASS as mcc where id='[commodityclassid]'";
		tTemplateSQL="select * from MALL_COMMODITYCLASS as mcc where upid='[id]'";
		TableList mallCommdityClassTl=new TableList("MALL_COMMODITYCLASS","commodityclassid","id",tSql,tTemplateSQL);
		
		//商品表
		tSql="select * from mall_commodityclassrelationship a,MALL_COMMODITY b where a.commodityid=b.id and a.commodityclassid='[id]'";
		tTemplateSQL="";
		TableList childCommodityTl=new TableList("ChildCommodity","id","",tSql,tTemplateSQL);
		
		//店铺表
		tSql="select * from mall_store a,MALL_RECOMMENDNEARCOMMODITY b where a.id=b.store_id and b.near_commodity_id='[id]'";
		tTemplateSQL="";
		TableList childDpTl=new TableList("ChildMallStore","id","",tSql,tTemplateSQL);
		
		
		//添加店铺表至货架内容表
		mallCommdityClassTl.addChildTable(childDpTl);
		
		//添加商品表至货架内容表
		mallCommdityClassTl.addChildTable(childCommodityTl);
		
		//添加货架内容表至货架布局表
		mallActivitiesTl.addChildTable(mallCommdityClassTl);
		
		//设置起始数据Sql语句
		tSql="select * from MALL_ACTIVITIES where page_type = '1'";
		LoadDataToJson ldtj=new LoadDataToJson(mallActivitiesTl,tSql);
		
		 
		
		
		
		return ldtj.getJson();
	}

}
