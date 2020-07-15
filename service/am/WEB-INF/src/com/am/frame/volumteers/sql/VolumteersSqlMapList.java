package com.am.frame.volumteers.sql;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.Checker;

/**
 * 获取志愿者支付缴纳信用保证金记录
 * @author mac
 *
 */
public class VolumteersSqlMapList implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) 
	{
		
		String sql ="select vpr.*,to_char(vpr.create_time,'yyyy-MM-dd HH24:MI') as createtime,to_char(vpr.refund_time,'yyyy-MM-dd HH24:MI') as refundtime,am.loginaccount,am.membername from volunteers_pay_record as vpr "
				+ " left join am_member as am on am.id = vpr.member_id where 1=1 ";
		
		DBManager db = new DBManager();
		MapList list = db.query(sql);
		String loginaccount = ac.getRequestParameter("member_account");
		String status = ac.getRequestParameter("status");
		
		if(!Checker.isEmpty(loginaccount))
		{
			sql+="am.loginaccount='"+loginaccount+"'";
		}
		if(!Checker.isEmpty(status))
		{
			sql+="vpr.status='"+status+"'";
		}
		
		return list;
	}

}
