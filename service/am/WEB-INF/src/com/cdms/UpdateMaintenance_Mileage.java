package com.cdms;
/*
 * 
 * 刘扬  清空保养里程
 * 
 * 车辆基础信息表
 */
import org.apache.log4j.Logger;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class UpdateMaintenance_Mileage extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Logger log = Logger.getLogger(UpdateMaintenance_Mileage.class);
		String id=ac.getRequestParameter("id");
		String sql = "update cdms_vehiclebasicinformation set current_mileage=0,maintenance_mileage_time=now() where id='"+id+"'";
		db.execute(sql);
		String delMessage = "delete from cdms_message where remind_type='1' and car_id='"+ id + "'";
		db.execute(delMessage);
	}

}
