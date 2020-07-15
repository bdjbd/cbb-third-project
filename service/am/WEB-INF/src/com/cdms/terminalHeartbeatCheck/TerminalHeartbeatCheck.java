package com.cdms.terminalHeartbeatCheck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.cdms.IllegalInquiry;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;

public class TerminalHeartbeatCheck implements Job{
	private  Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		DBManager db = new DBManager();	
		double literal_time=0;
		double caseovertime=0;
		//获取当前系统设置接单超时变量值
		String case_overtime = Var.get("case_overtime");
		if(!Checker.isEmpty(case_overtime)) {
			caseovertime=Double.parseDouble(case_overtime);
		}
		//派单超时sql
		 String casesql="update cdms_case  set case_state='0'  where (extract(epoch from now())-extract(epoch from distribution_time))>"+caseovertime+"  and case_state='2'";
		 db.execute(casesql);
		 String casesql1 = "update cdms_caseorderpersonnel set member_id ='' where case_id in (select id from cdms_case where case_state='0')";
		 db.execute(casesql1);
		//获取变量心跳检查规定时间
		String heartbeat = Var.get("heartbeat");
		if(!Checker.isEmpty(heartbeat)){
			literal_time=Double.parseDouble(heartbeat);
		}
		//终端心跳检查sql(不检查维修，维护，报废车辆)
		String  sql="select id,license_plate_number from cdms_vehiclebasicinformation where (extract(epoch from now())-extract(epoch from last_heartbeat_time))>"+literal_time+" and vehicle_state not in('2','3','4') order by license_plate_number";
		logger.info("检查离线车辆（当前时间与最后心跳时间间隔超过"+literal_time+"秒的车辆）的sql="+sql);
		MapList  mList = db.query(sql);
		if (!Checker.isEmpty(mList)&&mList.size()>0) {
			for (int i = 0; i < mList.size(); i++) {
				String id = mList.getRow(i).get("id");
//				管道id，tcpserverid由王成阳tcp那维护2018-04-27
//				String  updatesql="update cdms_vehiclebasicinformation set  vehicle_state='8',channelid='',tcpserverid='' where id='"+id+"'";
				String  updatesql="update cdms_vehiclebasicinformation set  vehicle_state='8' where id='"+id+"'";
				db.execute(updatesql);
				
				//车辆离线后，结束车辆运行情况表未结束的统计
				String runningSql = "update cdms_vehiclesrunningcondition set acc_status='1' where car_id='"+id+"' and acc_status='2'";
				db.execute(runningSql);
				//车辆离线后，结束车辆闲置情况表未结束的统计
				String idleSql = "update cdms_vehicleidling set acc_status='2' where car_id='"+id+"' and acc_status='1'";
				db.execute(idleSql);
				//车辆离线后，结束车辆非工作时间用车情况表未结束的统计
				String notWorkSql = "update cdms_NotworkingVehicleDetail set status='0' "
							+ "where car_id='" + id+"' and status='1'";
				db.execute(notWorkSql);
				//车辆离线后，结束车辆进出区域明细表未结束的统计
				String inOutAreaSql = "update cdms_enclosurerecord set status='1' where car_id='"+ id + "' and status='0'";
				db.execute(inOutAreaSql);
				//车辆离线后，结束车辆报警表未结束的统计
				String alarmSql = "update cdms_AlarmRecord set time_state='2' "+ " where car_id='"
							+ id+ "' and time_state='1'";
				db.execute(alarmSql);
			}
		}
		//车辆所属围栏不存在时，结束车辆进出区域明细表中未结束的统计
		String checkFence = "update cdms_enclosurerecord set status='1' where car_id in (select car_id from cdms_enclosurerecord where status='0' and fence_id not in (select id from cdms_electronicfence))";
		db.execute(checkFence);
		//车辆所属围栏不存在时，结束车辆进出区域报警表中未结束的统计
		String stopAreaAlarm = "update cdms_AlarmRecord set time_state='2' where car_id in (select car_id from cdms_enclosurerecord where status='0' and fence_id not in (select id from cdms_electronicfence))"
				+ "and time_state='1' and fault_class_alarm_type='32'";
		db.execute(stopAreaAlarm);
		
		//获取变量中的违章查询日和时间，如果当前时间=变量设置的时间，则执行违章查询
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);//本月第 N 天
		int varDay = Var.getInt("illegal_check_day", 1);//获取变量中设置的违章检查日
		if(day==varDay){
			
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(calendar.getTime());
			
			String varTime = Var.get("illegal_check_time");//获取变量中设置的违章检查时间
			varTime = date+" "+varTime;
			SimpleDateFormat varformat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				long vartime = varformat.parse(varTime).getTime();//变量时间
				long current = calendar.getTime().getTime();//当前时间
				long start = vartime-2*60*1000;
				long end = vartime+2*60*1000;
				logger.info("IllegalInquiry.getInstance().date====="+IllegalInquiry.getInstance().date);
				//当前时间大于变量时间前2分钟，小于后2分钟，且当前没有查询过违章
				if(current>start&&current<end&&!date.equals(IllegalInquiry.getInstance().date)){
					//违章查询
					logger.info("正在进行违章查询。。。start");
					IllegalInquiry.getInstance().illegal();
					IllegalInquiry.getInstance().date = format.format(calendar.getTime());
					logger.info("违章查询完毕。。。end");
				}	
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

}
