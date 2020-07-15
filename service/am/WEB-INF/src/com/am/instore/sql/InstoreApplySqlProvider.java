package com.am.instore.sql;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月28日
 *@version
 *说明：入库管理流程SqlProvider
 */
public class InstoreApplySqlProvider  implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder querSql=new StringBuilder();
		
		String flowid=ac.getRequestParameter("flowinstid");
		
		
		querSql.append("SELECT trim(to_char(COALESCE(total_amount/100),'99999999999990D99')) AS money,incode AS code,* FROM p2p_instore   ");
		querSql.append(" WHERE 1=1 ");
		
		if(!Checker.isEmpty(flowid)){
			querSql.append(" AND flow_id='"+flowid+"'");
		}else{
			querSql.append(" $SQL[[ and id = '$RS{p2p_instorage.form.id,am_bdp.p2p_instorage.form.id}']] ");
		}
		
//		querSql.append(" ORDER BY mp.create_time desc  ");
		return querSql.toString();
	}
}
