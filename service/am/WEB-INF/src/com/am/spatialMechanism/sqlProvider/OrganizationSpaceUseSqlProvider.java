package com.am.spatialMechanism.sqlProvider;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/**
 * @author qintao
 * @create 2016年11月16日
 *
 * 说明:<br />
 * 空间使用权列表显示
 */


public class OrganizationSpaceUseSqlProvider implements SqlProvider {

	
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String SQL="SELECT to_char(create_date,'yyyy-MM-dd hh24:mi') AS createdate,to_char(due_time,'yyyy-MM-dd hh24:mi') AS duetime,* FROM lxny_space_usage_fee where 1=1 "; 
		
		String id=ac.getVisitor().getUser().getOrgId();
		
		//组织机构代码判断是拥有该权限
		if("org_operation".equals(id)||"country_UP".equals(id)){
			SQL+=" ORDER BY due_time DESC";
		}else{
			SQL+=" AND org_code='"+id+"' ORDER BY due_time DESC";
		}
		
		return SQL;
	}

}
