package com.cdms.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

// 考勤统计
public class AttendanceStatistics implements SqlProvider {

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String getSql(ActionContext ac) {
		String orgcase = "";//机构查询条件
		String sql = "select date_of_attendance,"
				+ "to_char(clock_in_at_work,'yyyy-MM-dd HH24:MI:SS') as clock_in_at_work," + "punch_in_location,"
				+ "to_char(clock_out_after_work,'yyyy-MM-dd HH24:MI:SS') as clock_out_after_work,"
				+ "punch_out_location," + "clocking_in_duration," + "b.name as name," + "c.orgname as orgname,"
				+ "c.orgid as orgid " + "from CDMS_ATTENDANCERECORD a,am_member b,aorg c "
				+ "where a.member_id=b.id and b.orgcode=c.orgid ";
		// 接收总报表传过来的car_id，时间段
		String car_id = ac.getRequestParameter("cdms_attendancerecord.list.car_id");
		String starttime = ac.getRequestParameter("cdms_attendancerecord.list.starttime");
		String enttime = ac.getRequestParameter("cdms_attendancerecord.list.enttime");
		
		// 如果传过来的car_id不为空，则按照总报表的参数查询，如果为空，则正常查询
		if (!Checker.isEmpty(car_id)) {
			sql += "and a.car_id='" + car_id + "' ";
		}else{
			// 获取当前登录人员所在机构的机构id
			String orgcode = ac.getVisitor().getUser().getOrgId();
			//获得一月前的日期和一天后的日期，如果时间字段为空，则设为默认查询条件
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -1);
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
			starttime =  format.format(c.getTime());
			enttime = format.format(new Date());
			enttime = DateTool.dateAddOne(enttime); 
			
			// 获取查询的所有条件字段的数据
			Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_attendancerecord.query");
			
			if (queryRow != null) {
				// 如果得到的时间段条件字段不为空
				if (!Checker.isEmpty(queryRow.get("starttime"))) {
					starttime = queryRow.get("starttime");
					
				}
				if (!Checker.isEmpty(queryRow.get("endtime"))) {
					enttime = queryRow.get("endtime");
					
				}
				// 如果得到的人员姓名条件字段不为空
				if (!Checker.isEmpty(queryRow.get("name"))) {
					String name = queryRow.get("name");
					sql += "and b.name like '%" + name + "%' ";
				}
				// 如果得到的上班打卡位置条件字段不为空
				if (!Checker.isEmpty(queryRow.get("punch_in_location"))) {
					String punch_in_location = queryRow.get("punch_in_location");
					sql += "and punch_in_location like '%" + punch_in_location + "%' ";
				}
				// 如果得到的orgid不为空，就按照用户输入的机构查询
				// 如果得到的orgid为空，也就是用户没有输入机构，那么就查询当前用户所在的机构，及其下级机构
				if (!Checker.isEmpty(queryRow.get("orgid"))) {
					orgcode = queryRow.get("orgid");
					orgcase+="and c.orgid = '" + orgcode + "' ";
				}else{
					orgcase+="and c.orgid like '" + orgcode + "%' ";
				}
			}else{
				orgcase+="and c.orgid like '" + orgcode + "%' ";
			}
			sql +=orgcase;
		}
		sql += "and date_of_attendance>='" + starttime + "' ";
		sql += "and date_of_attendance<'" + enttime + "' ";
		sql += " order by date_of_attendance desc";
		logger.info("考勤统计表="+sql);
		return sql;

	}

}
