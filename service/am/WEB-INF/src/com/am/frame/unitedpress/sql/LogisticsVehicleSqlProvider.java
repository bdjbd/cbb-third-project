package com.am.frame.unitedpress.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月30日
 *@version
 *说明：物流车辆管理SqlProvider
 */
public class LogisticsVehicleSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 7294707103906812733L;

	@Override
	public String getSql(ActionContext ac) {
						
		String sql =  "SELECT am.membername,trim(to_char(COALESCE(ur.buy_price/100),'99999999999990D99')) AS money,ur.* "
				+ " FROM mall_logistics_vehicles AS ur "
				+ " LEFT JOIN am_member AS am ON am.id=ur.purchaser "
				+ " LEFT JOIN province AS pr ON pr.proid = ur.province_id "
				+ " LEFT JOIN city AS ct ON ct.cityid = ur.city_id "
				+ " LEFT JOIN zone AS zn ON zn.zoneid = ur.zone_id "
				+ " WHERE 1=1 and ur.f_status NOT IN ('4','5') ";
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_logistics_vehicles.query");
		String province_id = null;
		String zone_id = null;
		String purchaser = null;
		String city_id = null;
		String gapName = null;
		//获取查询数据
		if(queryRow != null){
			//省
			province_id = queryRow.get("province_idq");
			//区
			zone_id = queryRow.get("zone_idq");
			//购买社员
			purchaser = queryRow.get("purchaserq");
			//市
			city_id = queryRow.get("city_idq");
			//名称
			gapName = queryRow.get("gap_nameq");
		}
		if(!Checker.isEmpty(gapName)){
			sql += " AND ur.gap_name LIKE '%"+gapName+"%'";
		}
		if(!Checker.isEmpty(purchaser)){
			sql += " AND am.membername LIKE '%"+purchaser+"%' ";
		}
		if(!Checker.isEmpty(province_id)){
			sql += " AND pr.proname LIKE '%"+province_id+"%' ";
		}
		if(!Checker.isEmpty(city_id)){
			sql += " AND ct.cityname LIKE '%"+city_id+"%' ";
		}
		if(!Checker.isEmpty(zone_id)){
			sql += " AND zn.zonename LIKE '%"+zone_id+"%' ";
		}
		sql += " ORDER BY ur.create_time DESC";
		return sql.toString();
	}

}
