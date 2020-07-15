package com.fastunit.demo.unit;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.RandomUtil;
/* 
 * 张少飞   2017/7/26
 * fastUnit开发中心练习   MapListFactory生成数据并展示
 */
public class SampleDataMapListFactory implements MapListFactory {
	// 根据需要创建MapList  总共5行 ，每行分4个随机数据，列表展示识别符分别为'a'、'b'、'c'、'd'
	@Override
	public MapList getMapList(ActionContext ac) {
		MapList list = new MapList();
		for (int i = 0; i < 5; i++) {    //生成5行数据
			list.put(i, "a", RandomUtil.getRandomString(5));    //随机生成A列字符串  第i行、列表展示识别符为'a'、随机生成5个字符
			list.put(i, "b", RandomUtil.getRandomString(5));	//随机生成B列字符串
			list.put(i, "c", RandomUtil.getRandomString(5));	//随机生成C列字符串
			list.put(i, "d", RandomUtil.getRandomString(5));	//随机生成D列字符串
		}
		return list;
	}
}
