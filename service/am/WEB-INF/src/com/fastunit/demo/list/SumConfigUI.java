package com.fastunit.demo.list;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.framework.config.BeanFactory;
import com.fastunit.support.UnitInterceptor;

public class SumConfigUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String sumConfig = ac.getRequestParameter("list.sum.sumconfigs", "");
		unit.getElement("sumconfigs").setDefaultValue(sumConfig);
		unit.getElement("sumconfig").setDefaultValue(sumConfig);
		unit.setSumConfig(sumConfig);
		unit.getElement("money").setFormatter(BeanFactory.getFormatter("3"));
		return unit.write(ac);
	}

}
