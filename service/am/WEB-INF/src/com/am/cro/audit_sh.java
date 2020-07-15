package com.am.cro;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;

/**
 * 广告系统审核，
 * 
 * @author 刘浩
 *
 */
public class audit_sh extends DefaultAction {
	@Override
	

		
		public ActionContext execute(ActionContext ac) throws Exception {
		   
		    
			String id = ac.getRequestParameter("am_advertising_system.formsh.id");
			String audit_content = ac.getRequestParameter("am_advertising_system.formsh.audit_content");
			String actionParam = ac.getActionParameter();
			DB db = DBFactory.getDB();
			String updateSQL = "";
			//草稿
			if ("0".equals(actionParam)) {
				updateSQL = "UPDATE am_advertising_system set audit_status='0',audit_content='"+audit_content+"' ,audit_time=now() WHERE id='" + id + "'";	
			//审核中
			
			//审核通过
			} else if ("2".equals(actionParam)) {
				updateSQL = "UPDATE am_advertising_system set audit_status='2' ,audit_content='"+audit_content+"' ,audit_time=now() WHERE id='" + id + "'";	
			}else if("1".equals(actionParam)){
				String[] ids = ac.getRequestParameters("am_advertising_system.list.id.k");
				String[] idSelects = ac.getRequestParameters("_s_am_advertising_system.list");							
				for (int i = 0; i < ids.length; i++) {
					if ("1".equals(idSelects[i])) {
						updateSQL = "UPDATE am_advertising_system set audit_status='1' WHERE id='" +ids[i] + "'";												
					}
				}				
						
								
			}
			db.execute(updateSQL);
			ac.getActionResult().setSuccessful(true);
		
			return ac;
		}

	
}
