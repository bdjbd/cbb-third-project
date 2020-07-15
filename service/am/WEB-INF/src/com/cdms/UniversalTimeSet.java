package com.cdms;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class UniversalTimeSet extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table=ac.getTable("aorg");
		TableRow tr=table.getRows().get(0);
		String id=tr.getValue("orgid");
		
		String startTime= ac.getRequestParameter("aorg.form.car_start_time_hour")+":"+ac.getRequestParameter("aorg.form.car_start_time_min");
		String starthour=ac.getRequestParameter("aorg.form.car_start_time_hour");
		String startmin=ac.getRequestParameter("aorg.form.car_start_time_min");
		String stopTime= ac.getRequestParameter("aorg.form.car_stop_time_hour")+":"+ac.getRequestParameter("aorg.form.car_stop_time_min");
		String stophour=ac.getRequestParameter("aorg.form.car_stop_time_hour");
		String stopmin=ac.getRequestParameter("aorg.form.car_stop_time_min");
		String sql1="update aorg set car_start_time='"
				+ startTime
				+"',car_start_time_hour="
				+starthour
				+",car_start_time_min="
				+startmin
				+",car_stop_time='"
				+ stopTime
				+"',car_stop_time_hour="
				+stophour
				+",car_stop_time_min="
				+stopmin
				+ " where orgid like '"+id+"%'";
		String sql2="update cdms_vehiclebasicinformation set car_statr_time='"
				+ startTime
				+"',car_start_time_hour="
				+starthour
				+",car_start_time_min="
				+startmin
				+",car_stop_time='"
				+ stopTime
				+"',car_stop_time_hour="
				+stophour
				+",car_stop_time_min="
				+stopmin
				+ " where orgcode like '"+id+"%'";
		db.execute(sql1);
		db.execute(sql2);
	}
	
}
