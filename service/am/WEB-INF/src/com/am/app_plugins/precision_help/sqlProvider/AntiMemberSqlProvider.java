package com.am.app_plugins.precision_help.sqlProvider;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

public class AntiMemberSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	
	@Override
	public String getSql(ActionContext ac) {
		
		String orgid=ac.getVisitor().getUser().getOrgId();
		
		String sql="SELECT 'org_P'||province||'_C'||city||'_Z'||zzone||'_IPRCC' AS iprccOrg,*  "
				+ " FROM am_member  "
				+ " WHERE is_poor=1 ";
		
		if(!"country_IPRCC".equals(orgid)){
			orgid=orgid.replace("_IPRCC","");
			sql+=" AND ('org_P'||province||'_C'||city||'_Z'||zzone) "
			   + " LIKE '"+orgid+"%' ORDER BY loginaccount DESC ";
		}
		
		return sql;
	}

}
