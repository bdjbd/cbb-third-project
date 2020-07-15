package com.ambdp.enterpriseRecruitment;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：qintao
 * @date 创建时间：2016年11月25日
 * @explain 说明 : 企业招聘发布撤销
 */
public class EnterpriseRecruitmentRelease extends DefaultAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM lxny_enterprise_recruitment WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("data_status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE lxny_enterprise_recruitment SET data_status='1',release_time='now()' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 发布-->草稿
				
				String SQL="select * from lxny_myapplication where enterprise_recruitment_id='"+Id+"'";
				MapList maps=db.query(SQL);
				
				if (!Checker.isEmpty(maps)) {
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("该条信息已有应聘者，无法撤销");
				}else{
					updateSQL="UPDATE lxny_enterprise_recruitment SET data_status='0',release_time=null WHERE id='"+Id+"'";
				}
				
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		return ac;
	}
}	
