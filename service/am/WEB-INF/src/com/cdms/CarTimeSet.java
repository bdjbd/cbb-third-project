package com.cdms;

/**
 * 刘扬
 *车辆基础信息表处理时间 类
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class CarTimeSet extends DefaultAction {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取表格
		Table table = ac.getTable("cdms_vehiclebasicinformation");
		// 获得ID
		String id = table.getRows().get(0).getValue("id");

		// 获得车牌号
		String license_plate_number = table.getRows().get(0).getValue("license_plate_number");
		
		// 获得车架号
		String frame_number = table.getRows().get(0).getValue("frame_number");
		
		// 获得终端编号
		String device_sn_number = table.getRows().get(0).getValue("device_sn_number");
		
		// 上次年检时间
		String inspection_time = ac.getRequestParameter("cdms_vehiclebasicinformation.form.inspection_time");
		
		//是否修改了上次年检时间，如果修改了，则删除消息表中的对应记录
		if(!Checker.isEmpty(inspection_time)){
			String delMessagesql = "delete from cdms_message where remind_type='2' and car_id='"+ id + "' ";
			db.execute(delMessagesql);
		}
		int count1 = 0;// 车牌号重复次数
		int count2 = 0;// 车架号重复次数
		int count3 = 0;// 终端编号重复次数
		int count4 = 0;// id是否存在
		if (!Checker.isEmpty(license_plate_number)) {
			count1 = isRepeat(db, id, "license_plate_number",
					license_plate_number);
		}
		if (!Checker.isEmpty(frame_number)) {
			count2 = isRepeat(db, id, "frame_number", frame_number);
		}
		if (!Checker.isEmpty(device_sn_number)) {
			count3 = isRepeat(db, id, "device_sn_number", device_sn_number);
		}
		
		count4 = isAdd(db, id);
		if(count4==0){
			table.getRows().get(0).setValue("vehicle_state", "8");
		}
		if (count1 > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("车牌号不能重复");
		} else if (count2 > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("车架号不能重复");
		} else if (count3 > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("终端编号不能重复");
		} else {
			
			//如果更新车辆信息时未修改车辆状态，则保持原来的状态不变
			String vehicle_stateSql = "select vehicle_state from cdms_vehiclebasicinformation where id='"+id+"'";
			MapList mapList = db.query(vehicle_stateSql);
			if(!Checker.isEmpty(mapList)){
				String vehicle_state = table.getRows().get(0).getValue("vehicle_state");//车辆状态
				if(!(vehicle_state.equals("1")||vehicle_state.equals("2")||vehicle_state.equals("3")||vehicle_state.equals("4"))){
					vehicle_state = mapList.getRow(0).get(0);
					table.getRows().get(0).setValue("vehicle_state", vehicle_state);
				}
				
			}
			db.save(table);
			String sqlParams = "";
			
			// 用车开始时分
			String startTime = "";
			if (!Checker.isEmpty(ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_start_time_hour"))) {
				startTime += ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_start_time_hour");
				startTime += ":";
				if (!Checker.isEmpty(ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_start_time_min"))) {
					startTime += ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_start_time_min");
				} else {
					startTime += "00";
				}
			}
			
			// 用车结束时分
			String stopTime = "";
			if (!Checker.isEmpty(ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_stop_time_hour"))) {
				stopTime += ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_stop_time_hour");
				stopTime += ":";
				if (!Checker.isEmpty(ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_stop_time_min"))) {
					stopTime += ac.getRequestParameter("cdms_vehiclebasicinformation.form.car_stop_time_min");
				} else {
					stopTime += "00";
				}
			}
			//获取orgid
			String orgcode = ac.getRequestParameter("cdms_vehiclebasicinformation.form.orgcode");
			logger.debug("orgcode---->"+orgcode);

			logger.debug("上次年检时间"+inspection_time);
			String purchase_date = ac.getRequestParameter("cdms_vehiclebasicinformation.form.purchase_date");
			logger.debug("车辆购买时间"+purchase_date);
			String date_of_equipment = ac.getRequestParameter("cdms_vehiclebasicinformation.form.date_of_equipment");
			logger.debug("设备安装时间"+date_of_equipment);
			
			
			if (!Checker.isEmpty(startTime)) {
				if (!Checker.isEmpty(sqlParams)){
					sqlParams += ",";
				}
				sqlParams += "car_statr_time='" + startTime + "'";
			}
			else{
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";	
				}
				sqlParams += "car_statr_time=null";
			}
			if (!Checker.isEmpty(stopTime)) {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";	
				}
				sqlParams += "car_stop_time='" + stopTime + "'";
			}else {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";	
				}
				sqlParams += "car_stop_time=null";
			}

			if (!Checker.isEmpty(orgcode)) {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";
				}
				sqlParams += "orgcode='" + orgcode + "'";
			}
				
			if (!Checker.isEmpty(inspection_time)) {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";
				}
				sqlParams += "annual_inspection_time='" + inspection_time + "'";
			}else{
				sqlParams += ",annual_inspection_time=null ";
			}
			
			if (!Checker.isEmpty(purchase_date)) {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";
				}
				sqlParams += "vehicle_purchase_date='" + purchase_date + "'";
			}else{
				sqlParams += " ,vehicle_purchase_date=null ";
			}
			if (!Checker.isEmpty(date_of_equipment)) {
				if (!Checker.isEmpty(sqlParams)) {
					sqlParams += ",";
				}
				sqlParams += "installation_date_of_equipment='"+ date_of_equipment + "'";
			}else{
				sqlParams += ",installation_date_of_equipment=null ";
			}
			//年检间隔
			String annual_inspection_interval=ac.getRequestParameter("cdms_vehiclebasicinformation.form.annual_inspection_interval");
			if(!Checker.isEmpty(annual_inspection_interval)){
				if(!Checker.isEmpty(sqlParams)){
					sqlParams += ",";
				}
				sqlParams +=" annual_inspection_interval = '"+annual_inspection_interval+"'";
			}else{
				sqlParams += " ,annual_inspection_interval="+1+"  ";
			}
			
			
			if (!Checker.isEmpty(sqlParams)) {
				String sql = "update cdms_vehiclebasicinformation set "+ sqlParams + " where id = '" + id + "'";
				db.execute(sql);
			}
		}

	}

	public int isRepeat(DB db, String id, String colName, String colValue)
			throws JDBCException {
		int rValue = 0;
		String sql = "select count(*) from cdms_vehiclebasicinformation where "
				+ colName + "='" + colValue + "' and id <>'" + id + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = mapList.getRow(0).getInt(0, 0);
		}
		return rValue;
	}
	/**
	 * 判断ID是否存在（是否是新增）
	 * @param db
	 * @param id
	 * @return
	 * @throws JDBCException 
	 */
	public int isAdd(DB db, String id) throws JDBCException{
		int rValue = 0;
		String sql = "select count(*) from cdms_vehiclebasicinformation where  id ='" + id + "'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = mapList.getRow(0).getInt(0, 0);
		}
		return rValue;
	}
}
