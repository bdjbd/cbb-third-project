package com.fastunit.demo.unit;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.RandomUtil;
/**
 * @author 张少飞  2017/7/26    fastUnit练习 
 * 拦截器决定最终数据，可以改动或覆盖其他方式的数据。
 */
public class SampleDataUI implements UnitInterceptor {
	// 根据需要创建MapList  总共5行 ，每行分4个随机数据，列表展示识别符分别为'a'、'b'、'c'、'd'
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList list = new MapList();
		for (int i = 0; i < 5; i++) {
			list.put(i, "a", RandomUtil.getRandomString(10)); //随机生成A列字符串  第i行、列表展示识别符为'a'、随机生成10个字符
			list.put(i, "b", RandomUtil.getRandomString(10));
			list.put(i, "c", RandomUtil.getRandomString(10));
			list.put(i, "d", RandomUtil.getRandomString(10));
		}
		unit.setData(list);
		return unit.write(ac);
	}

}
