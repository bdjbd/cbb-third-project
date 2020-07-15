package com.am.frame.unitedpress.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * 扶贫办sql
 * @author yuebin
 *
 */
public class IprccOfficeSqlProvider implements SqlProvider {


	private static final long serialVersionUID = 7294707103906812733L;

	@Override
	public String getSql(ActionContext ac) {
						
		String sql =  "select ur.*,get_membername_by_id(purchaser) AS purchaser_name  from iprcc_office as ur "
				+ " left join am_member as am on am.id=ur.purchaser "
				+ " left join province as pr on pr.proid = ur.province_id "
				+ " left join city as ct on ct.cityid = ur.city_id "
				+ " left join zone as zn on zn.zoneid = ur.zone_id "
				+ " where 1=1 and ur.f_status NOT IN ('4','5') "
				+ " $Q{iprcc_office,ur} $Q{province,pr}"
				+ " $Q{city,ct} $Q{zone,zn} $Q{am_member,am} ";
		
		
		//$RS{areaType,sc.iprcc.areaType}
		String areaType=ac.getRequestParameter("areaType");
		
		if(Checker.isEmpty(areaType)){
			areaType=ac.getSessionAttribute("sc.iprcc.areaType", "");
		}else{
			ac.setSessionAttribute("sc.iprcc.areaType",areaType);
		}
		
		sql+=" AND ur.area_type='"+areaType+"' ";
		
//		String curretnOrg=ac.getVisitor().getUser().getOrgId();
		
//		if(curretnOrg!=null&&curretnOrg.contains("country")){
//		}else{
//			//如果不是国家级别的，则需要过滤机构
//			sql+=" and orgid like '$U{orgid}%'  ";
//		}
		
		
		
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","iprcc_office.query");
		String province_id = null;
		String zone_id = null;
		String purchaser = null;
		String city_id = null;
		String f_status = null;
		//获取查询数据
		if(queryRow != null){
			province_id = queryRow.get("province_idq");
			zone_id = queryRow.get("zone_idq");
			purchaser = queryRow.get("purchaserq");
			city_id = queryRow.get("city_idq");
			f_status = queryRow.get("f_statusq");
		}
		if(!Checker.isEmpty(f_status)){
			sql += " AND ur.f_status = '"+f_status+"'";
		}
		if(!Checker.isEmpty(purchaser)){
			sql += " AND am.membername LIKE '%"+province_id+"%' ";
		}
		if(!Checker.isEmpty(province_id)){
			sql += " AND pr.proname LIKE '%"+province_id+"%' ";
		}
		if(!Checker.isEmpty(zone_id)){
			sql += " AND ct.cityname LIKE '%"+zone_id+"%' ";
		}
		if(!Checker.isEmpty(city_id)){
			sql += " AND zn.zonename LIKE '%"+city_id+"%' ";
		}
		sql += " order by ur.create_time DESC";
		
		return sql.toString();
	}

}
