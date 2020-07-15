package com.am.content.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年7月4日
 *@version
 *说明：内容点赞WebApi
 */
public class ContentClickLikeWebApi implements IWebApiService{

		@Override
		public String execute(HttpServletRequest request,
				HttpServletResponse response) {

			DBManager db = new DBManager();
			JSONObject resultJson = null;
			try {
			//内容id
			String id = request.getParameter("id");

			//点赞
			String LikesNumber= "";
			
					String updateSQL=" UPDATE am_content SET "
							+ " like_number=(COALESCE(like_number,0)+1 ) WHERE id='"+id+"'";
					db.update(updateSQL);

					
					String selectSQL=" SELECT * FROM am_content WHERE id='"+id+"'  ";
					MapList map = db.query(selectSQL);
					if(!Checker.isEmpty(map)){
						LikesNumber=map.getRow(0).get("like_number");
					}
					resultJson = new JSONObject();
					if(!Checker.isEmpty(LikesNumber)){
						resultJson.put("LikesNumber", LikesNumber);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			
			return resultJson.toString();
		}
		


}
