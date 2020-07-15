package com.cdms.alarmrecordUi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class AlarmrecordListUi implements UnitInterceptor{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {

		// 查询报警状态为0的报警次数(未读次数)
		String read_numsql ="";
		// 查询报警总数（报警总数）
		String give_alarm_numsql ="";
		String orgcode = ac.getVisitor().getUser().getOrgId();
		MapList unitData = unit.getData();
		if (!Checker.isEmpty(unitData)){
		for (int i = 0; i < unitData.size(); i++) {

			// 获取当前行的id
			unit.getElement("scan").setDefaultValue(i, "查看");
			String alarm_type = unitData.getRow(i).get("alarm_type");
			logger.info("++++++++++++++-----------" + alarm_type);
			
          if(!Checker.isEmpty(alarm_type)) {
				// 查询报警状态为0的报警次数(未读次数)
			 read_numsql = "select count(*) from  CDMS_ALARMRECORD ca,CDMS_VEHICLEBASICINFORMATION cvb  \r\n" + 
			 		"where  1=1  and ca.car_id =cvb.id and ca.operation_status='0' and cvb.orgcode like '"+orgcode+"%' and ca.alarm_type ='"+alarm_type+"'";
				// 查询报警总数（报警总数）
			 give_alarm_numsql = "select count(operation_status) from  CDMS_ALARMRECORD ca,CDMS_VEHICLEBASICINFORMATION cvb   \r\n" + 
			 		"			 		where  1=1  and ca.car_id =cvb.id  and cvb.orgcode like '"+orgcode+"%'  and ca.alarm_type ='"+alarm_type+"'";
	
			DB db = null;

			db = DBFactory.newDB();
			MapList give_alarm_numlist = db.query(give_alarm_numsql);
			MapList  read_numlist = db.query(read_numsql);
		
			String read_numsqllist = read_numlist.getRow(0).get("count");
			String give_alarm_numsqllist = give_alarm_numlist.getRow(0).get("count");
			logger.info("------------------" + read_numsqllist);
			logger.info("------------------" + give_alarm_numsqllist);

			
			unit.getElement("scan").setDefaultValue(i, "查看");
			if(alarm_type.equals("1")) {
				logger.info("++++++++++++++-----------11111111" );
				unit.getElement("scan").setLink(i,"/cdms/cdms_alarmrecord_breakdown.do?m=s&cdms_alarmrecord_breakdown.alarm_type=$D{alarm_type}");
				
			}
			if(alarm_type.equals("2")) {
				logger.info("++++++++++++++-----------22222222" );
				unit.getElement("scan").setLink(i,"/cdms/cdms_alarmrecord_violation.do?m=s&cdms_alarmrecord_violation.alarm_type=$D{alarm_type}");
				
			}
			if(alarm_type.equals("3")) {
				logger.info("++++++++++++++-----------3333333" );
				unit.getElement("scan").setLink(i,"/cdms/cdms_alarmrecord_abnormal.do?m=s&cdms_alarmrecord_abnormal.alarm_type=$D{alarm_type}");
				
			}
			
			unit.getElement("give_alarm_num").setDefaultValue(i, give_alarm_numsqllist);
			unit.getElement("unread_num").setDefaultValue(i, read_numsqllist);
		}
		}
		}
		return unit.write(ac);
	
	}


}
