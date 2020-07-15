package com.cdms.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 总报表列设置
 * 刘扬
 */
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
/*
 * 
 * 
 * 总报表列设置 刘扬
 */
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class TotalReportColumnSettings extends DefaultAction {

	public void doAction(DB db, ActionContext ac) throws Exception {
		Logger logger = LoggerFactory.getLogger(getClass());
		// 得到表单
		Table table = ac.getTable("cdms_TotalReportColumnSettings");
		System.err.println(table);
		System.err.println(table);
		System.err.println(table);
		// 获得当前登录人id
		String user_id = ac.getVisitor().getUser().getId();
		System.err.println(user_id);
		System.err.println(user_id);
		System.err.println(user_id);
		// 获取表单中的数据
		// 车牌号
		int number_plate = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.number_plate"));
		// 急加速次数
		int rapid_acceleration_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.rapid_acceleration_count"));
		// 驾驶员
		int driver = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.driver"));
		// 急减速次数
		int rapid_deceleration_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.rapid_deceleration_count"));
		// 机构
		int oegcode = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.oegcode"));
		// 急转弯次数
		int sharp_turn_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.sharp_turn_count"));
		// 超速次数
		int overspeed_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.overspeed_count"));
		// 急变道次数
		int steep_road_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.steep_road_count"));
		// 疲劳驾驶次数
		int fatigue_driving_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.fatigue_driving_count"));
		// 行车总里程
		int total_mileage = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.total_mileage"));
		// 工作里程
		int work_mileage = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.work_mileage"));
		// 非工作里程
		int non_work_mileage = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.non_work_mileage"));
		// 作业里程
		int operating_mileage = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.operating_mileage"));
		// 非作业里程
		int non_operation_mileage = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.non_operation_mileage"));
		// 违章次数
		int peccancy_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.peccancy_count"));
		// 怠速异常总时长
		int idle_speed_abnormal_total_lengt = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.idle_speed_abnormal_total_lengt"));
		// 非工作时间用车
		int total_length_of_non_working_tim = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.total_length_of_non_working_tim"));
		// 区域外用车总时长
		int total_length_of_regional_extern = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.total_length_of_regional_extern"));
		// 进出区域总次数
		int import_and_export_area_count = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.import_and_export_area_count"));
		// 考勤总时长
		int total_time_attendance = Integer.parseInt(ac.getRequestParameter("totalreportcolumnsettings.form.total_time_attendance"));
		
		String sql="select id from cdms_TotalReportColumnSettings where user_id ='"+user_id+"'";
		DBManager db1=new DBManager();
		//判断是否查到了值
		boolean b=db1.queryToJSON(sql).length()!=0;
		System.err.println(db1.queryToJSON(sql).length());
		//判断sql1
		String sql1="";
		if(!b){
			db.save(table);
		}else{
			sql1="update cdms_TotalReportColumnSettings set  number_plate='"+number_plate+"',rapid_acceleration_count='"+rapid_acceleration_count+"',driver='"+driver+"',rapid_deceleration_count='"+rapid_deceleration_count+"'"
					+ ",oegcode='"+oegcode+"',sharp_turn_count='"+sharp_turn_count+"',overspeed_count='"+overspeed_count+"',steep_road_count='"+steep_road_count+"',fatigue_driving_count='"+fatigue_driving_count+"'"
							+ ",total_mileage='"+total_mileage+"',work_mileage='"+work_mileage+"',non_work_mileage='"+non_work_mileage+"',operating_mileage='"+operating_mileage+"',non_operation_mileage='"+non_operation_mileage+"',peccancy_count='"+peccancy_count+"'"
									+ ",idle_speed_abnormal_total_lengt='"+idle_speed_abnormal_total_lengt+"',total_length_of_non_working_tim='"+total_length_of_non_working_tim+"',import_and_export_area_count='"+import_and_export_area_count+"',total_time_attendance='"+total_time_attendance+"',total_length_of_regional_extern='"+total_length_of_regional_extern+"'"
											+ " where user_id='"+user_id+"' ";
			db.execute(sql1);
		}
		System.err.println(sql1);
		System.err.println(sql1);
		System.err.println(sql1);
		System.err.println(sql1);
		System.err.println(sql1);
	}
}
