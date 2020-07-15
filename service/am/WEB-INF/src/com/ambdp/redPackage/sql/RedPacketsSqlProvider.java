package com.ambdp.redPackage.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月14日
 *@version
 *说明：
 */
public class RedPacketsSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder querSQL = new StringBuilder();

		querSQL.append("SELECT trim(to_char(COALESCE(rp.money_value/100),'99999999999990D99')) AS money,rp.* ");
		querSQL.append(" FROM mall_red_packets AS rp ");
		querSQL.append(" LEFT JOIN mall_base_setting AS bs ON rp.set_type=bs.id ");
		querSQL.append(" WHERE 1=1 ");
		
		
		// 类型
		String set_types = null;
		// 金额
		String money_values = null;
		String money_values_t = null;

		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_red_packets.query");

		if (queryRow != null) {
			// 类型
			set_types = queryRow.get("set_types");
		}
		if (queryRow != null) {
			// 金额前
			money_values = queryRow.get("money_values");
		}
		if (queryRow != null) {
			// 金额后
			money_values_t = queryRow.get("money_values.t");
		}
		// 3,如果查询单元有数据，将数据拼接到where中
		if (!Checker.isEmpty(set_types)) {
			querSQL.append(" AND bs.bname  LIKE  '%" + set_types + "%' ");
		}
		if (!Checker.isEmpty(money_values)) {
			querSQL.append("  AND rp.money_value >="+money_values+"*100 ");
		}
		if(!Checker.isEmpty(money_values_t)){
			querSQL.append("  AND rp.money_value <="+money_values_t+"*100 ");
		}
	

	
		querSQL.append(" ORDER BY rp.rp_sort ");
		
		return querSQL.toString();
	}

}
