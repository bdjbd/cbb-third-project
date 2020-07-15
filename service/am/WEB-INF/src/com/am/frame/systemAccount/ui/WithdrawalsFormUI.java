package com.am.frame.systemAccount.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月20日 下午6:58:32 
 * @version 1.0   
 */
public class WithdrawalsFormUI implements UnitInterceptor {

	/**
	 *  提现表单ui
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		
		String outAccountId = ac.getRequestParameter("outaccountid");
		String state = ac.getRequestParameter("state");

		String  outAccountClass = ac.getRequestParameter("outaccountclass");
				
		
		if(!Checker.isEmpty(outAccountClass)){
			ac.setSessionAttribute("virementui.outaccountclass", outAccountClass);
		}else{
			outAccountClass =(String) ac.getSessionAttribute("virementui.outaccountclass");
		}
		
		if(!Checker.isEmpty(outAccountId)){
			ac.setSessionAttribute("ss.outaccountid", outAccountId);
		}else{
			outAccountId =(String) ac.getSessionAttribute("ss.outaccountid");
		}
		
		if(!Checker.isEmpty(state)){
			ac.setSessionAttribute("ss.state", state);
		}else{
			state =(String) ac.getSessionAttribute("ss.state");
		}
		
		
		
		String sql = "select msac.id as out_account_id,msac.cash_fee_ratio from mall_system_account_class as msac "
				+ "left join mall_account_info as mai on mai.a_class_id = msac.id "
				+ "where msac.sa_code = '"+outAccountId+"' and mai.member_orgid_id = '"+ac.getVisitor().getUser().getOrgId()+"'";
		
		DB db = DBFactory.newDB();
		
		MapList list = db.query(sql);
		
		String transferFeeRatio = "";
		
		if(list.size()>0){
			
			transferFeeRatio = list.getRow(0).get("cash_fee_ratio");
			
			if(unit.getElement("cash_fee_ratio")!=null){
				unit.getElement("cash_fee_ratio").setDefaultValue(transferFeeRatio);
			}
			if(unit.getElement("cash_fee_ratio")!=null){
				unit.getElement("cash_fee_ratio").setDefaultValue(transferFeeRatio);
			}
			//unit.getElement("out_account_id").setDefaultValue(list.getRow(0).get("out_account_id"));
			
		}
		 
		return unit.write(ac);
	}

}
