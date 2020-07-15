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

/**
 * 区域外用车统计表
 * 
 * @author zyz
 *
 */
public class OutsideUseCarFormProvider implements SqlProvider {
Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String getSql(ActionContext ac) {
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_enclosurerecordout.query");
		Calendar c = Calendar.getInstance();
		  c.add(Calendar.MONTH, -1);
		  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		// 声明查询的条件字段：时间段
		String starttime = format.format(c.getTime());
		String endtime = format.format(new Date());
		endtime = DateTool.dateAddOne(endtime);
		if (queryRow != null) {
			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				// 赋值
				starttime = queryRow.get("starttime");
				// 添加查询条件
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
			}
		}
		String sql = "";
		sql +=  " select '"+starttime+"' as starttime,'"+endtime+"' as endtime,"
				+ "count(a.id) as times," + "c.license_plate_number as license_plate_number,"
				+ "d.orgname as orgname," + "sum(a.duration) as sumduration," + "sum(a.mileage) as summileage,"
				+ "sum(a.oil) as sumoil," + "c.id as car_id " + "from CDMS_ENCLOSURERECORD a,"
				+ "cdms_ElectronicFence b," + "cdms_vehiclebasicinformation c," + "aorg d " + "where a.fence_id=b.id "
				+ "and b.electronic_fence_type='2' " + "and a.car_id=c.id " + "and c.orgcode=d.orgid ";

		// + "where starttime<=start_time and endtime>=start_time";

		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();

		// 获取查询的所有条件字段的数据
		

		// 如果查询的条件字段的数据结果不为空，根据用户输入的字段查询，如果为空，直接查询当前登陆人所在机构及其下级机构的数据
		if (queryRow != null) {
			// 如果得到的时间段条件字段不为空

			// 如果获取的车牌号不为空
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				String license_plate_number = queryRow.get("license_plate_number");
				sql += "and license_plate_number like '%" + license_plate_number + "%' ";
			}

			// 如果得到的orgid不为空，就按照用户输入的机构查询
			// 如果得到的orgid为空，也就是用户没有输入机构，那么就查询当前用户所在的机构，及其下级机构
			if (!Checker.isEmpty(queryRow.get("orgid"))) {
				orgcode = queryRow.get("orgid");
				sql += "and orgid = '" + orgcode + "' ";
			}else{
				sql += "and orgid like '" + orgcode + "%' ";
			}
		}else{
			sql += "and orgid like '" + orgcode + "%' ";
		}
		sql += "and '" + endtime + "'>=a.start_time ";
		sql += "and '" + starttime + "'<=a.start_time ";
		

		sql += "group by c.id,d.orgid ";

		sql += "order by license_plate_number ";

	
		// 返回结果
		return sql;
	}

}
