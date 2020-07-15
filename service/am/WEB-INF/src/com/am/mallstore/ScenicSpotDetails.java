package com.am.mallstore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.sdk.json.LoadDataToJson;
import com.am.sdk.json.TableList;
import com.p2p.service.IWebApiService;

/**
 *@author 作者：yangdong
 *@create 时间：2016年6月25日 下午3:51:11
 *@version 说明：景区详情数据
 */
public class ScenicSpotDetails implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String tId = request.getParameter("id");
		String mallClass = request.getParameter("mallclass"); 
		
		//店铺表
		String tSql = "SELECT id,store_name,addressdetil,comment,mainimgs,"
				+ "facility_details,qjzs,"
				+ "COALESCE(good_comments,0) AS good_comments,"
				+ "COALESCE(bad_comments,0) AS bad_comments,"
				+ "COALESCE(show_imgs,0) AS show_imgs,"
				+ "COALESCE(pjpxdf,0) AS pjpxdf "
				+ "FROM  mall_store WHERE id = '"+tId+"' ";
		TableList mallStoreTl=new TableList("MALL_STORE","id","",tSql,"");
		
		//评论
		tSql = "SELECT COUNT(*) FROM dynamiccomment WHERE praiseid = '[id]' ";
		TableList dynTable = new TableList("DYNAMICCOMMENT","id","",tSql,"");
		mallStoreTl.addChildTable(dynTable);
		
		//商品
//		tSql = "SELECT id,name,COALESCE(salenumber,0) AS salenumber,COALESCE(price,0) AS price,COALESCE(discount,1) AS discount,"
//				+ "scan_in,scan_in,supper_return,pcmainimages,mainimages,listimage,can_stack,validity_date_end,is_book, "
//				+ "breakfast,cancel,in_ensure,play,seat "
//				+ "FROM mall_commodity WHERE store = '[id]' AND commoditystate = '1' AND mall_class='"+mallClass+"'  ORDER BY createdate DESC ";
		tSql = " SELECT * FROM mall_commodity WHERE store = '[id]' AND commoditystate = '1' AND mall_class='"+mallClass+"'  ORDER BY COALESCE(recommendvalue,0) DESC";
		TableList comTable = new TableList("MALL_COMMODITY","id","",tSql,"");
		mallStoreTl.addChildTable(comTable);
		
		//酒店、演艺、跟团游类商品规格
		tSql = "SELECT to_char(price,'999999999990D99') AS price,* FROM mall_commodityspecifications WHERE commodityid = '[id]' AND dates >= to_char(now(),'yyyy-mm-dd')::date ORDER BY dates ASC" ;
		TableList mallCommTable = new TableList("MALL_COMMODITYSPECIFICATIONS","id","",tSql,"");
		comTable.addChildTable(mallCommTable);
		
		//景区、餐饮类商品规格
		tSql = "SELECT to_char(price,'999999999990D99') AS price,* FROM mall_commodityspecifications WHERE commodityid = '[id]'  ORDER BY dates DESC" ;
		TableList comSpeTable = new TableList("MALL_COMSPEC","id","",tSql,"");
		comTable.addChildTable(comSpeTable);
		
		//店铺介绍
		tSql = "SELECT id,classname,content,store_id,createtime FROM mall_storeinfo WHERE store_id = '[id]' ORDER BY createtime DESC ";
		TableList mallStoreInfoTable = new TableList("MALL_STOREINFO","id","",tSql,"");
		mallStoreTl.addChildTable(mallStoreInfoTable);
		
		//店铺图集
		tSql = "SELECT imagelist,classname  FROM mall_storeimagelist WHERE store_id= '[id]' ";
		TableList mallStoreListTable = new TableList("MALL_STOREIMAGELIST","id","",tSql,"");
		mallStoreTl.addChildTable(mallStoreListTable);
		
		//设置起始数据Sql语句
		tSql= "SELECT id,store_name,addressdetil,comment,mainimgs,"
				+ "facility_details,COALESCE(qjzs,0) AS qjzs,"
				+ "COALESCE(good_comments,0) AS good_comments,"
				+ "COALESCE(bad_comments,0) AS bad_comments,"
				+ "COALESCE(show_imgs,0) AS show_imgs,"
				+ "COALESCE(pjpxdf,0) AS pjpxdf,COALESCE(yscpzs,0) AS yscpzs,pjjg AS pjjg "
				+ "FROM  mall_store WHERE id = '"+tId+"' ";
		LoadDataToJson ldtj=new LoadDataToJson(mallStoreTl,tSql);
		
		
//		JSONObject resultObj = new JSONObject();
//		
//		try {
//			resultObj.put("DATA", ldtj.getJson());
//		} catch (JSONException e) {
//		
//			e.printStackTrace();
//		}
		
		System.out.println(ldtj.getJson().toString());
		return ldtj.getJson().toString();
	}

}
