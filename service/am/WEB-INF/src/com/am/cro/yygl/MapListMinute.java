package com.am.cro.yygl;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;

/**
 * 分钟枚举
 * @author guorenjie
 */
public class MapListMinute implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		MapList mapList = new MapList();
		for (int i = 0; i < 61; i++) {
			mapList.put(i, "name", i);
			mapList.put(i, "value", i);
		}
		
		return mapList;
	}

}
