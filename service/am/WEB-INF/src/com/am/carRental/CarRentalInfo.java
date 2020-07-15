package com.am.carRental;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.sdk.json.LoadDataToJson;
import com.am.sdk.json.TableList;
import com.p2p.service.IWebApiService;

/**
 *@author 作者：王喜
 *@create 时间：2016年6月26日 11:34:22
 *@version 
 * 获取车型分类下车辆详细信息
 */
public class CarRentalInfo implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String tId = request.getParameter("id");
		
		//店铺表
		String tSql = "SELECT id,store_name,addressdetil,comment FROM  mall_store WHERE id = '"+tId+"' ";
		TableList mallStoreTl=new TableList("MALL_STORE","id","",tSql,"");
				
		//商品
		tSql = "SELECT  (SELECT count(id) FROM mall_commodity WHERE car_type=m.car_type) AS dityid,(SELECT store_name FROM mall_store where id=m.store)||'---'|| name AS carname,"
				+ "	id,name,salenumber,cancel,car_rental_type, price,COALESCE(m.discount,1)AS discount,COALESCE(m.pjpxdf,0)AS pjpxdf,COALESCE(m.qjzs,0)AS qjzs,recommendvalue,good_comments,bad_comments,show_imgs,m.* "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=m.id AND classname='预订须知') AS yudingxuzhi "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=m.id AND classname='车辆信息') AS cheliangxinxi "
				+ " FROM mall_commodity AS m WHERE m.car_type = '[id]' AND m.commoditystate = '1'  ORDER BY COALESCE(m.recommendvalue,-1) DESC ";
		TableList comTable = new TableList("MALL_COMMODITY","id","",tSql,"");
		mallStoreTl.addChildTable(comTable);
		
		//店铺图集
		tSql = "SELECT imagelist,classname  FROM mall_storeimagelist WHERE store_id= '[id]' ";
		TableList mallStoreListTable = new TableList("MALL_STOREIMAGELIST","id","",tSql,"");
		mallStoreTl.addChildTable(mallStoreListTable);
		
		//设置起始数据Sql语句
		tSql="SELECT id,store_name,addressdetil,comment,mainimgs,pjpxdf FROM  mall_store WHERE id = '"+tId+"' ";
		LoadDataToJson ldtj=new LoadDataToJson(mallStoreTl,tSql);
		
		return ldtj.getJson().toString();
	}

}
