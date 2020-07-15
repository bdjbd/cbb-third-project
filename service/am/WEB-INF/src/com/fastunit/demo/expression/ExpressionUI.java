package com.fastunit.demo.expression;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class ExpressionUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String expression = ac.getRequestParameter("expression.expression");
		String result = FastUnit.resolveSQL(ac, expression);

		MapList data = new MapList();
		data.put(0, "expression", expression);
		data.put(0, "result", result);

		unit.setData(data);

		return unit.write(ac);
	}

}
