package com.p2p.comment;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class CommentSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String orgid=ac.getVisitor().getUser().getOrgId();
		String sql=" SELECT * FROM p2p_comment AS pc  "
				+ " LEFT JOIN ws_commodity_name AS wcn "
				+ " ON pc.comdity_id=wcn.comdity_id "
				+ " LEFT JOIN ws_member AS mb "
				+ " ON pc.member_code=mb.member_code "
				+ " WHERE wcn.orgid='"+orgid+"'";;
				
		//查询行
		Row query = FastUnit.getQueryRow(ac, "wwd", "p2p_comment.query");
		if(query!=null){
			String name=query.get("name");
			String comdity_code=query.get("comdity_code");
			if(Checker.isEmpty(name)){
				sql+=" AND wcn.name like '%"+name+"%' ";
			}
			if(Checker.isEmpty(comdity_code)){
				sql+=" AND wcn.comdity_code like '%"+comdity_code+"%'";
			}
		}
		
		return sql;
	}

}
