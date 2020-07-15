package com.am.discount_group;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.sdk.json.LoadDataToJson;
import com.am.sdk.json.TableList;
import com.p2p.service.IWebApiService;

/**
 *@author 作者：王喜
 *@create 时间：2016年6月27日 16:58:29
 *@version 
 * 获取商品详细信息
 */
public class DiscountGroupDetailsInfo implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String tId = request.getParameter("id");
		
		//商品表
		String tSql = "SELECT mc.id,tlm.lable_name AS ztname,tl.lable_name AS zhname,mc.them,mc.group_label,mc.price,"
				+ "mc.pjpxdf,COALESCE(mc.qjzs,0) AS qjzs,"
				+ "mc.triph_comment,mc.mainimages,COALESCE(mc.discount,1)AS discount,mc.pcdetilimages,mc.name "
				+ " ,mc.show_imgs,mc.bad_comments,mc.good_comments,mc.every_sunday "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='套餐亮点') AS taocan "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='预订须知') AS yuding "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='线路特色') AS linefeatures "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='行程介绍') AS travelIntr "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='费用说明') AS costdes "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='旅行社') AS travelagency "
				+ "	FROM mall_commodity  AS mc "
				+ " LEFT JOIN triph_label_management AS tlm ON tlm.id = mc.them "
				+ " LEFT JOIN triph_label_management AS tl ON tl.id = mc.group_label WHERE mc.id = '"+tId+"' ";
		TableList mallCommodityT1=new TableList("MALL_COMMODITY","id","",tSql,"");
				
		//商品推荐
		tSql = "SELECT mc.mall_class,mc.name,mc.price,mc.id "
				+ " FROM mall_commodityrecommend AS com "
				+ " LEFT JOIN mall_commodity AS mc ON com.reccommodityid = mc.id "
				+ " WHERE com.thiscommodityid='[id]' AND mc.commoditystate = '1' ";
		TableList comTable = new TableList("MALL_COMMODITYRECOMMEND","id","",tSql,"");
		mallCommodityT1.addChildTable(comTable);
		
		//查询商品所有信息
		tSql = "SELECT * FROM mall_commodity WHERE id='[id]' ";
		TableList commodity = new TableList("COMMODITY_INFO","id","",tSql,"");
		mallCommodityT1.addChildTable(commodity);
		
		//商品图集
		tSql = "SELECT imagelist,classname  FROM mall_storeimagelist WHERE store_id= '[id]' ";
		TableList mallStoreListTable = new TableList("MALL_STOREIMAGELIST","id","",tSql,"");
		mallCommodityT1.addChildTable(mallStoreListTable);
		
		//查询商品规格价格
		tSql = "SELECT to_char(price,'999999999990D99')AS jiage,* FROM mall_commodityspecifications WHERE commodityid = '[id]' AND dates>=to_char(now(),'yyyy-mm-dd')::date ORDER BY dates" ;
		TableList mallCommTable = new TableList("MALL_COMMODITYSPECIFICATIONS","id","",tSql,"");
		mallCommodityT1.addChildTable(mallCommTable);
		
		//查询推荐商品现价
		tSql = "SELECT sum(mc.price)AS yuanjia FROM MALL_COMMODITYRECOMMEND AS com "
				+ " LEFT JOIN mall_commodity AS mc ON com.reccommodityid = mc.id WHERE com.thiscommodityid='[id]'";
		TableList yuanJiaTable = new TableList("YUANJIA","id","",tSql,"");
		mallCommodityT1.addChildTable(yuanJiaTable);
		
		//设置起始数据Sql语句
		tSql = "SELECT mc.id,tlm.lable_name AS ztname,tl.lable_name AS zhname,mc.them,mc.group_label,"
				+ "mc.price,COALESCE(mc.pjpxdf,0) AS pjpxdf,COALESCE(mc.qjzs,0) AS qjzs,mc.triph_comment,mc.mainimages,"
				+ "COALESCE(mc.discount,1)AS discount,mc.pcdetilimages,mc.name,mc.advance_days "
				+ " ,mc.show_imgs,mc.bad_comments,mc.good_comments,mc.every_sunday,ispostage,COALESCE(price,0) "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='套餐亮点') AS taocan "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='预订须知') AS yuding "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='线路特色') AS linefeatures "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='行程介绍') AS travelIntr "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='费用说明') AS costdes "
				+ " ,(SELECT content FROM mall_storeinfo WHERE store_id=mc.id AND classname='旅行社') AS travelagency "
				+ "	FROM mall_commodity  AS mc "
				+ " LEFT JOIN triph_label_management AS tlm ON tlm.id = mc.them "
				+ " LEFT JOIN triph_label_management AS tl ON tl.id = mc.group_label WHERE mc.id = '"+tId+"' ";
		LoadDataToJson ldtj=new LoadDataToJson(mallCommodityT1,tSql);
		
		return ldtj.getJson().toString();
	}

}
