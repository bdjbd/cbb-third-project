package com.cdms;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class RemoteUpgradeManagementUi implements UnitInterceptor {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
	
		MapList unitData = unit.getData();
		String state=null;
		if (!Checker.isEmpty(unitData)) {
			System.err.println("Unit不为空");
			for (int i = 0; i < unitData.size(); i++) {
				state=unitData.getRow(i).get("upgrade_status");
				System.err.println(state);		
				if (state==null || !state.equals("2")) {
				//unit.setListSelectAttribute(i, "disabled");
					unit.getElement("percent").setShowMode(i,ElementShowMode.REMOVE);
				}
			}
		}
		return unit.write(ac);
	}

}
