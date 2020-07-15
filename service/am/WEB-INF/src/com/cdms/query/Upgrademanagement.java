package com.cdms.query;
/**
 * 
 * 
 * 远程列表升级查询
 * 
 * 刘扬
 */
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class Upgrademanagement implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// 获取查询的所有条件字段的数据
		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_upgrademanagement111.query");
		
		// 创建sql语句
		//定义车牌号
		String device_sn_number="";
		String sim_card_number="";
		String license_plate_number = "";
		String sql = "select v.* "
				+ " ,a.orgname"
				+ " ,(select (case when total_package <= 0 then total_package   when current_package<=0 then 0    else ((current_package*1.0)/(total_package*1.0)) end)  as percent  from cdms_TerminalCommand as ct where ct.car_id=v.id and ct.state='2' and cmd_type='1')"
				+ " ,(select version_number from cdms_RemoteUpgradeManagement "
				+ " where id=v.current_version_number)"
				+ " from cdms_VehicleBasicInformation v ,AORG a"
				+ " where"
				+ " v.orgcode=a.orgid";

		
		
		// 如果搜索栏orgcode不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgcode = queryRow.get("orgcode");
				sql+="  and  v.orgcode ='"+orgcode+"' ";
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
			}
			sql+="  and  v.license_plate_number like '%"+license_plate_number+"%' ";
		}
		//如果获取的SIM卡号不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("sim_card_number"))) {
				sim_card_number = queryRow.get("sim_card_number");
			}
			sql+="  and  v.sim_card_number like '%"+sim_card_number+"%' ";
		}
		
		//如果获取终端编号不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("device_sn_number"))) {
				device_sn_number = queryRow.get("device_sn_number");
			}
			sql+="  and  v.device_sn_number like '%"+device_sn_number+"%' ";
		}
		
		
		sql+=" order by v.last_upgrade_time desc";
		System.err.println("sql-->"+sql);
		
		
		return sql.toString();
	}
}
