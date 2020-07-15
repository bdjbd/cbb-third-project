package com.am.frame.comment.sql;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;


public class SelectUserSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql="SELECT *,get_addrname_by_id('','',COALESCE(zzone,'')) AS address FROM am_member WHERE is_poor=1 ";
		
		String zzone="";
		
		
		//获取当前机构的区号
		String orgId=ac.getVisitor().getUser().getOrgId();
		
		
		if(!Checker.isEmpty(orgId)){
			//org_P22_C214_Z1805_IPRCC 
			String[] orgIdPostions=orgId.split("_");
			if(orgIdPostions.length>4){
				zzone=orgIdPostions[3].substring(1, orgIdPostions.length);
			}
		}
		
		sql+=" AND zzone='"+zzone+"' ";
		
		return sql;
	}

}
