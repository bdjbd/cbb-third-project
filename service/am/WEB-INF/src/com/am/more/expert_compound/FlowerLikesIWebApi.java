package com.am.more.expert_compound;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月19日
 *@version
 *说明：送花点赞WebApi
 */
public class FlowerLikesIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//内容id
		String id = request.getParameter("id");
		//字段
		String field = request.getParameter("field");
		//鲜花
		String flowerNumber= "";
		//点赞
		String LikesNumber= "";
		
				String updateSQL=" UPDATE mall_cope_article SET "
						+ " "+field+"=(COALESCE("+field+",0)+1 ) WHERE id='"+id+"'";
				db.execute(updateSQL);

				
				String selectSQL=" SELECT * FROM mall_cope_article WHERE id='"+id+"'  ";
				MapList map = db.query(selectSQL);
				if(!Checker.isEmpty(map)){
					flowerNumber=map.getRow(0).get("flower_number");
					LikesNumber=map.getRow(0).get("likes_number");
				}
				resultJson = new JSONObject();
				if(!Checker.isEmpty(flowerNumber)){
					resultJson.put("flowerNumber", flowerNumber);
					resultJson.put("LikesNumber", LikesNumber);
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			} catch (JSONException e) {
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
			
		return resultJson.toString();
	}
	

}
