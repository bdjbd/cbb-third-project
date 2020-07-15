package com.am.app_plugins_common.push;

import java.util.UUID;

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
 * 设置用户 信鸽推送token
 * @author mac
 *
 */
public class setUserPushToken implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		String memberid = request.getParameter("memberId");
		
		String token = request.getParameter("token");
		
		String mobile = request.getParameter("mobile");
		
		String sql = "SELECT * FROM mall_mobile_type_record WHERE 1=1 ";
		
		String usql = "";
		
		String isql ="";
		
		JSONObject json = new JSONObject();
		
		if(!Checker.isEmpty(token)){
			
			sql += " AND xtoken='"+token+"'";
			
		}
		
		//1 android 2 ios
		String types = "2";
		
		if("android".equals(mobile)){
			types = "1";
		}
		
		DB db = null;
		
		try {
			
			db =DBFactory.newDB();
			
			MapList list = db.query(sql);
			
			if(list.size()>0){
				
				if(!Checker.isEmpty(memberid)){
				
					for (int i = 0; i < list.size(); i++) {
						
							if("1".equals(list.getRow(i).get("mobile_type"))){	
								
								usql = "update mall_mobile_type_record set member_id='"+memberid+"' where id='"+list.getRow(i).get("id")+"'";
								db.execute(usql);
							
							}else if("2".equals(list.getRow(i).get("mobile_type"))){
								
								usql = "update mall_mobile_type_record set member_id='"+memberid+"' where id='"+list.getRow(i).get("id")+"'";
								db.execute(usql);
							}
						}
				}
				
			}else{
				
				isql = "insert into mall_mobile_type_record (id,xtoken,member_id,mobile_type,last_update_time,create_time)"
						+ " values('"+UUID.randomUUID()+"','"+token+"','"+memberid+"','"+types+"','now()','now()')";
				
				db.execute(isql);
			}
			
			json.put("code", "0");
			
		} catch (Exception e) {
			
			try {
				json.put("code", "999");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
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
		
		
		
		return null;
	}

}
