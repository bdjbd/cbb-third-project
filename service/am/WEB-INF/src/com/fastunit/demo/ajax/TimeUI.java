package com.fastunit.demo.ajax;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.DateUtil;

public class TimeUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		unit.setTitle(unit.getTitle() + " " + DateUtil.getCurrentDatetime());
		return unit.write(ac);
	}

}
