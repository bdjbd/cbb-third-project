package com.cdms;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * 油卡小票图片上传 
 * 油卡信息表
 */
import com.am.common.util.FileUtils;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class OilCardImgsSaveAction extends DefaultAction {
	Logger log = Logger.getLogger(UpdateMaintenance_Mileage.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//油卡信息表
		Table table=ac.getTable("CDMS_OILCARD");
		 log.debug("准备进入保存方法");
		    UUID uuid=UUID.randomUUID();
		   //id
		   String oilid = ac.getRequestParameter("cdms_oilcard.form.id");
		   // 油卡编号
		    String oil_card_number=ac.getRequestParameter("cdms_oilcard.form.oil_card_number");
		    //加油量
			String add_oil=ac.getRequestParameter("cdms_oilcard.form.add_oil");
			//汽车id
			String car_id=ac.getRequestParameter("cdms_oilcard.form.car_id");
			 log.debug(car_id);
			//加油时间
			String add_oil_time=ac.getRequestParameter("cdms_oilcard.form.add_oil_time");
			//驾驶员
			String driver_name=ac.getRequestParameter("cdms_oilcard.form.driver_name");
			 //加油时间不是必填，判断是否为空
		    String add_oil_time11="";
		   if(!Checker.isEmpty(add_oil_time)){
			   add_oil_time11="='"+add_oil_time+"'";  
			   add_oil_time="'"+add_oil_time+"'";
		   }else {
			   add_oil_time11=" is null";
			   add_oil_time=null;
		   }
		  //油卡编号不是必填，判断是否为空   
		   String oil_card_number111="";
		   if(!Checker.isEmpty(oil_card_number)){
			   oil_card_number111="='"+oil_card_number+"'";  
		   }else {
			   oil_card_number111=" is null";
		   }
		 if(!Checker.isEmpty(oilid)){
			 String  updatesql="update  CDMS_OILCARD set driver_name='"+driver_name+"',add_oil="+add_oil+","
			 		+ "oil_card_number='"+oil_card_number+"',add_oil_time="+add_oil_time+" where id='"+oilid+"'";
				db.execute(updatesql);
			}
		 else {
   
			   String sqoil = "select * from cdms_oilcard where car_id='"+car_id+"' and driver_name='"+driver_name+"' "
						+ "and add_oil_time"+add_oil_time11+" and add_oil="+add_oil+""
						+ " and oil_card_number"+oil_card_number111+"";
				MapList mapListoil = db.query(sqoil);
				if(mapListoil.size()>0){
					ac.getActionResult().addErrorMessage("数据重复");
					ac.getActionResult().setSuccessful(false);
				}
				else {
				
					String instersql="insert into cdms_oilcard( id,car_id,oil_card_number,add_oil,add_oil_time,driver_name)"
							+ "values('"+uuid+"','"+car_id+"','"+oil_card_number+"',"+add_oil+","+add_oil_time+",'"+driver_name+"')";
				
					db.execute(instersql);
				
				} 
			   
			   
			   
			   
			 
		 }
		 log.debug(oilid);
		// db.save(table);
		
		
		//获取主键
		TableRow tr=table.getRows().get(0);
		String id=tr.getValue("id");
		
       
        
		
		//上传的文件以临时文件的形式保存在本地服务器虚拟字段中
		
		//获取相关虚拟字段的文件路径(多图片)
		String url=new FileUtils().getFastUnitFilePathJSON("CDMS_OILCARD", "bdp_picture", id);
		
		//把路径存入真实的字段里
		String sql="update CDMS_OILCARD set oil_card_ticket='"+url+"' where id='"+id+"' ";
		
		//执行SQL
		db.execute(sql);
		
	}

}
