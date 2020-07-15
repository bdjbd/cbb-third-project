package com.fastunit.demo.performance;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.RandomUtil;

public class SortListPerformanceUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList data = new MapList();
		int size = ac.getRequestParameterInt("performance.sort.size", 100);
		unit.getElement("size").setDefaultValue(Integer.toString(size));
		for (int i = 0; i < size; i++) {
			data.put(i, "a", RandomUtil.getRandomDoubleString(5, 2));
			String str = RandomUtil.getRandomString(9);
			data.put(i, "b", str);
			data.put(i, "c", str);
			data.put(i, "d", RandomUtil.getRandomInteger(9));
		}
		((Unit) unit.getElement("list").getObject()).setData(data);
		return unit.write(ac);
	}

}
