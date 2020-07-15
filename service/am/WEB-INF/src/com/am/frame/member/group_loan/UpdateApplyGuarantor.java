package com.am.frame.member.group_loan;

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
 *@create 2016年5月30日
 *@version
 *说明：
 */
public class UpdateApplyGuarantor implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject resultJson = new JSONObject();
		DB db=null;
		try {
			db = DBFactory.newDB();
		//联保申请ID
		String lpid = request.getParameter("lpid");
		//联保人ID
		String memberid = request.getParameter("memberid");
		//修改状态
		String lpgStatu = request.getParameter("lpgStatu");
		
			updateApplyGuarantor(db,lpid,memberid,lpgStatu);
			resultJson.put("code", "0");
			resultJson.put("msg", "成功");
		} catch (Exception e) {
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "失败!");
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

	private void updateApplyGuarantor(DB db, String lpid, String memberid,
			String lpgStatu) throws JDBCException {
		
		String updateSql = "UPDATE mall_loan_appy_guarantor SET lpg_status='"+lpgStatu+"' WHERE lp_id='"+lpid+"' AND member_id='"+memberid+"' ";
		
		db.execute(updateSql);
		
	}
	

}
