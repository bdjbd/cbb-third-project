package com.cdms.peopleCar;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class PeopleCarRelieveUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData = unit.getData();
		if (!Checker.isEmpty(unitData)) {
			for (int i = 0; i < unitData.size(); i++) {
				// 获取当前行的name,当前绑定人
				// 如果没有当前绑定人，则无法解除绑定
				String name = unitData.getRow(i).get("name");

				if ("".equals(name) || name == null) {
					unit.getElement("relieve").setShowMode(i, ElementShowMode.CONTROL_DISABLED);

				}
			}
		}
		return unit.write(ac);
	}

}
