package com.cdms;
/**
 * 刘扬
 *保险表中保险到期时间更新时，更新车辆基础信息表中的相应保险到期时间字段
 * 
 * 
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class UpdateTerm_Insurance extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Logger log = Logger.getLogger(UpdateTerm_Insurance.class);

		// 获取表格
		Table table = ac.getTable("CDMS_INSURANCE");
		   UUID uuid=UUID.randomUUID();
		   //id
		   String insuranceid = ac.getRequestParameter("cdms_insurance.form.id");
		   // 保险金额
		    String insurance_amount=ac.getRequestParameter("cdms_insurance.form.insurance_amount");
		    //保险缴纳时间
			String insurance_payment_time=ac.getRequestParameter("cdms_insurance.form.insurance_payment_time");
			//汽车id
			String car_id1=ac.getRequestParameter("cdms_insurance.form.car_id");
			 log.debug(car_id1);
			//保险到期时间
			String term_insurance_time1=ac.getRequestParameter("cdms_insurance.form.term_insurance_time");
			//备注
			String notes=ac.getRequestParameter("cdms_insurance.form.notes");
			
			//如果保险到期时间修改了，则删除信息表中的保险数据
			if(!Checker.isEmpty(term_insurance_time1)){
				String sql = "delete from cdms_message where remind_type='3' and car_id='"+ car_id1 + "'";
				db.execute(sql);
			}
			//保险缴纳时间不是必填，判断是否为空
		    String insurance_payment_time11="";
		   if(!Checker.isEmpty(insurance_payment_time)){
			   insurance_payment_time11="='"+insurance_payment_time+"'"; 
			   insurance_payment_time="'"+insurance_payment_time+"'";
		   }else {
			   insurance_payment_time11=" is null";
			   insurance_payment_time=null;
		   }
		  //备注不是必填，判断是否为空   
		   String notes11="";
		   if(!Checker.isEmpty(notes)){
			   notes11="='"+notes+"'";  
		   }else {
			   notes11=" is null";
		   }
			

			 if(!Checker.isEmpty(insuranceid)){
				 
				
				 String  updatesql="update  CDMS_INSURANCE set insurance_amount="+insurance_amount+",insurance_payment_time="+insurance_payment_time+","
				 		+ "term_insurance_time='"+term_insurance_time1+"',notes='"+notes+"' where id='"+insuranceid+"'";
					db.execute(updatesql);
					
				}
			 else {
					
				   
				//判断数据是否重复   
				   String sqlinsurance = "select * from CDMS_INSURANCE where car_id='"+car_id1+"' and term_insurance_time='"+term_insurance_time1+"' "
							+ "and insurance_payment_time"+insurance_payment_time11+" and insurance_amount="+insurance_amount+""
							+ " and notes"+notes11+"";
					MapList mapListoil = db.query(sqlinsurance);
					if(mapListoil.size()>0){
						ac.getActionResult().addErrorMessage("数据重复");
						ac.getActionResult().setSuccessful(false);
					}
					else {
					
						String instersql="insert into CDMS_INSURANCE( id,car_id,insurance_payment_time,insurance_amount,term_insurance_time,notes)"
								+ "values('"+uuid+"','"+car_id1+"',"+insurance_payment_time+","+insurance_amount+",'"+term_insurance_time1+"','"+notes+"')";
					
						db.execute(instersql);
					
					} 
				 
			 }
		
		
		
		
		
		
		//db.save(table);
		TableRow tr=table.getRows().get(0);
		//获得车辆ID
		String car_id=tr.getValue("car_id");
		log.info("---------------------------------------------------------------------------"+car_id);
		
		upCar(db,car_id);
	}
	public void upCar(DB db,String car_id) throws JDBCException, ParseException {
		// 查詢保险费表
		// 按照降序查詢，將最大的保險提醒時間取出，放入車輛基礎信息表
		String mysql = "select term_insurance_time from cdms_insurance where car_id='"+car_id+"' order by term_insurance_time DESC";
		MapList mapList = db.query(mysql);
		Date date = new Date();
		if (!Checker.isEmpty(mapList)) {
			String term_insurance_time = mapList.getRow(0).get("term_insurance_time");
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");  
			date = format.parse(term_insurance_time);  
		}
		
		String sql="update cdms_VehicleBasicInformation set duration_of_insurance='"+date+"' where id='"+car_id+"'";
		db.execute(sql);
	}
}
