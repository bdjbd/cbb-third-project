package com.ambdp.enterpriseRecruitment.action;

import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.context.ActionContext;
import com.fastunit.framework.util.LManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：
 * @date 创建时间：2016年10月26日 下午6:54:51
 * @explain 说明 :  工资金额类型转换   
 */
public class SaveRecruitmentInformation extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table ta=ac.getTable("lxny_enterprise_recruitment");
		db.save(ta);
		
		String id=ta.getRows().get(0).getValue("id");
		//开始时间
		String start_date=ta.getRows().get(0).getValue("start_date");
		
		boolean isValie=LManager.checkExpiresDate(start_date);
		
		if(!isValie){
			
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("开始时间不能小于当前时间");
			
			return;
		}
		
		Float wages_number=Float.parseFloat(ta.getRows().get(0).getValue("wages_number"));
		
		VirementManager vm=new VirementManager();
		
		Long wn=VirementManager.changeY2F(wages_number+"");
		
		String SQL="UPDATE lxny_enterprise_recruitment SET wages_number='"+wn+"' WHERE id='"+id+"' "; 
		
		db.execute(SQL);
		ac.setSessionAttribute("am_bdp.lxny_enterprise_recruitment.form.id", id);
	}
		
}
