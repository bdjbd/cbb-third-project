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
 *说明：合作社管理SqlProvider
 */
public class CooperativeListSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 7294707103906812733L;

	@Override
	public String getSql(ActionContext ac) {
						
		String sql =  "SELECT ur.*,get_membername_by_id(purchaser) AS purchaser_name FROM mall_cooperative as ur "
				+ " LEFT JOIN am_member AS am ON am.id=ur.purchaser "
				+ " LEFT JOIN province AS pr ON pr.proid = ur.province_id "
				+ " LEFT JOIN city AS ct ON ct.cityid = ur.city_id "
				+ " LEFT JOIN zone AS zn ON zn.zoneid = ur.zone_id "
				+ " WHERE 1=1 and ur.f_status NOT IN ('4','5') ";
		
//		联合社，合作社，涉农企业，农场，配送中心
//		String curretnOrg=ac.getVisitor().getUser().getOrgId();
//		if(curretnOrg!=null&&curretnOrg.contains("country")){
//		}else{
//			//如果不是国家级别的，则需要过滤机构
//			sql+=" AND orgid LIKE '$U{orgid}%'  ";
//		}
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac,"am_bdp","mall_cooperative_district.query");
		String province_id = null;
		String zone_id = null;
		String purchaser = null;
		String city_id = null;
		String f_status = null;
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
			//状态
			f_status = queryRow.get("f_statusq");
		}
		if(!Checker.isEmpty(f_status)){
			sql += " AND ur.f_status = '"+f_status+"'";
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
