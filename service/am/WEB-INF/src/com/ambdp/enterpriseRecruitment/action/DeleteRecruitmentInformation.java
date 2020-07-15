package com.ambdp.enterpriseRecruitment.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：qintao
 * @date 创建时间：2016年11月25日
 * @explain 说明 : 删除验证
 */

public class DeleteRecruitmentInformation extends DefaultAction{
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String[] list = ac.getRequestParameters("_s_lxny_enterprise_recruitment.list");
		String[] Id = ac.getRequestParameters("lxny_enterprise_recruitment.list.id");
		if (!Checker.isEmpty(list)) {
			for (int i = 0; i < list.length; i++) {
				if ("1".equals(list[i])) {
					
					String SQL="select * from lxny_myapplication where enterprise_recruitment_id='"+Id[i]+"'";
					String deleteSQL="delete from lxny_enterprise_recruitment where id='"+Id[i]+"'";
					MapList map=db.query(SQL);
					
					if (!Checker.isEmpty(map)) {
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage("该条信息已有应聘者，无法删除");
					}else{
						db.execute(deleteSQL);
					}
					
				}
			}
		}
	}
}
