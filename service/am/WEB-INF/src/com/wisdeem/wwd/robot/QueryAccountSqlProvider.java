package com.wisdeem.wwd.robot;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class QueryAccountSqlProvider implements SqlProvider{

	/**
	 * 机器人客服查询区
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		Row query = FastUnit.getQueryRow(ac, "wwd", "ws_auto_replay_rule.query");
		String orgid=ac.getVisitor().getUser().getOrgId();
		
		String sql = "select * from WS_AUTO_REPLAY_RULE a left join WS_SEND_MSG b on a.rule_id=b.rule_id " +
				"left join WS_PUBLIC_ACCOUNTS c on c.public_id=a.public_id  where 1=1 and lower(c.orgid) = '"+orgid+"'";
	
		String wchat_account="";
		if(query!=null){
			wchat_account=query.get("wchat_account");
		}
		if(!Checker.isEmpty(wchat_account)){
			sql+=" and lower(c.wchat_account) like '%"+wchat_account+"%'";
		}
		sql+="order by a.create_time desc";
		return sql;
	}
}
