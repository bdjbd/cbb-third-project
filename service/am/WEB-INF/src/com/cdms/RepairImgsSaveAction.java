package com.cdms;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * 上传维修明细图片
 * 
 * 维修表
 */
import com.am.common.util.FileUtils;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class RepairImgsSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//维修信息表
		Table table=ac.getTable("CDMS_CARREPAIRRRECORDS");
		Logger log = Logger.getLogger(UpdateTerm_Insurance.class);
		//db.save(table);
		  UUID uuid=UUID.randomUUID();
		   //id
		   String repairid = ac.getRequestParameter("cdms_carrepairrrecords.form.id");
		   log.debug(repairid);
		   // 维修费
		    String repair_fees=ac.getRequestParameter("cdms_carrepairrrecords.form.repair_fees");
		    //维修时间
			String repair_time=ac.getRequestParameter("cdms_carrepairrrecords.form.repair_time");
			//汽车id
			String car_id=ac.getRequestParameter("cdms_carrepairrrecords.form.car_id");
			
			//维修类型
			String repair_type=ac.getRequestParameter("cdms_carrepairrrecords.form.repair_type");
			//人员名册
			String member_id=ac.getRequestParameter("cdms_carrepairrrecords.form.member_id");	
			
			//维修时间不是必填，判断是否为空
		    String repair_time11="";
		   if(!Checker.isEmpty(repair_time)){
			   repair_time11="='"+repair_time+"'"; 
			   repair_time="'"+repair_time+"'";
		   }else {
			   repair_time11=" is null";
			   repair_time=null;
		   }
	
		 if(!Checker.isEmpty(repairid)){
				
			 String  updatesql="update  CDMS_CARREPAIRRRECORDS set repair_fees="+repair_fees+",repair_time="+repair_time+","
			 		+ "repair_type='"+repair_type+"',member_id='"+member_id+"' where id='"+repairid+"'";
				db.execute(updatesql);
			 
			}
		 else {
			//判断是否数据重复	   
			   String sqoil = "select * from CDMS_CARREPAIRRRECORDS where car_id='"+car_id+"' and repair_fees="+repair_fees+" "
						+ "and repair_time"+repair_time11+" and repair_type='"+repair_type+"'"
						+ " and member_id='"+member_id+"'";
	        MapList mapListoil = db.query(sqoil);
				if(mapListoil.size()>0){
					ac.getActionResult().addErrorMessage("数据重复");
					ac.getActionResult().setSuccessful(false);
				}
				else {
				
					String instersql="insert into CDMS_CARREPAIRRRECORDS( id,car_id,repair_fees,repair_time,repair_type,member_id)"
							+ "values('"+uuid+"','"+car_id+"',"+repair_fees+","+repair_time+",'"+repair_type+"','"+member_id+"')";
				
					db.execute(instersql);
				
				} 
		 }
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//获取主键
		TableRow tr=table.getRows().get(0);
		String id=tr.getValue("id");
		
		
		//上传的文件以临时文件的形式保存在本地服务器虚拟字段中
		
		//获取相关虚拟字段的文件路径(多图片)
		String url=new FileUtils().getFastUnitFilePathJSON("CDMS_CARREPAIRRRECORDS", "bdp_repair_picture", id);
		
		//把路径存入真实的字段里
		String sql="update CDMS_CARREPAIRRRECORDS set car_repair_picture='"+url+"' where id='"+id+"' ";
		
		//执行SQL
		db.execute(sql);
		
	}

}
