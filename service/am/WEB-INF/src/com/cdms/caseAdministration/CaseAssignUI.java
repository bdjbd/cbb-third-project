package com.cdms.caseAdministration;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

//案件编辑---分派按钮
//1=新案件、2=派单中、3=已接单、4=无人接单、5=查勘中、6=已结束
public class CaseAssignUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData = unit.getData();
		if (!Checker.isEmpty(unitData)) {
			for (int i = 0; i < unitData.size(); i++) {

				// 获取当前行的case_state,案件状态
				// 案件状态为2、3、5、6时，不可编辑，不可分派
				String case_state = unitData.getRow(i).get("case_state");
				if (case_state.equals("2") || case_state.equals("3") || case_state.equals("5")
						|| case_state.equals("6")) {
					unit.getElement("assign").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
					unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
				}

			}
		}
		return unit.write(ac);
	}
}
