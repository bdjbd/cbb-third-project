package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.member.MemberManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年8月2日
 * @version 
 * 说明:<br />
 * 理性农业，用户普通邀请码初始化，如果有。
 */
public class InitMemberInvecode implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//1，删除所有用户的邀请码，这个步骤在数据库中执行
		//2，查询所有的用户
		//3，根据用户的手机号码生成对应的邀请码
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			//查询所有没有邀请码的社员
			String querySQL="SELECT * FROM am_member WHERE phone is not null "
					+ " AND id  NOT IN (SELECT am_memberid FROM am_MemberInvitationCode) ";
			
			MapList membarMap=db.query(querySQL);
			
			if(!Checker.isEmpty(membarMap)){
				MemberManager memberManager=new MemberManager();
				for(int i=0;i<membarMap.size();i++){
					//社员ID
					String memberId=membarMap.getRow(i).get("id");
					String mrole=membarMap.getRow(0).get("mrole");
					
					memberManager.createInvitationCode(db, memberId,mrole);
				}
			}
			
		}catch(Exception e){
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
