package com.cdms.generalStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class GeneralStatementUI implements UnitInterceptor{
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData = unit.getData();
		DBManager db = new DBManager();	
		String userid=ac.getVisitor().getUser().getId();
		logger.info("uerid++++++++++++++++++++++"+userid);
		
//列設置 状态1显示 状态0不显示		
		String sql="select * from cdms_TotalReportColumnSettings  where user_id='"+userid+"'";
		MapList mlist=db.query(sql);
		if (!Checker.isEmpty(mlist)) {
			//判断车牌号在列设置表里的状态是否为0
		  String carName=mlist.getRow(0).get("number_plate");
		     logger.info("carId++++++++++++++++++++++"+carName);
		     if(carName.equals("0")) {
		    	 logger.info("carId2++++++++++++++++++++++"+carName);
		    	 unit.getElement("license_plate_number").setShowMode(ElementShowMode.REMOVE);
		    	 
		     }
		   //判断机构在列设置表里的状态是否为0
			  String oegcode=mlist.getRow(0).get("oegcode");
			     logger.info("carId++++++++++++++++++++++"+oegcode);
			     if(oegcode.equals("0")) {
			    	 logger.info("carId2++++++++++++++++++++++"+carName);
			    	 unit.getElement("orgname").setShowMode(ElementShowMode.REMOVE);
			    	 
			     }
			 //判断驾驶员在列设置表里的状态是否为0
				  String driver=mlist.getRow(0).get("driver");
				     logger.info("driver++++++++++++++++++++++"+driver);
				     if(driver.equals("0")) {
				    	 logger.info("driver++++++++++++++++++++++"+driver);
				    	 unit.getElement("driver").setShowMode(ElementShowMode.REMOVE);
				    	 
             }
				   //判断超速次数在列设置表里的状态是否为0
					  String overspeed_count=mlist.getRow(0).get("overspeed_count");
					     logger.info("overspeed_count++++++++++++++++++++++"+overspeed_count);
					     if(overspeed_count.equals("0")) {
					    	 logger.info("overspeed_count++++++++++++++++++++++"+overspeed_count);
					    	 unit.getElement("speeding_sum").setShowMode(ElementShowMode.REMOVE);
					    	 
	             } 
			//判断疲劳驾驶次数在列设置表里的状态是否为0
						  String fatigue_driving_count=mlist.getRow(0).get("fatigue_driving_count");
						     logger.info("fatigue_driving_count++++++++++++++++++++++"+fatigue_driving_count);
						     if(fatigue_driving_count.equals("0")) {
						    	 logger.info("fatigue_driving_count++++++++++++++++++++++"+fatigue_driving_count);
						    	 unit.getElement("fatigue_sum").setShowMode(ElementShowMode.REMOVE);
						    	 
		             }
		//判断急加速次数在列设置表里的状态是否为0
							 String rapid_acceleration_count=mlist.getRow(0).get("rapid_acceleration_count");
							    logger.info("rapid_acceleration_count++++++++++++++++++++++"+rapid_acceleration_count);
							    if(rapid_acceleration_count.equals("0")) {
							    	 logger.info("fatigue_driving_count++++++++++++++++++++++"+rapid_acceleration_count);
							    	 unit.getElement("rapid_acceleration_sum").setShowMode(ElementShowMode.REMOVE);
							    	 
			             }						     
       //判断急减速次数在列设置表里的状态是否为0
						String rapid_deceleration_count=mlist.getRow(0).get("rapid_deceleration_count");
								logger.info("rapid_deceleration_count++++++++++++++++++++++"+rapid_deceleration_count);
								 if(rapid_deceleration_count.equals("0")) {
								   logger.info("rapid_deceleration_count++++++++++++++++++++++"+rapid_deceleration_count);
								   unit.getElement("deceleration_sum").setShowMode(ElementShowMode.REMOVE);
								    	 
								 }
			
	//判断急转弯次数在列设置表里的状态是否为0
			String sharp_turn_count=mlist.getRow(0).get("sharp_turn_count");
								logger.info("sharp_turn_count++++++++++++++++++++++"+sharp_turn_count);
								 if(sharp_turn_count.equals("0")) {
								logger.info("sharp_turn_count++++++++++++++++++++++"+sharp_turn_count);
								unit.getElement("corner_sum").setShowMode(ElementShowMode.REMOVE);
											    	 
							    }		
			
	//判断变道次数在列设置表里的状态是否为0
			String steep_road_count=mlist.getRow(0).get("steep_road_count");
						 if(steep_road_count.equals("0")) {
						unit.getElement("lane_change_sum").setShowMode(ElementShowMode.REMOVE);
																	    	 
								   }			
  //判断行车总里程在列设置表里的状态是否为0	
	      String total_mileage=mlist.getRow(0).get("total_mileage");
					  if(total_mileage.equals("0")) {
						unit.getElement("mileage_oil_sum").setShowMode(ElementShowMode.REMOVE);	
					   }			
//判断工作里程/油耗在列设置表里的状态是否为0	
       String work_mileage=mlist.getRow(0).get("work_mileage");
		  if(work_mileage.equals("0")) {
			unit.getElement("work_oil").setShowMode(ElementShowMode.REMOVE);	
		  }
//判断非工作里程/油耗在列设置表里的状态是否为0	
	       String non_work_mileage=mlist.getRow(0).get("non_work_mileage");
			  if(non_work_mileage.equals("0")) {
				unit.getElement("notwork_oil_sum").setShowMode(ElementShowMode.REMOVE);	
			  }
//判断作业里程/油耗在列设置表里的状态是否为0	
		      String operating_mileage=mlist.getRow(0).get("operating_mileage");
				if(operating_mileage.equals("0")) {
				unit.getElement("task_oil_sum").setShowMode(ElementShowMode.REMOVE);	
				  }		
//判断非作业里程/油耗在列设置表里的状态是否为0	
			      String non_operation_mileage=mlist.getRow(0).get("non_operation_mileage");
			      logger.info("non_operation_mileage++++++++++++++++++++++"+non_operation_mileage);   
					if(non_operation_mileage.equals("0")) {
					unit.getElement("notask_oil_sum").setShowMode(ElementShowMode.REMOVE);	
		
					}
					
//判断违章次数在列设置表里的状态是否为0	
				String peccancy_count=mlist.getRow(0).get("peccancy_count");
				logger.info("peccancy_count++++++++++++++++++++++"+peccancy_count);
					if(peccancy_count.equals("0")) {
				unit.getElement("illegal_sum").setShowMode(ElementShowMode.REMOVE);	
			
					}
//判断怠速异常总时长在列设置表里的状态是否为0						
					String Idle_speed_abnormal_total_lengt=mlist.getRow(0).get("idle_speed_abnormal_total_lengt");
					logger.info("Idle_speed_abnormal_total_lengt++++++++++++++++++++++"+Idle_speed_abnormal_total_lengt);
					if(Idle_speed_abnormal_total_lengt.equals("0")) {
						 logger.info("Idle_speed_abnormal_total_lengt++++++++++++++++++++++"+Idle_speed_abnormal_total_lengt);
				           unit.getElement("idling_abnormality").setShowMode(ElementShowMode.REMOVE);	
					}
					//判断非工作时间用车时长在列设置表里的状态是否为0						
					String total_length_of_non_working_tim=mlist.getRow(0).get("total_length_of_non_working_tim");
					if(total_length_of_non_working_tim.equals("0")) {
				unit.getElement("notwork_time_car_sum").setShowMode(ElementShowMode.REMOVE);	
			
					}					
					//判断区域外时长在列设置表里的状态是否为0						
					String total_length_of_regional_extern=mlist.getRow(0).get("total_length_of_regional_extern");
					if(total_length_of_regional_extern.equals("0")) {
				unit.getElement("region_out_tim").setShowMode(ElementShowMode.REMOVE);	
			
					}					
					//判断进出区域次数在列设置表里的状态是否为0						
					String import_and_export_area_count=mlist.getRow(0).get("import_and_export_area_count");
					if(import_and_export_area_count.equals("0")) {
				unit.getElement("region_out_in_sum").setShowMode(ElementShowMode.REMOVE);	
			
					}					
						
					//判断考勤总时长在列设置表里的状态是否为0						
					String total_time_attendance=mlist.getRow(0).get("total_time_attendance");
					if(total_time_attendance.equals("0")) {
				unit.getElement("work_time_sum").setShowMode(ElementShowMode.REMOVE);	
			
					}	
		}
		
		
		
		
		
		return unit.write(ac);
	}

}
