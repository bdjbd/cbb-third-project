package com.p2p.base.badge;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * Author: Mike
 * 2014年7月18日
 * 说明：
 *  徽章列表UI，启用的无法修改。
 **/
public class BadgeListUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList data = unit.getData();
		// 没有价格的设为禁用
		for (int i = 0; i < data.size(); i++) {
			String state = data.getRow(i).get("badgestate");
			if ("1".equals(state)) {
//				unit.setListSelectAttribute(i, "disabled");
//				unit.getElement("edit").setHtml("<a href='#' onclick='function alert('请先停用，然后在修改内容。')'>修改</a>");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
		}
		return unit.write(ac);
	}

}
