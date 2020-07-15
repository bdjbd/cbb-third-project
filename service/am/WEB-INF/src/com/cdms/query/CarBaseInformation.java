package com.cdms.query;
/**
 * 
 * 
 * 车辆基本信息统计表
 * 
 * 刘扬
 */
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class CarBaseInformation implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// 获取查询的所有条件字段的数据
		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();
		String license_plate_number="";
		String sim_card_number="";
		String device_sn_number="";
		String member_id="";
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_car_base_information.query");
		
		// 创建sql语句
		String sql = "select *"
				+ " ,to_char(installation_date_of_equipment,'YYYY-MM-DD') as  installation_time"
				+ " ,to_char(annual_inspection_time,'YYYY-MM-DD') as  inspection_time"
				+ " ,to_char(duration_of_insurance,'YYYY-MM-DD') as  duration_time"
				+ " ,(select m.contact_number from am_member m where m.id=v.member_id) "
				+ " ,(select m.name from am_member m where m.id=v.member_id)"
				+ " from "
				+ " cdms_VehicleBasicInformation v inner join AORG  a "
				+ " on v.orgcode=a.orgid where 1=1 " ;
				
		
		
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
		
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				license_plate_number = queryRow.get("license_plate_number");
				String license_plate_number1=license_plate_number.toUpperCase();
				sql+=" and v.license_plate_number like '%"+license_plate_number1+"%'";
			}
			
			if (!Checker.isEmpty(queryRow.get("sim_card_number"))) {
				sim_card_number = queryRow.get("sim_card_number");
				sql+=" and v.sim_card_number like '%"+sim_card_number+"%'";
			}
			
			if (!Checker.isEmpty(queryRow.get("device_sn_number"))) {
				device_sn_number = queryRow.get("device_sn_number");
				sql+=" and v.device_sn_number like '%"+device_sn_number+"%'";
			}
			
			if (!Checker.isEmpty(queryRow.get("member_id"))) {
				member_id = queryRow.get("member_id");
				sql+=" and (select name from am_member where id=v.member_id) like '%"+member_id+"%'";
			}
			
		}
		
		
		
		sql+=" group by v.orgcode,v.id,a.orgid  order by v.license_plate_number ";

		return sql.toString();
	}
}
