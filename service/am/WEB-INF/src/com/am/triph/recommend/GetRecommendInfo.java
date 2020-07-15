package com.am.triph.recommend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class GetRecommendInfo implements IWebApiService
{
	
	private static double EARTH_RADIUS = 6378.137;

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{
		
		String id = request.getParameter("id");
		
		String gps = request.getParameter("gps");
		
		String type = request.getParameter("type");
		
		
		
		String [] arr = gps.split(",");
		
		String userX = arr[0];
		
		String userY = arr[1];
		
		DBManager db = new DBManager();
		
		String distance = "";
		
		JSONObject result = new JSONObject();
		
		JSONArray resultArr = new JSONArray();
		
		String sql = "";
		
		try
		{	
			
			if("7".equals(type))
			{
				
				sql ="select mrn.id as mid,mc.*,tlm.lable_name from mall_RecommendNearCommodity as mrn "
						+ "left join mall_commodity as mc on mc.id = mrn.near_commodity_id "
						+ "left join TRIPH_LABEL_MANAGEMENT as tlm on tlm.id = mc.group_label"
						+ " where mrn.store_id = '"+id+"' and mc.mall_class = '"+type+"'";		
				
				MapList list = db.query(sql);
				
				if(!Checker.isEmpty(list))
				{
					for (int i = 0; i < list.size(); i++) 
					{
						
						JSONObject jso = new JSONObject();
						
						jso.put("id",list.getRow(i).get("id"));
						//商品名称
						jso.put("name",list.getRow(i).get("name"));
						//折扣
						jso.put("discount",list.getRow(i).get("discount"));
						//销售数量
						jso.put("salenumber",list.getRow(i).get("salenumber"));
						//创建日期
						jso.put("createdate",list.getRow(i).get("createdate"));
						//状态
						jso.put("commoditystate",list.getRow(i).get("commoditystate"));
						//价格
						jso.put("price",list.getRow(i).get("price"));
						//标签
						jso.put("marks",list.getRow(i).get("marks"));
						//商城分类
						jso.put("mall_class",list.getRow(i).get("mall_class"));
						//所属店铺
						jso.put("store",list.getRow(i).get("store"));
						//剩余库存数量
						jso.put("current_store_number",list.getRow(i).get("current_store_number"));
						//价格标签
						jso.put("price_label",list.getRow(i).get("price_label"));
						//我要玩儿
						jso.put("my_play_label",list.getRow(i).get("my_play_label"));
						//天数标签
						jso.put("days_label",list.getRow(i).get("days_label"));
						//铁定成团
						jso.put("is_group",list.getRow(i).get("is_group"));
						//主题标签
						jso.put("them",list.getRow(i).get("them"));
						//组合标签
						jso.put("group_label",list.getRow(i).get("group_label"));
						//货架图片
						jso.put("shelf_image",list.getRow(i).get("shelf_image"));
						//标签名称
						jso.put("lable_name",list.getRow(i).get("lable_name"));
						//列表图
						jso.put("list_image",list.getRow(i).get("listimage"));
					
						resultArr.put(jso);

					}
					
					result.put("DATA", resultArr);
					
				}	
				
				
			}
			else if("1".equals(type))
			{
				//如果显示第一个则先查询两张表中是否存在数据
				String cmSql = "select count(*) from mall_RecommendNearStore where store_id='"+id+"'";
				String ccSql = "select count(*) from mall_RecommendNearCommodity where store_id='"+id+"'";
				
				MapList mlist = db.query(cmSql);
				MapList clist =db.query(ccSql);
				
				//判断数据是否存在
				if("0".equals(mlist.getRow(0).get("count"))&&"0".equals(clist.getRow(0).get("count")))
				{
					
					JSONObject json = new JSONObject();
					
					result.put("DATA", "0");
					
				}else
				{
					
					String ssql ="select mrn.id as mid,ms.*,tlm.lable_name from mall_RecommendNearStore as mrn ";
					ssql += "left join mall_store as ms on ms.id = mrn.near_store_id ";
					ssql += "left join TRIPH_LABEL_MANAGEMENT as tlm ";
					if("1".equals(type))
					{
						ssql += "on tlm.id = ms.ssjqdqid";
					}
					if("2".equals(type))
					{
						ssql += "on tlm.id = ms.wyw_id";
					}
					if("3".equals(type))
					{
						ssql += "on tlm.id = ms.wyc_id";
					}
					ssql += " where mrn.store_id = '"+id+"' and ms.mallclass_id = '"+type+"'";
			
					MapList list = db.query(ssql);
					
					if(!Checker.isEmpty(list))
					{
						for (int i = 0; i < list.size(); i++) 
						{
							
							JSONObject jso = new JSONObject();
							String x = list.getRow(i).get("longitud");
							String y = list.getRow(i).get("latitude");
							distance = getDistance(userX,userY,x,y);
							jso.put("distance", distance);
							jso.put("id",list.getRow(i).get("id"));
							//所属商城分类
							jso.put("mallclass_id",list.getRow(i).get("mallclass_id"));
							//名称
							jso.put("store_name",list.getRow(i).get("store_name"));
							//地址
							jso.put("addressdetil",list.getRow(i).get("addressdetil"));
							//价格
							jso.put("price",list.getRow(i).get("price"));
							//货架图片
							jso.put("shelf_image",list.getRow(i).get("shelf_image"));
							//平均价格
							jso.put("pjjg",list.getRow(i).get("pjjg"));
							//折扣
							jso.put("pjjg_zk",list.getRow(i).get("pjjg_zk"));
							//我要玩儿
							jso.put("wyw_id",list.getRow(i).get("wyw_id"));
							//我要住
							jso.put("wyz_id",list.getRow(i).get("wyz_id"));
							//我要吃
							jso.put("wyc_id",list.getRow(i).get("wyc_id"));
							//创建时间
							jso.put("createtime",list.getRow(i).get("createtime"));
							//已售产品总数
							jso.put("yscpzs",list.getRow(i).get("yscpzs"));
							//评价总数
							jso.put("qjzs",list.getRow(i).get("qjzs"));
							//平均评星分数
							jso.put("pjpxdf",list.getRow(i).get("pjpxdf"));
							//所属景区地区标签
							jso.put("ssjqdqid",list.getRow(i).get("ssjqdqid"));
							//标签名称
							jso.put("lable_name",list.getRow(i).get("lable_name"));
							//列表图
							jso.put("list_image",list.getRow(i).get("list_image"));
							
							resultArr.put(jso);
		
						}
						
						result.put("DATA", resultArr);
						
					}	
					
				}	
				
			}
			else{
				
				String ssql ="select mrn.id as mid,ms.*,tlm.lable_name from mall_RecommendNearStore as mrn ";
						ssql += "left join mall_store as ms on ms.id = mrn.near_store_id ";
						ssql += "left join TRIPH_LABEL_MANAGEMENT as tlm ";
						if("1".equals(type))
						{
							ssql += "on tlm.id = ms.ssjqdqid";
						}
						if("2".equals(type))
						{
							ssql += "on tlm.id = ms.wyw_id";
						}
						if("3".equals(type))
						{
							ssql += "on tlm.id = ms.wyc_id";
						}
						ssql += " where mrn.store_id = '"+id+"' and ms.mallclass_id = '"+type+"'";
				
				MapList list = db.query(ssql);
				
				if(!Checker.isEmpty(list))
				{
					for (int i = 0; i < list.size(); i++) 
					{
						
						JSONObject jso = new JSONObject();
						String x = list.getRow(i).get("longitud");
						String y = list.getRow(i).get("latitude");
						distance = getDistance(userX,userY,x,y);
						jso.put("distance", distance);
						jso.put("id",list.getRow(i).get("id"));
						//所属商城分类
						jso.put("mallclass_id",list.getRow(i).get("mallclass_id"));
						//名称
						jso.put("store_name",list.getRow(i).get("store_name"));
						//地址
						jso.put("addressdetil",list.getRow(i).get("addressdetil"));
						//价格
						jso.put("price",list.getRow(i).get("price"));
						//货架图片
						jso.put("shelf_image",list.getRow(i).get("shelf_image"));
						//平均价格
						jso.put("pjjg",list.getRow(i).get("pjjg"));
						//折扣
						jso.put("pjjg_zk",list.getRow(i).get("pjjg_zk"));
						//我要玩儿
						jso.put("wyw_id",list.getRow(i).get("wyw_id"));
						//我要住
						jso.put("wyz_id",list.getRow(i).get("wyz_id"));
						//我要吃
						jso.put("wyc_id",list.getRow(i).get("wyc_id"));
						//创建时间
						jso.put("createtime",list.getRow(i).get("createtime"));
						//已售产品总数
						jso.put("yscpzs",list.getRow(i).get("yscpzs"));
						//评价总数
						jso.put("qjzs",list.getRow(i).get("qjzs"));
						//平均评星分数
						jso.put("pjpxdf",list.getRow(i).get("pjpxdf"));
						//所属景区地区标签
						jso.put("ssjqdqid",list.getRow(i).get("ssjqdqid"));
						//标签名称
						jso.put("lable_name",list.getRow(i).get("lable_name"));
						//列表图
						jso.put("list_image",list.getRow(i).get("list_image"));
						
						resultArr.put(jso);

					}
					
					result.put("DATA", resultArr);
					
				}			
				
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

		return result.toString();
	}
	
	
	 /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     * @param lat1 用户经度
     * @param lng1 用户纬度
     * @param lat2 商家经度
     * @param lng2 商家纬度
     * @return
     */
    public  String getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
        Double lat1 = Double.parseDouble(lat1Str);
        Double lng1 = Double.parseDouble(lng1Str);
        Double lat2 = Double.parseDouble(lat2Str);
        Double lng2 = Double.parseDouble(lng2Str);
         
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(lng2);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2), 2)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10000;
        String distanceStr = distance+"";
        distanceStr = distanceStr.
            substring(0, distanceStr.indexOf("."));
         
        return distanceStr;
    }
    
    
    private double rad(double d) { 
        return d * Math.PI / 180.0; 
    }

}
