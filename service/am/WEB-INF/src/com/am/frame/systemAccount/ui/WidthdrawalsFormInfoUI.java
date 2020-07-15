package com.am.frame.systemAccount.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

//提现ui
public class WidthdrawalsFormInfoUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception 
	{
		
		MapList unitData=unit.getData();
		
		String settlement_state = unitData.getRow(0).get("settlement_state");
		
		String member_id = unitData.getRow(0).get("member_id");
		
		String account_type = unitData.getRow(0).get("account_type");
		
		if("1".equals(account_type))
		{
			unit.getElement("second_director").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("audit_opinion").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("director").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("main_audit_opinion").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("audit_result").setShowMode(ElementShowMode.HIDDEN);

		}
		
		
		return unit.write(ac);
	}

}
