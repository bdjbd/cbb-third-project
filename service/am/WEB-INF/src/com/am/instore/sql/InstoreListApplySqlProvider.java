package com.am.instore.sql;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月28日
 *@version
 *说明：入库管理列表流程SqlProvider
 */
public class InstoreListApplySqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder querSql=new StringBuilder();
		
		String flowid=ac.getRequestParameter("flowinstid");
		
		querSql.append(" SELECT trim(to_char(COALESCE(inprice/100),'99999999999990D99')) AS price,* FROM mall_instore_list WHERE 1=1  ");
		
		if(!Checker.isEmpty(flowid)){
			querSql.append(" AND in_store_id IN (SELECT id FROM p2p_instore WHERE flow_id="+flowid+" ) ");
		}else{
			querSql.append(" AND in_store_id='$RS{p2p_instorage.form.id,am_bdp.p2p_instorage.form.id}'  ");
		}
		
		querSql.append(" ORDER BY create_time DESC ");
		
		return querSql.toString();
	}
}
