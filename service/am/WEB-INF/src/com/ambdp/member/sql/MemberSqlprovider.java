package com.ambdp.member.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年1月15日 下午4:00:35
 * @version 说明：会员查询sql
 */
public class MemberSqlprovider implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		// 查询会员sql
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT amli.* from am_memberlogininfo AS amli");
		sql.append(" LEFT JOIN am_member AS am ON am.id = amli.am_memberid");
		sql.append(" WHERE 1=1 ");
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "am_memberlogininfo_query");
		
		//会员id
		String memberid =null;
		//会员姓名
		String membername = null;
		
		if(queryRow!=null){
			
			//会员id
			memberid = queryRow.get("memberid");
			
			//会员姓名
			membername = queryRow.get("membername");
			
		}
		
		//按会员Id查询
		if(!Checker.isEmpty(memberid)){
			sql.append(" AND am.loginaccount like '"+memberid+"' ");
		}
		
		//按会员姓名查询
		if(!Checker.isEmpty(membername)){
			sql.append(" AND membername like '"+membername+"' ");
		}
		
		sql.append(" ORDER BY logindate desc  ");
		
		
		return sql.toString();
	}

}
