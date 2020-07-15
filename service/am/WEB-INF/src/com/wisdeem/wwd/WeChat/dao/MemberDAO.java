package com.wisdeem.wwd.WeChat.dao;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.tools.DatabaseAccess;
import com.wisdeem.wwd.WeChat.beans.Member;
import com.wisdeem.wwd.WeChat.Utils;

/**
 *   说明:
 * 		会员操作
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */
public class MemberDAO {
	DB db=null;
	
	public MemberDAO(){
		try {
			db=DBFactory.getDB();
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据OpenID注册一个新会员	
	 *   根据OpenID注意一个新会员，如果会员昵称和密码自动分配
	 * @param openId  微信OPENID  
	 * @param token     公众帐号ID
	 * @return 注册成功返回此会员，失败返回null
	 */
	public Member registMember(String openId,String token){
		Member member=null;
		try {
			String queryOrgid="SELECT orgid FROM ws_public_accounts  WHERE token='"+token+"' ";
			MapList map=db.query(queryOrgid);
			String orgid="org";
			if(!Checker.isEmpty(map)){
				orgid=map.getRow(0).get("orgid");
			}
			String memberAccount=openId;
			String paswd=Utils.getRandomStr(5);
			Table table=new Table("wwd","WS_MEMBER" );
			TableRow tr=table.addInsertRow();
			tr.setValue("orgid", orgid);
			tr.setValue("wshop_name", memberAccount);
			tr.setValue("openid", openId);
			tr.setValue("group_id",1);
			tr.setValue("wshaop_password", DigestUtils.md5Hex(paswd));
			tr.setValue("data_status",1);
			db.save(table);
			member=new Member();
			member.setOpenid(openId);
			member.setAorg(orgid);
			member.setWshopName(memberAccount);
			member.setWshaopPassword(paswd);
		} catch (JDBCException e) {
			e.printStackTrace();
			return null;
		}
		return member;
	}
	
	/**
	 * 根据OPENID查找对应的会员
	 * @param openid  微信OPENID
	 * @param token  公众帐号ID
	 * @return  成功返回此会员，失败返回null
	 */
	public Member findMemberByOpenId(String openid,String token){
		Member member=null;
		try {
			
			String sql="SELECT a.orgid,a.wchat_account,m.*  			   "+
					"FROM ws_member AS m LEFT JOIN ws_public_accounts AS a "+
					"ON a.orgid=m.orgid                                    "+
					"WHERE m.openid='"+openid+"'                 "+
					"AND a.token='"+token+"'                 ";
			MapList map=db.query(sql);
			if(!Checker.isEmpty(map)){
				member=new Member(map.getRow(0));
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return member;
	}
	
	/**
	 * 根据OPENID和Publicid 返回对应会员的信息JSONArray格式数据
	 * @param openid
	 * @param token
	 * @return
	 */
	public JSONObject findMemberByIdToJSON(String openid,String token){
		String sql=
				"SELECT * FROM ws_member AS m LEFT JOIN       "+
				"	ws_public_accounts AS ac ON                  "+
				"	m.orgid=ac.orgid                             "+
				"	WHERE                                        "+
				"	ac.token='"+token+"' AND m.openid='"+openid+"' ";
		JSONArray jsoa=DatabaseAccess.query(sql);
		JSONObject result=null;
		try {
			if(jsoa!=null&&jsoa.length()>0){
				result=jsoa.getJSONObject(0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 根据公众帐号保存对应的会员信息
	 * @param member
	 * @param token
	 */
	public boolean saveMemberByPid(Member member,String token) {
		String orgid=Utils.getOrgId(token);
		Table table=new Table("wwd", "WS_MEMBER");
		TableRow row=table.addUpdateRow();
		if(member.getMemberCode()==null){
			return false;
		}
		row.setOldValue("member_code",member.getMemberCode());
		row.setValue("orgid",orgid);
//		row.setValue("wshop_name",member.getWshopName());
//		row.setValue("wshop_password",DigestUtils.md5Hex( member.getWshaopPassword()));
		row.setValue("nickname",member.getNickname());
		row.setValue("gender",member.getGender());
		row.setValue("age",member.getAge());
		row.setValue("openid",member.getOpenid());
		row.setValue("email",member.getEmail());
		row.setValue("phone",member.getPhone());
		row.setValue("postal_code",member.getPostalCode());
		row.setValue("hobby", member.getHobby());
		try {
			db.save(table);
		} catch (JDBCException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
