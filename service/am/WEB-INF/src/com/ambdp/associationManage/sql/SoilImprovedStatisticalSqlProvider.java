package com.ambdp.associationManage.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author xc
 * @create 2016年12月9日18:11:49
 * @version 说明：土地SqlProvider
 */
public class SoilImprovedStatisticalSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {

		StringBuilder qsb=new StringBuilder();
		
		qsb.append("SELECT COALESCE(sum(starting_amount)*0.01,0) AS farmers_top_up/**启动金额--农户充值总额**/,");
		qsb.append(" COALESCE(sum(consumer_donor_amount)*0.01,0) AS public_funding/**消费者帮扶---市民资助总额**/,");
		qsb.append(" COALESCE(sum(project_donor_amount)*0.01,0) AS project_support/**项目扶持---项目扶持总金额**/,");
		qsb.append(" count((select id FROM am_member WHERE id=am.id)) AS funding_farmers");
		qsb.append(" FROM mall_account_info AS mai");
		qsb.append(" LEFT JOIN mall_system_account_class AS msac ON msac.id = mai.a_class_id ");
		qsb.append(" LEFT JOIN am_member AS am ON am.id = mai.member_orgid_id  ");
		qsb.append(" WHERE am.member_type='3' AND msac.sa_code='CREDIT_CARD_ACCOUNT' ");

		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "soil_improved_statistical.query");
		if (queryRow != null) {

			if (!Checker.isEmpty(queryRow.get("soil_province"))) {
				qsb.append(" AND am.province='"+queryRow.get("soil_province")+"' ");
			}
			
			if (!Checker.isEmpty(queryRow.get("soil_city"))) {
				qsb.append(" AND am.city='"+queryRow.get("soil_city")+"' ");
			}
			
			if (!Checker.isEmpty(queryRow.get("soil_zones"))) {
				qsb.append(" AND am.zzone='"+queryRow.get("soil_zones")+"' ");
			}
		}
		
		return qsb.toString();
	}
}
