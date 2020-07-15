package com.am.marketplace_entity;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class MarketplaceEntityListSql implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		
		String type = ac.getRequestParameter("type");
		
		String sql = "select DISTINCT trim(to_char(COALESCE(price / 100.00),'99999999999990D99')) "
				+ " AS prices, to_char(shelve_time,'YYYY-MM-dd HH:mm:SS') "
				+ " AS shelve_times,member_id AS memberid, "
				+ " CASE release_status WHEN '1' THEN (SELECT orgname FROM aorg WHERE orgid=member_id)  "
				+ " ELSE (SELECT membername FROM am_member WHERE id = member_id) "
				+ " END AS uname,mme.*,ai.zone_id from MALL_MARKETPLACE_ENTITY as mme "
				+ " LEFT JOIN account_info as ai on mme.member_id = ai.id  "
				+ " WHERE mme.status>1 "
				+ " and mme.member_id<>'org' ";
		
				if(Checker.isEmpty(type))
				{
					sql +=  " and ai.zone_id=(select zone_id from service_mall_info where orgid='$U{orgid}') ";
				}
				
				sql += " order by create_time desc";
		
		
		return sql;
	}

}
