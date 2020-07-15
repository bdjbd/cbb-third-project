package com.am.frame.memberlogout.sqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/**
 * @author YueBin
 * @create 2016年6月14日
 * @version 
 * 说明:<br />
 */
public class MemberLogoutSqlProvidre implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		
		Row row=FastUnit.getQueryRow(ac,"am_bdp","am_member_logout.query");
		
		if(row!=null){
			String loginaccount=row.get("");
		}
		
		String sql=" select * from AM_MEMBER_LOGOUT ORDER BY create_time DESC ";
		
		return null;
	}

}
