package com.cdms.caseAdministration;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class CaseAssesslProvider implements SqlProvider{
/*案件考核
 * 
 * 白
 */
	@Override
	public String getSql(ActionContext ac) {
		
		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_case.query");

		// 声明查询的条件字段：时间段
		String names="";//姓名
		String orgnames="";//机构名
		String starttime = "";//开始时间
		String endtime = "";//结束时间
		
		String namesql="";//姓名sql
		String orgnamesql="";//机构sql
		String starttimesql = "";//开始时间sql
		String endtimesql = "";//结束时间sql

		// 如果查询的条件字段的数据结果不为空
		if (queryRow != null){
			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				// 赋值
				starttime = queryRow.get("starttime");
				// 添加查询条件
				starttimesql += "and cc.case_create_time >='"+starttime+"'";
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
				endtimesql += "and cc.case_create_time <='"+endtime+"'";
			}

			// 如果得到的name不为空，就按照用户输入的姓名查询
			if (!Checker.isEmpty(queryRow.get("name"))) {
				 names= queryRow.get("name");
				 namesql += "and am.name like '%" + names + "%' "; 
			}
			

			// 如果获取的机构不为空
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgnames   = queryRow.get("orgcode");
				orgnamesql += "and am.orgcode = '" + orgnames + "' ";
			}else{
				orgnamesql += "and am.orgcode like '" + orgnames + "%' ";
			}
		}else{
			orgnamesql += "and am.orgcode like '" + orgnames + "%' ";
		}
		
	
		String sql = "";
		sql += "select ss.name,ss.orgname ,avg((extract(epoch from ss.case_order_time)-extract(epoch from ss.distribution_time)))as order_time_sum," + //接单时长=接单时间-派单时间
				"avg((extract(epoch from ss.contact_owners_time)-extract(epoch from ss.case_order_time)))as contact_time_sum," + //联系车主时长=联系车主时间-接单时间
				"avg((extract(epoch from ss.survey_start_time)-extract(epoch from ss.contact_owners_time)))as arrive_time_sum," + //抵达现场时长=查勘开始时间-联系车主时间
				"avg((extract(epoch from ss.survey_stop_time)-extract(epoch from ss.survey_start_time)))as survey_time_sum," + //查勘时长= 查勘结束时间-查勘开始时间
				//总时长= 查勘结束时间-派单时间
				"avg((extract(epoch from ss.survey_stop_time)-extract(epoch from ss.distribution_time)))as time_sum,'"+starttime+"' as start_times,'"+endtime+"' as end_times," + 
				"count(ss.*) as coun from (select cc.*,ao.orgname,am.name from cdms_case cc,am_member am,aorg ao where cc.member_id=am.id and am.orgcode=ao.orgid "+starttimesql+""
				+ ""+endtimesql+""+namesql+""+orgnamesql+") ss "
				+ " where  ss.case_state='6' group by ss.name,ss.orgname";

//		// 如果返回的结果不为空，将starttime和endtime两个条件字段放入sql
//		if (!Checker.isEmpty(mapList)) {
//			for (int i = 0; i < mapList.size(); i++) {
//				mapList.getRow(i).put("start_times", starttime);
//				mapList.getRow(i).put("end_times", endtime);
//			}
//		}
		
		
		
		
		
		
		
		
		
		return sql.toString();
	}

}
