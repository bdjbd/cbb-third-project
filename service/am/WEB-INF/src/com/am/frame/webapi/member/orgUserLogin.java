package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.LoadMenus;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/** 
 * @author  作者：qiaozifeng
 * @date 创建时间：2017年4月26日 下午12:09:40
 * @explain 说明 : 
 */
public class orgUserLogin implements IWebApiService {

	
	final Logger logger = LoggerFactory.getLogger(orgUserLogin.class);
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String login_account = request.getParameter("login_account");
		String login_password = request.getParameter("login_password");
		
		String wx_open_id =  request.getParameter("wx_open_id");
		
		String orgid = null;
		String tCode="1";
		String tMsg="用户名或密码错误！";
		String tMemberData="";
		JSONObject json = new JSONObject();
		String selSql = "";
		
		DBManager db =new DBManager();
		
		String checkUserSQL = "SELECT * FROM auser WHERE userid = '"+login_account+"' "
				+ "AND password = '"+login_password+"' ";

		MapList ml = db.query(checkUserSQL);
		tMemberData = LoadMenus.loadTableDataOfSql(checkUserSQL);
		
		try {
		
			if(!Checker.isEmpty(ml)){
				orgid = ml.getRow(0).get("orgid"); 
				selSql = "SELECT aorg.* FROM aorg,auser WHERE "
						+ "aorg.orgid = auser.orgid and auser.userid = '"+login_account+"' "
						+ "and aorg.orgid = '"+orgid+"'";
				MapList aorg = db.query(selSql);
				
				if(!Checker.isEmpty(aorg)){
					
					String updateWxOpenIdSQL = "UPDATE auser SET wx_open_id='"+wx_open_id+"' WHERE userid='"+login_account+"'";
					db.execute(updateWxOpenIdSQL);
					json = getJson(aorg);
					JSONObject jsa = new JSONObject(tMemberData);
					jsa.put("member_info", json);
					tMemberData = jsa.toString();
				}
				
				
				tCode = "0";
				tMsg = "登陆成功！";
				
				return "{\"CODE\" : \"" + tCode + "\",\"MSG\" : \"" + tMsg + "\",\"MEMBER_DATA\" : " + tMemberData +"}";
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "{\"CODE\" : \"" + tCode + "\",\"MSG\" : \"" + tMsg + "\",\"MEMBER_DATA\" : \"\"}";
	}

	public static JSONObject getJson(MapList map) throws Exception{
		JSONObject json = new JSONObject();
			for(int i=0;i<map.size();i++)
			{
				for(int k = 0; k < map.getRow(i).size(); k++)
				{
					json.put(map.getKey(k),map.getRow(i).get(k));
				}
			}
		System.err.println(json);
		return json;
	}
}
