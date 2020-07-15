package com.wisdeem.wwd.opaccount;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class OperatorsAccountListUI implements UnitInterceptor {
	private static final long serialVersionUID = 4862102095772912047L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList lstMajorApp = unit.getData();

		for (int i = 0; i < lstMajorApp.size(); i++) {
			String strState = lstMajorApp.getRow(i).get("data_status");
			if (strState.equals("2")) {
				unit.getElement("edit").setShowMode(i, ElementShowMode.HIDDEN);
			}
		}

		return unit.write(ac);
	}

}
