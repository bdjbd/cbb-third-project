package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.member.MemberManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月3日
 * @version 
 * 说明:<br />
 * 成为生产者
 * 变成生产者是，输入内容为农场的组织机构编码
 */
public class ChangeMemberTypeWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		
		String userInvCodeMemberId=request.getParameter("memberid");
		String invCode=request.getParameter("invcode");
		String membrType=request.getParameter("memberType");
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			//1,更加邀请码查询社员邀请码信息
			//2,修改当前使用邀请码社员的额社员类型
			MemberManager mm=new MemberManager();
			
			if(!Checker.isEmpty(membrType)){
				result=mm.userInvCode(db, invCode, userInvCodeMemberId,membrType);
			}else{
				result=mm.userInvCode(db, invCode, userInvCodeMemberId);
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
		
		return result.toString();
	}

}
