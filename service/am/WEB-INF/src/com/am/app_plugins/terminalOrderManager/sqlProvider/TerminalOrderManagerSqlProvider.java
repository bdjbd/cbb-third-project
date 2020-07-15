package com.am.app_plugins.terminalOrderManager.sqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class TerminalOrderManagerSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql="select g.orgname,m.membername,t.*,to_char(t.create_time,'yyyy-mm-dd hh24:MI:ss') AS fcreate_time " +
					" from AM_TERMINAL_ORDER_MANAGER AS t " +
					" LEFT JOIN am_member AS m ON t.buyer=m.id  " +
					" LEFT JOIN aorg AS g ON g.orgid=t.org_id   " +
					" WHERE 1=1 ";
		
		Row row=FastUnit.getQueryRow(ac,"am_bdp","am_terminal_order_manager.query");
		
		if(row!=null){
			String orgnameq=row.get("orgnameq");
			
			if(!Checker.isEmpty(orgnameq)){
				sql+=" AND g.orgname LIKE '%"+orgnameq+"%' ";
			}
			
			String buyerq=row.get("buyerq");
			if(!Checker.isEmpty(buyerq)){
				sql+=" AND m.membername LIKE '%"+buyerq+"%' ";
			}
		}
		
		sql+=" ORDER BY t.create_time DESC ";
		
		return sql;
	}

}
