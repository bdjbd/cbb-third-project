package com.fastunit.demo.layout;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.view.config.Layout;
import com.fastunit.view.unit.UnitComponent;

public class LayoutModeUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		int layoutMode = ac.getRequestParameterInt("layoutmode.layoutmode",
				Layout.GROUP_VERTICAL);
		unit.getElement("layoutmode").setDefaultValue(Integer.toString(layoutMode));
		UnitComponent formUnit = (UnitComponent) unit.getElement("form")
				.getObject();
		if (layoutMode == Layout.FIELD_SET_VERTICAL
				|| layoutMode == Layout.CARD_TOP_NAVIGATOR
				|| layoutMode == Layout.CARD_LEFT_NAVIGATOR
				|| layoutMode == Layout.CARD_NO_NAVIGATOR) {
			formUnit.setShowGridBorder(false);
		}
		formUnit.setLayoutMode(layoutMode);
		return unit.write(ac);
	}

}
