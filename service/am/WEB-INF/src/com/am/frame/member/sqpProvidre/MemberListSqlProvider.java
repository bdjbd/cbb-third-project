package com.am.frame.member.sqpProvidre;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年8月13日
 * @version 
 * 说明:<br />
 * 会员查询
 */
public class MemberListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp","am_member_query");
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("select m.is_auth,m.orgcode,to_char(m.create_time,'yyyy-mm-dd') AS time,m.id AS id,m.membername,");
		querySQL.append("	m.loginaccount,m.account_freeze,m.phone,c.invitationcode,m.membersex,m.orgcode,                     ");
		querySQL.append("	p.proname||' '||ci.cityname||' '||z.zonename AS area,m.city,m.zzone,m.member_type, ");
		querySQL.append("	m.income_people_name,m.income_people_idcard,                                       ");
		querySQL.append("	CASE WHEN m.status=1 THEN '已注销' WHEN m.status=0 THEN '正常' END AS status       ");
		querySQL.append("	from AM_MEMBER  AS m                                                               ");
		querySQL.append("	LEFT JOIN am_MemberInvitationCode AS c ON m.register_inv_code_id=c.id              ");
		querySQL.append("	LEFT JOIN aorg AS g ON m.orgcode=g.orgid                                           ");
		querySQL.append("	LEFT JOIN province AS p ON p.proid =m.province                                     ");
		querySQL.append("	LEFT JOIN city  AS ci ON ci.cityid=m.city                                          ");
		querySQL.append("	LEFT JOIN zone AS z ON z.zoneid=m.zzone                                            ");
		querySQL.append("	WHERE 1=1  ");
		
		
		if(queryRow!=null){
			//认证状态
			String is_auth=queryRow.get("is_auth");
			if(!Checker.isEmpty(is_auth)){
				querySQL.append(" AND m.is_auth= "+is_auth+" ");
			}
			//登录帐号
			String loginaccountQ=queryRow.get("loginaccount_q");
			if(!Checker.isEmpty(loginaccountQ)){
				querySQL.append(" AND m.loginaccount LIKE '%"+loginaccountQ+"%' ");
			}
			//会员姓名
			String membername_q=queryRow.get("membername_q");
			if(!Checker.isEmpty(membername_q)){
				querySQL.append(" AND m.membername LIKE '%"+membername_q+"%' ");
			}
			
			//注册时间 start
			String create_timeStart=queryRow.get("create_time_q");
			//注册时间 end
			String create_timeEnd=queryRow.get("create_time_q.t");
			if(!Checker.isEmpty(create_timeStart)&&!Checker.isEmpty(create_timeEnd)){
				querySQL.append(" AND (m.create_time>=to_date('"+create_timeStart+"','yyyy-mm-dd') "
						+ " AND m.create_time<= to_date('"+create_timeEnd+"','yyyy-mm-dd'))");
			}
			
			//区域
			String area_q=queryRow.get("area_q");
			if(!Checker.isEmpty(area_q)){
				querySQL.append(" AND p.proname||ci.cityname||z.zonename ~* '"+area_q+"' ");
			}
			
			//会员类型
			String member_type_q=queryRow.get("member_type_q");
			if(!Checker.isEmpty(member_type_q)){
				querySQL.append(" AND  m.member_type ='"+member_type_q+"' ");
			}
			
			//所属机构
			String orgcode_q=queryRow.get("orgcode_q");
			if(!Checker.isEmpty(orgcode_q)){
				querySQL.append("	AND  g.orgname ~* '"+orgcode_q+"'  ");
			}

			
			//去掉在查询时过滤此人所属组织机构的条件，也就是说在会员查询时去除他是否已经加入组织机构，xc 2017年2月28日16:34:51修改
//			else{
//				querySQL.append("	AND  m.orgcode LIKE 'org%'  ");
//			}
			
		}else{
			querySQL.append("	AND m.orgcode LIKE 'org%'    ");
		}
		
		querySQL.append("	ORDER BY orgcode desc ");
		
		return querySQL.toString();
	}

}
