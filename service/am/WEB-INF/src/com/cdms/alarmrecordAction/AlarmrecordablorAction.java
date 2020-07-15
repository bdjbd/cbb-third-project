package com.cdms.alarmrecordAction;

import org.apache.log4j.Logger;

import com.cdms.UpdateMaintenance_Mileage;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/*
 * 异常用车类报警批量已读
 * */
public class AlarmrecordablorAction extends DefaultAction{
	public void doAction(DB db, ActionContext ac) throws Exception {
		Logger log = Logger.getLogger(UpdateMaintenance_Mileage.class);
		         // 变量ids
				String ids = "";
				String msg="无效的操作!";
				// 获取列表选择列		
		String[] List = ac.getRequestParameters("_s_cdms_alarmrecord_abnormal.list");

		// 获取主键
		String[] id = ac.getRequestParameters("cdms_alarmrecord_abnormal.list.id.k");
		if (!Checker.isEmpty(id)) {
			for (int i = 0; i < id.length; i++) {
				if ("1".equals(List[i])) {
					 log.info("id------------------------------------------------------------>"+id[i]);
					 String sql = "update cdms_alarmrecord set operation_status='1' where id='"+id[i]+"'";
					 db.execute(sql);
				}
		  
		
			}
				
		}

	}


}
