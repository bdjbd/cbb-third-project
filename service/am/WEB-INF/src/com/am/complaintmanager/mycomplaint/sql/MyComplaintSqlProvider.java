package com.am.complaintmanager.mycomplaint.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午6:53:15
 * @version 我的投诉sql
 */
public class MyComplaintSqlProvider implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","my_complaint_manager.query");
		String order_code = null;
		String start_time = null;
		String end_time = null;
		if(queryRow != null){
			order_code = queryRow.get("order_code");
			start_time = queryRow.get("create_times");
			end_time = queryRow.get("create_times.t");
		}
		
		// 投诉管理sql
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT mcm.*,mmbo.ordercode,ame.membername,mmbo.commodityname,mco.producer_id ");
		sql.append(" FROM mall_complaint_manager AS mcm ");
		sql.append(" LEFT JOIN mall_complaint_owner AS mco ON mco.comp_id = mcm.id ");
		sql.append(" LEFT JOIN mall_memberorder AS mmbo ON mmbo.id = mcm.order_id ");
		sql.append(" LEFT JOIN am_member AS ame ON ame.id = mcm.member_id ");
		sql.append(" WHERE 1=1 AND mcm.member_id = '$U{userid}' ");
		
		if(!Checker.isEmpty(order_code)){
			sql.append(" AND UPPER(mmbo.ordercode) LIKE UPPER('%"+order_code+"%') ");
		}
		
		if(!Checker.isEmpty(start_time) && !Checker.isEmpty(end_time)){
			sql.append(" AND to_date(to_char(mcm.create_time,'YYYY-MM-dd HH:mm:SS'),'YYYY-MM-dd HH:mm:SS') >= to_date('"+start_time+"','YYYY-MM-dd HH:mm:SS') ");
			sql.append(" AND to_date(to_char(mcm.create_time,'YYYY-MM-dd HH:mm:SS'),'YYYY-MM-dd HH:mm:SS') <= to_date('"+end_time+"','YYYY-MM-dd HH:mm:SS') ");
		}
		
		if(!Checker.isEmpty(start_time) && Checker.isEmpty(end_time)){
			sql.append(" AND to_date(to_char(mcm.create_time,'YYYY-MM-dd HH:mm:SS'),'YYYY-MM-dd HH:mm:SS') >= to_date('"+start_time+"','YYYY-MM-dd HH:mm:SS') ");
		}
		
		if(Checker.isEmpty(start_time) && !Checker.isEmpty(end_time)){
			sql.append(" AND to_date(to_char(mcm.create_time,'YYYY-MM-dd HH:mm:SS'),'YYYY-MM-dd HH:mm:SS') <= to_date('"+end_time+"','YYYY-MM-dd HH:mm:SS') ");
		}
		
		sql.append(" ORDER BY mcm.create_time DESC");
		
		return sql.toString();
	}

}
