package com.fastunit.demo.performance;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class TreePerformanceUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		int level = ac.getRequestParameterInt("performance.tree.level",
				PerformanceTreeFactory.LEVEL);
		int size = ac.getRequestParameterInt("performance.tree.size",
				PerformanceTreeFactory.SIZE);
		unit.getElement("level").setDefaultValue(Integer.toString(level));
		unit.getElement("size").setDefaultValue(Integer.toString(size));
		return unit.write(ac);
	}

}
