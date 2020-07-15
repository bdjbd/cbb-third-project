package com.cdms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.report.DateTool;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/*
 * 车辆使用情况
 */
public class CarUsageProvider implements SqlProvider 
{
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String getSql(ActionContext ac) 
	{
		String orgcode =ac.getVisitor().getUser().getOrgId();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		String ks =  format.format(c.getTime());
		String js = format.format(new Date());
		js = DateTool.dateAddOne(js);
		Row queryRow = FastUnit.getQueryRow(ac, "cdms","cdms_car_usage.query");
		if (queryRow!=null) {
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				ks=queryRow.get("starttime");
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				js=queryRow.get("endtime");
				
			}
		}
		
		
		String sql="";
		String cvsql = "select sum(stop_time) from cdms_VehicleIdling where car_id=cvb.id ";
		cvsql+=" and  '"+ks+"'<=acc_turn_off_time ";
		cvsql+=" and '"+js+"'>acc_turn_on_time ";
		String cvdusql = "select sum(the_travel_time+the_rest_time) from cdms_vehicledayutilization where car_id=cvb.id";
		cvdusql+=" and '"+ks+"'<=date  ";
		cvdusql+=" and '" +  js+"'> date ";
		sql+="select '"+ks+"' as starttime,'"+js+"' as endtime,"+
				"cvb.id car_id,org.orgid orgcode,"+
				" cvb.license_plate_number,org.orgname,("+cvsql+") as sum_stop_time,("+cvdusql+") as sum_the_travel_time" + 
				" from cdms_vehiclebasicInformation cvb " 
				+ "left join aorg org on cvb.orgcode=org.orgid  where 1=1 ";
	
		if (queryRow!=null) 
		{
			if (!Checker.isEmpty(queryRow.get("orgcode"))) 
			{
				orgcode=queryRow.get("orgcode");
				sql+=" and  org.orgid ='"+orgcode+"'";
			}else {
				sql+=" and  org.orgid like '"+orgcode+"%'";
			}
			
		}else {
			sql+=" and  org.orgid like '"+orgcode+"%'";
		}
		
		if (queryRow!=null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				String license_plate_number=queryRow.get("license_plate_number");
			
				sql+=" and cvb.license_plate_number like '%"+license_plate_number+"%'";
			}
		}
		sql+=" group by cvb.id,org.orgname,org.orgid";
		sql+=" order by org.orgid,sum_stop_time desc nulls last,sum_the_travel_time desc nulls last";	
		
		return sql;
	}


    
}
