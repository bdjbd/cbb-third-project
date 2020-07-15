package com.cdms.query;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/**
 * 
 * 
 * 离线车辆统计表模糊查询
 * 
 * 刘扬
 */

public class CarOffLine implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// 获取查询的所有条件字段的数据
		//定义车牌号
		String license_plate_number = "";
		String location="";
		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "camd_car_off_line.query");
		// 创建sql语句
		String sql = "select extract(epoch from now())-extract(epoch from last_heartbeat_time) off_line_time"
				+ " ,v.license_plate_number"
				+ " ,v.orgcode"
				+ " ,to_char(v.last_heartbeat_time,'YYYY-MM-DD HH24:MI:SS') last_heartbeat_time"
				+ " ,v.location"
				+ " ,v.vehicle_state"
				+ " ,(case when v.fault_code in (select f.alarm_code from cdms_faultalarmtype f where f.atid='1')  then (select f.fatname from cdms_faultalarmtype f where v.fault_code=f.alarm_code and f.atid='1') "
				+ "  when vehicle_state = '2' then '车辆维修中' "
				+ "  when vehicle_state = '3' then '终端维护中' "
				+ "  when vehicle_state = '4' then '车辆已报废' "
				+ "else '未知原因' end) as offline_reason "
				+ "  from  "
				+ "  cdms_VehicleBasicInformation v"
				+ "  where 1=1 and vehicle_state in ('2','3','4','8')";
		
		// 如果搜索栏orgcode不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgcode = queryRow.get("orgcode");
				sql+="  and  v.orgcode = '"+orgcode+"' ";
			}else{
				sql+="  and  v.orgcode like '"+orgcode+"%' ";
			}
			
		}else{
			sql+="  and  v.orgcode like '"+orgcode+"%' ";
		}
		
		// 如果获取的车牌号不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
			license_plate_number = queryRow.get("license_plate_number");
			sql+="  and  v.license_plate_number like '%"+license_plate_number+"%' ";
			}
			
		}
		
		
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("location"))) {
				location = queryRow.get("location");
				sql+="  and  v.location like '%"+location+"%' ";
			}
			
		}
		
		sql+="order by last_heartbeat_time desc";
		return sql;
	}
}
