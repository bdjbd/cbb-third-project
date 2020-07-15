package com.cdms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;

/**
 *
 * 
 * @author lh
 *
 */
public class SendReport implements Job {

	private  Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		logger.info("开始执行计划任务-----同步订单！");
		DBManager db = new DBManager();		
	
		String Implementation = "";
		String id="";
		String uuid="";	
		String stater_time="";
	    String ssql="select * from task_execution_record order by creete_time desc";
		MapList mList = db.query(ssql);
		if(!Checker.isEmpty(mList)){
			stater_time=mList.getRow(0).get("end_time");
		}else{
			 stater_time=Var.get("Last_synchronization_time");
		}
		logger.info(stater_time);
		MapList mapList = db
				.query("select * from synchronous_order ");
		if(!Checker.isEmpty(mapList)){
		for (int i = 0; i < mapList.size(); i++) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			
			Implementation = mapList.getRow(i).get("implementation");	
			String isql="INSERT INTO task_execution_record (id,creete_time,implementation,rid,state,stater_time,end_time) values('"+uuid+"',now(),'"+Implementation+"','"+id+"','0','"+stater_time+"',now())";
			db.execute(isql);
		}
		}
		logger.info("计划任务执行完成-----！");
	}

	/**
	 * 获取当天的日期
	 * 
	 * @return
	 */
	public static String getToDay() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = sdf.format(date);
		return dateNowStr;
	}
	 public static String addDateMinut( int hour){   
		
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = null;   
	        try {   
	            date = new Date();   
	        } catch (Exception ex) {   
	            ex.printStackTrace();   
	        }   
	        if (date == null)   
	            return "";   
	        System.out.println("front:" + format.format(date)); //显示输入的日期  
	        Calendar cal = Calendar.getInstance();   
	        cal.setTime(date);   
	        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)-hour);// 24小时制   
	        date = cal.getTime();   
	        System.out.println("after:" + format.format(date));  //显示更新后的日期 
	        cal = null;   
	        return format.format(date);   

	    }
}
