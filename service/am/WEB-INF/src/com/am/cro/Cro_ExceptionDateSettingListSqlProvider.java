package com.am.cro;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/**
 * @version 
 * 说明:<br />
 */
public class Cro_ExceptionDateSettingListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("select id,orgcode,to_char(start_date,'yyyy-MM-DD') as start_date,to_char(end_date,'yyyy-MM-DD') as end_date from CRO_EXCEPTIONDATESETTING where  orgcode = '"+orgid+"'  order by start_date desc");
		
		return querySQL.toString();
	}

}
