package com.am.app_plugins_common.comment;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月20日
 *@version
 *说明：保存评论WebApi
 */
public class SaveCommentWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//上级评论id
		String mentid = request.getParameter("mentid");
		//技术栏目
		String contentid = request.getParameter("contentid");
		//会员id
		String memberid = request.getParameter("memberid");
		//上级会员id
		String upmemberid = request.getParameter("upmemberid");
		//内容
		String surveycontent = request.getParameter("surveycontent");
		
		
		resultJson = new JSONObject();
				//判断是评论还是回复
				if(!Checker.isEmpty(upmemberid)){
					//回复
					replyComment(surveycontent,mentid,contentid,memberid,db);
				}else{
					//评论
					SaveComment(surveycontent,contentid,memberid,db);
				}
			} catch (JDBCException e) {
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
	
	/**
	 * 评论
	 * @param surveycontent
	 * @param contentid
	 * @param memberid
	 * @param db
	 * @throws JDBCException 
	 */
	private void SaveComment(String surveycontent, String contentid,
			String memberid, DB db) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		String saveSQL=" INSERT INTO dynamiccomment (id,commentContent,praiseid,ContentId,create_time) "
				+ " VALUES('" +uuid + "','" +surveycontent + "','"+memberid+"','"+contentid+"','now()' ) ";
		
		db.execute(saveSQL);
		
	}
	/**
	 * 回复
	 * @param mentid  上级评论id
     * @param contentid  内容id
     * @param memberid 会员id
	 * @param db
	 * @throws JDBCException 
	 */
	private void replyComment(String surveycontent, String mentid, String contentid, String memberid,
			DB db) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		String replySQL=" INSERT INTO dynamiccomment (id,commentContent,praiseid,ContentId,tomemberid,create_time) "
				+ " VALUES('" +uuid + "','" +surveycontent + "','"+memberid+"','"+contentid+"','"+mentid+"','now()' ) ";
		
		db.execute(replySQL);
	}



}
