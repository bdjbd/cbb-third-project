package com.cdms;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class PreservationToolaction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 获取表格
				Table table = ac.getTable("cdms_DangerousTools");
				   UUID uuid=UUID.randomUUID();
				   //id
				   String insuranceid = ac.getRequestParameter("cdms_dangeroustools.form.id");
				   // 工具名字
				    String tool_name=ac.getRequestParameter("cdms_dangeroustools.form.tool_name");
				    //工具数量
					String tool_number=ac.getRequestParameter("cdms_dangeroustools.form.tool_number");
					//汽车id
					String car_id1=ac.getRequestParameter("cdms_dangeroustools.form.car_id");
					
					//负责人
					String principal=ac.getRequestParameter("cdms_dangeroustools.form.principal");
					//领取时间
					String receivetime=ac.getRequestParameter("cdms_dangeroustools.form.receivetime");
					//变更记录
					String change_log=ac.getRequestParameter("cdms_dangeroustools.form.change_log");
					
					//变革记录不是必填，判断是否为空
				    String change_log11="";
				   if(!Checker.isEmpty(change_log)){
					   change_log11="='"+change_log+"'"; 
					   change_log="'"+change_log+"'";
				   }else {
					   change_log11=" is null";
					   change_log=null;
				   }
				  
					

					 if(!Checker.isEmpty(insuranceid)){
						 
						
						 String  updatesql="update  cdms_DangerousTools set tool_number="+tool_number+",principal='"+principal+"',"
						 		+ "receive_time='"+receivetime+"',change_log="+change_log+",tool_name='"+tool_name+"' where id='"+insuranceid+"'";
							db.execute(updatesql);
							
						}
					 else {
							
						   
						//判断数据是否重复   
						   String sqlinsurance = "select * from cdms_DangerousTools where car_id='"+car_id1+"' and tool_number="+tool_number+" "
									+ "and change_log"+change_log11+" and principal='"+principal+"'"
									+ " and receive_time='"+receivetime+"' and tool_name='"+tool_name+"'";
							MapList mapListoil = db.query(sqlinsurance);
							if(mapListoil.size()>0){
								ac.getActionResult().addErrorMessage("数据重复");
								ac.getActionResult().setSuccessful(false);
							}
							else {
							
								String instersql="insert into cdms_DangerousTools( id,car_id,tool_number,change_log,principal,receive_time,tool_name)"
										+ "values('"+uuid+"','"+car_id1+"',"+tool_number+","+change_log+",'"+principal+"','"+receivetime+"','"+tool_name+"')";
							
								db.execute(instersql);
							
							} 
						 
					 }
				
	
	}
}
