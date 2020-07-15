package com.am.frame.systemAccount.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author 作者：xiechao
 *@create 时间：2016年12月19日19:09:17
 *@version 说明：
 */
public class WithdrawalsAuditFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		
		if(!Checker.isEmpty(unitData)){
			String audit_state = unitData.getRow(0).get("audit_state");
			String audit_result = unitData.getRow(0).get("audit_result");
			
			if(!"0".equals(audit_state)){
				unit.getElement("save").setShowMode(ElementShowMode.REMOVE);
			}
			
			if(Checker.isEmpty(audit_result)){
				unit.getElement("second_director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("audit_opinion").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("main_audit_opinion").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("audit_result").setShowMode(ElementShowMode.REMOVE);
			}
			
			if(Checker.isEmpty(audit_result)||"1".equals(audit_result)||"3".equals(audit_result)){
				unit.getElement("director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("main_audit_opinion").setShowMode(ElementShowMode.REMOVE);
			}
		}
		
		return unit.write(ac);
	}
}