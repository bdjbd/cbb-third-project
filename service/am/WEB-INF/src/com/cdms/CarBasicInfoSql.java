package com.cdms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.support.action.QueryAction;
import com.fastunit.util.Checker;

public class CarBasicInfoSql implements SqlProvider {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String getSql(ActionContext ac) {

		String sql = "select *,o.orgname from cdms_vehiclebasicinformation v,AORG o where v.orgcode=o.orgid and v.orgcode like '$O{orgid}%' ";
		// 车牌号
		String license_plate_number = ac.getRequestParameter("license_plate_number");
		if (!Checker.isEmpty(license_plate_number)) {
			try {
				Row queryRow = FastUnit.getQueryRow(ac, "cdms","cdms_vehiclebasicinformation.query");
				queryRow.put("license_plate_number", license_plate_number);
				sql += " and license_plate_number='" + license_plate_number + "'";
				QueryAction q = new QueryAction();
				q.execute(ac);
			} catch (Exception e) {
				logger.info("车牌号不为空时捕捉到的异常==" + e.toString());
			}
		}

		sql += " order by v.token";
		return sql;
	}

}
