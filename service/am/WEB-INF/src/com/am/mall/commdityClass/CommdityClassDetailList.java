package com.am.mall.commdityClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年3月30日 下午6:28:33 
 * @version 1.0   
 */
public class CommdityClassDetailList implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String  oneClassId = request.getParameter("id");
		
		//1 活动 2 普通分类
		String type = request.getParameter("type");
		
		DB db = null;
		
		return getClassDetialAllList(type,oneClassId,db).toString();
	}
	
	
	/**
	 * 查询该活动下的商品分类，罗列如果有子类罗列子类，子类下有商品罗列商品，若无子类罗列该分类
	 * @param db
	 * @return
	 */
	public JSONObject getClassDetialAllList(String type,String classId,DB db){
		
		
		
		String sql = "select commodityclassid from mall_activities  where id='"+classId+"'";
		
		JSONObject listJson = null;
 		
		try {
			db = DBFactory.newDB();
			
//			if("1".equals(type)){
				
				MapList list = db.query(sql);
				
				
				if(list.size()>0){
					
					String ssql = "select count(*) as count from mall_CommodityClass "
							+ "where upid='"+ list.getRow(0).get("commodityclassid") + "'";
					
					MapList hasechildList = db.query(ssql);
					
					if("0".equals(hasechildList.getRow(0).get("count"))){
						
						listJson = new JSONObject();
						listJson.put("classState", "0");
						listJson.put("classid", classId);
						listJson.put("msg", "没有子分类");
					
					}else{
						
						String queryClassSQL = "select ID,title,ListImage"
	                            +" from mall_CommodityClass where upid='"
	                            + list.getRow(0).get("commodityclassid") +"'";
						
						listJson = new JSONObject();
						listJson.put("classState", "999");
						listJson.put("classid", list.getRow(0).get("commodityclassid"));
						listJson.put("msg", "有子分类");
						
					}
				}
				
//			}else{
//				
//				String ssql = "select count(*) as count from mall_CommodityClass "
//						+ "where upid='"+ classId + "'";
//				
//				MapList hasechildList = db.query(ssql);
//				
//				if("0".equals(hasechildList.getRow(0).get("count"))){
//					
//					listJson = new JSONObject();
//					listJson.put("classState", "0");
//					listJson.put("classid", classId);
//					listJson.put("msg", "没有子分类");
//				
//				}else{
//					
//					String queryClassSQL = "select ID,title,ListImage"
//                            +" from mall_CommodityClass where upid='"
//                            + classId +"'";
//					
//					listJson = new JSONObject();
//					listJson.put("classState", "999");
//					listJson.put("classid", classId);
//					listJson.put("msg", "有子分类");
//					
//				}
//				
//			}
			
		} catch (Exception e) {
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
		return listJson;
	}

}
