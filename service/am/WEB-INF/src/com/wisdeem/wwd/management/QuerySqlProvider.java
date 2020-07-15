package com.wisdeem.wwd.management;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class QuerySqlProvider implements SqlProvider{

	/**
	 * 企业管理查询区
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql = "select b.orgid as queryid,* from WS_ORG_BASEINFO a right join aorg b on a.orgid=b.orgid where parentid <> ''";
		Row query = FastUnit.getQueryRow(ac, "wwd", "ws_org_baseinfo.query");
		String orgid="";
		String orgname="";
		if(query!=null){
			orgid=query.get("queryid");
			orgname=query.get("orgname");
		}
		if(!Checker.isEmpty(orgid)){
			sql+=" and lower(b.orgid) like '%"+orgid+"%'";
		}
		if(!Checker.isEmpty(orgname)){
			sql+=" and lower(b.orgname) like '%"+orgname+"%'";
		}
		//System.out.println(sql);
		return sql;
	}

}
