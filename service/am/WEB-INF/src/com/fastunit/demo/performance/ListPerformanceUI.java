package com.fastunit.demo.performance;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.RandomUtil;

public class ListPerformanceUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList data = new MapList();
		int size = ac.getRequestParameterInt("performance.list.size", 1000);
		unit.getElement("size").setDefaultValue(Integer.toString(size));
		data.put(0, "a", RandomUtil.getRandomString(10));
		data.put(0, "b", RandomUtil.getRandomString(10));
		data.put(0, "c", RandomUtil.getRandomString(10));
		data.put(0, "d", RandomUtil.getRandomString(10));
		data.put(0, "e", RandomUtil.getRandomString(10));
		data.put(0, "f", RandomUtil.getRandomString(10));
		data.put(0, "g", RandomUtil.getRandomString(10));
		data.put(0, "h", RandomUtil.getRandomString(10));
		for (int i = 1; i < size; i++) {
			String str = RandomUtil.getRandomString(10);
			data.put(i, "a", str);
			data.put(i, "b", RandomUtil.getRandomString(10));
			data.put(i, "c", RandomUtil.getRandomString(10));
			data.put(i, "d", RandomUtil.getRandomString(10));
			data.put(i, "e", RandomUtil.getRandomString(10));
			data.put(i, "f", RandomUtil.getRandomString(10));
			data.put(i, "g", RandomUtil.getRandomString(10));
			data.put(i, "h", RandomUtil.getRandomString(10));
		}
		((Unit) unit.getElement("list").getObject()).setData(data);
		return unit.write(ac);
	}

}
