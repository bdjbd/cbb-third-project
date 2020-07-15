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
 * @create 2016年5月30日
 * @version 
 * 说明:<br />
 * 删除社员关系
 * 
 */
public class SubmitDelInveMember implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//邀请码ID
		String invId=request.getParameter("invId");
		
		DB db=null;
		JSONObject result=new JSONObject();
		
		try{
			
			if(!Checker.isEmpty(invId)){
				db=DBFactory.newDB();
				
				MemberManager mManger=new MemberManager();
				
				mManger.delInverMember(db,invId);
				
				result.put("CODE", 0);
				result.put("MSG","删除成功");
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
