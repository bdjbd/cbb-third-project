package com.am.cro.member.SqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/7/25
 * @version 
 * 说明:会员列表  只显示当前机构及下属机构的会员列表
 * 会员查询
 */
public class MembersListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String orgcode = ac.getVisitor().getUser().getOrgId();
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp","am_member1.query");
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append(" select m.id as id, m.loginaccount,m.membername,m.registrationdate,a.balance*0.01 as balance ");
		querySQL.append(" from am_member as m,  mall_account_info as a ");
		querySQL.append(" where 1=1 and m.orgcode like '"+orgcode+"%' and m.id=a.member_orgid_id ");
		querySQL.append(" and a.a_class_id in(select id from mall_system_account_class where sa_code='CASH_ACCOUNT') ");
		
		
		if(queryRow!=null){
			//组织机构代码（SQL中已直接自动拼接上了，不必再重复使用，很惊奇吧！）
//			String orgcode =queryRow.get("orgcode");
//			if(!Checker.isEmpty(orgcode)){
//				querySQL.append(" and m.orgcode like '"+orgcode+"%' ");
//			}
			
			//会员账号
			String loginaccount_q = queryRow.get("loginaccount_q");
			if(!Checker.isEmpty(loginaccount_q)){
				querySQL.append(" and m.loginaccount like '%"+loginaccount_q+"%' ");
			}
			//会员姓名
			String membername_q = queryRow.get("membername_q");
			if(!Checker.isEmpty(membername_q)){
				querySQL.append(" and m.membername like '%"+membername_q+"%' ");
			}
			//注册时间 start
			String timeStart=queryRow.get("registrationdate_q");
			//注册时间 end
			String timeEnd=queryRow.get("registrationdate_q.t");
			if(!Checker.isEmpty(timeStart)&&!Checker.isEmpty(timeEnd)){
				querySQL.append(" AND (m.registrationdate>=to_date('"+timeStart+"','yyyy-mm-dd') "
						+ " AND m.registrationdate<= to_date('"+timeEnd+"','yyyy-mm-dd'))");
			}
						
		}
		
		querySQL.append(" ORDER BY m.registrationdate desc ");
		
		return querySQL.toString();
	}

}
