package com.am.more.member_poll;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月22日
 *@version
 *说明：议题投票WebApi
 */
public class MemberPollIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//选项ID
		String  paramsOptions = request.getParameter("paramsOptions");
		//会员id
		String  memberId = request.getParameter("memberid");
		//议题id
		String  pollId = request.getParameter("pollId");
		
		paramsOptions=paramsOptions.replace("[", "");
		paramsOptions=paramsOptions.replace("]", "");
		paramsOptions=paramsOptions.replace("\"", "'");
		resultJson = new JSONObject();
    			//给投票人数表插一条数据
				inser(memberId,pollId,db);
				String updateSQL=" UPDATE mall_options SET "
	    				+ " votes=(votes+1) WHERE id IN ("+paramsOptions+") ";
				db.execute(updateSQL);
				resultJson.put("code", "0");
			} catch (Exception e) {
				try {
					resultJson.put("code", "999");
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
            
		return resultJson.toString();
	}

	/**
	 * 给投票人数表插一条数据
	 * @param memberId 会员id
	 * @param pollId 议题id
	 * @throws JDBCException 
	 */
	private void inser(String memberId, String pollId,DB db) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		String inserSQL=" INSERT INTO mall_poll_voted (id,mp_id,member_id,create_time) "
				+ " VALUES('" +uuid + "','" +pollId + "','"+memberId+"','now()' ) ";
		
		db.execute(inserSQL);
	}

}
