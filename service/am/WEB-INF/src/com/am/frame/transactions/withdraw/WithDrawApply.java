package com.am.frame.transactions.withdraw;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

public class WithDrawApply implements IWebApiService {

	
	/**
	 * 现金支付 操作
	 * outMemberid  出账用户id
	 * outAccountId 出账账户id
	 * inAccountId 入账账户id
	 * virementNumber 金额
	 * detail 说明
	 * account_type 1 手机端 2 后台
	 */
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		//出账用户id
		String outMemberid = request.getParameter("outMemberid");
		//出账账户
		String outAccountId = request.getParameter("outAccountId");
		//入账账户
		String inAccountId = request.getParameter("inAccountId");
		//提现金额
		String virementNumber = request.getParameter("virementNumber");
		//说明
		String detail = request.getParameter("detail");
		//account type
		String account_type = "1";
		
		WidthDrawManager widthDrawManager = new WidthDrawManager();
		
		JSONObject jsob = null;
		
		DB db = null;
		
		try {
			
			db = DBFactory.newDB();
			
			jsob = widthDrawManager.execute(db,outMemberid, outAccountId, inAccountId, virementNumber, account_type,detail);
			
			
			
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

		return jsob.toString();
	}
}
