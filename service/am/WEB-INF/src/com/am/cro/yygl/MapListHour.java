package com.am.cro.yygl;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;

/**
 * 小时枚举
 * @author guorenjie
 */
public class MapListHour implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		MapList mapList = new MapList();
		for (int i = 0; i < 25; i++) {
			mapList.put(i, "name", i);
			mapList.put(i, "value", i);
		}
		
		return mapList;
	}

}
