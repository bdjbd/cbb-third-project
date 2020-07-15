package com.am.mall.Activities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.sdk.json.LoadDataToJson;
import com.am.sdk.json.TableList;
import com.p2p.service.IWebApiService;

/**
 * 获取活动模板
 * @author mac
 *
 */
public class GetMallShelvesInfo implements IWebApiService{

	final Logger logger = LoggerFactory.getLogger(LoadDataToJson.class);
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{
		//类型
		String type = request.getParameter("type");
		
		//mall_activities	布局 主表
		//MALL_COMMODITYCLASS 商城分类 子表
		//MALL_COMMODITY 商品表
		//mall_commodityclassrelationship 商品分类映射表
		
		//货架布局表
		String tSql="select id,title"
				+ ",activitiestype"
				+ ",recommendvalue"
				+ ",commodityclassid"
				+ ",activitiesstate"
				+ ",page_type"
				+ ",content"
				+ ",startdate"
				+ ",enddate from MALL_ACTIVITIES "
				+ "where activitiesstate='1'"
				+ " and page_type = '"+type+"' order by recommendvalue desc ";
		String tTemplateSQL="";
		TableList mallActivitiesTl=new TableList("MALL_ACTIVITIES","id","",tSql,tTemplateSQL);
		
		//货架内容表-需要递归
		tSql="select id,title,upid,commodityclassstate,content_type,url,url_param from MALL_COMMODITYCLASS where id='[commodityclassid]'";
		tTemplateSQL="select id,title,upid,commodityclassstate,content_type,url,url_param from MALL_COMMODITYCLASS  where upid='[id]'";
		TableList mallCommdityClassTl=new TableList("MALL_COMMODITYCLASS","commodityclassid","id",tSql,tTemplateSQL);
		
		//商品表
		tSql="select commodityclassid"
				+ ",commodityid"
				+ ",b.salenumber"
				+ ",b.listimage"
				+ ",b.price"
				+ ",b.name"
				+ ",b.id"
				+ ",materialstypeid"
				+ ",ispostage"
				+ ",commoditystate"
				+ ",shop_id"
				+ ",mall_class"
				+ ",store"
				+ ",shelf_image from mall_commodityclassrelationship a,MALL_COMMODITY b where a.commodityid=b.id and a.commodityclassid='[id]'";
		tTemplateSQL="";
		TableList childCommodityTl=new TableList("ChildCommodity","id","",tSql,tTemplateSQL);
		
		//店铺表
//		tSql="select b.id,"
//				+ "a.id as sid"
//				+ ",mallclass_id"
//				+ ",price"
//				+ ",ssjqdqid"
//				+ ",shelf_image from mall_store a"
//				+ ",MALL_RECOMMENDNEARCOMMODITY b where a.id=b.store_id "
//				+ "and b.near_commodity_id='[id]'";
		tSql="select ms.id,ms.mallclass_id as mall_class,ms.price,ms.ssjqdqid,ms.shelf_image from mall_store as ms "
				+ " left join mall_recommendnearstore as mr on mr.near_store_id = ms.id"
				+ " left join mall_commodityclass as mct on mct.id = mr.store_id "
				+ " where mct.id = '[id]'";
		tTemplateSQL="";
		TableList childDpTl=new TableList("mall_store","id","",tSql,tTemplateSQL);
		
		
		//添加店铺表至货架内容表
		mallCommdityClassTl.addChildTable(childDpTl);
		
		//添加商品表至货架内容表
		mallCommdityClassTl.addChildTable(childCommodityTl);
		
		//添加货架内容表至货架布局表
		mallActivitiesTl.addChildTable(mallCommdityClassTl);
		
		//设置起始数据Sql语句
		tSql="select id,title"
				+ ",activitiestype"
				+ ",recommendvalue"
				+ ",commodityclassid"
				+ ",activitiesstate"
				+ ",page_type"
				+ ",content"
				+ ",startdate"
				+ ",enddate from MALL_ACTIVITIES "
				+ "where activitiesstate='1'"
				+ " and page_type = '"+type+"' order by recommendvalue desc ";
		LoadDataToJson ldtj=new LoadDataToJson(mallActivitiesTl,tSql);
		
		JSONObject resultObj = new JSONObject();
		
		try {
			resultObj.put("DATA", ldtj.getJson());
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
	
		logger.info(resultObj.toString());
		return resultObj.toString();
	}

}