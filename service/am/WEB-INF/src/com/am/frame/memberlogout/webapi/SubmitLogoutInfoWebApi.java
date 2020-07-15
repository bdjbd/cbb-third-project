package com.am.frame.memberlogout.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月14日
 * @version 
 * 说明:<br />
 * 社员注销原因
 */
public class SubmitLogoutInfoWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		
		try {
			db=DBFactory.newDB();
			
			//社员id
			String memberid=request.getParameter("memberid");
			//社员注销原因
			String reason=request.getParameter("reason");
			
			Table table=new Table("am_bdp","AM_MEMBER_LOGOUT");
			
			TableRow iTr=table.addInsertRow();
			
			iTr.setValue("memberid", memberid);
			iTr.setValue("reasons", reason);
			iTr.setValue("status", 1);
			
			db.save(table);
			
			
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
		
		return "";
	}

}
