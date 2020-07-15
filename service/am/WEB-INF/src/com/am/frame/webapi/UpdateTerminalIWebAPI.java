package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年12月31日
 * @version 
 * 说明:<br />
 * 用户终端登录更新
 * 参数：
 * {
	userId : e.userId
	,channelId : e.channelId
	,appid : e.appid
	,requestId : e.requestId
	,errorCode : e.errorCode
	,deviceType : config.DevicePlatform()
	}
 * 
 */
public class UpdateTerminalIWebAPI implements IWebApiService {

	
	/**检查userid是否存在在数据库中**/
	private String checkeSQL="SELECT id,bduserid FROM am_terminal WHERE bduserid=? AND orgcode=?";
	
	/**inser SQL**/
	private String inserSQL="INSERT INTO am_terminal( "+
            " id, bduserid, bdchannelid, appid, "+
            " devicetype, lastlogintime, createtime,orgcode)"+
            " VALUES ("+
            " uuid_generate_v4(), ?, ?, ?, "+
            " ?, now(),now(),?)";
	
	/**update SQL**/
	private String  updateSQL="UPDATE am_terminal SET lastlogintime=now() WHERE bduserid=? AND orgcode=? ";
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		try {
			
			JSONObject reqParam=new JSONObject(request.getParameter("DATA"));
			
			String userId=reqParam.getString("USERID");
			String orgCode=reqParam.getString("ORG");
			
			db=DBFactory.newDB();
			
			MapList map=db.query(checkeSQL,
					new String[]{userId,orgCode},
					new int[]{Type.VARCHAR,Type.VARCHAR});
			
			//检查数据是否存在
			if(!Checker.isEmpty(map)){
				//存在，更新数据库中的值。
				db.execute(updateSQL,
						new String[]{userId,orgCode},
						new int[]{Type.VARCHAR,Type.VARCHAR});
			}else{
				//不存在，给数据库中插入新数据。
				db.execute(inserSQL,
						new String[]{
						userId,reqParam.getString("CHANNELID"),reqParam.getString("APPID"),
						reqParam.getString("DEVICETYPE"),reqParam.getString("ORG")},
						new int[]{
						Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
						Type.VARCHAR,Type.VARCHAR
				});
			}
			
			
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
		return "{}";
	}

}
