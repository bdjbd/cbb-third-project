package com.am.expert_compound.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年5月3日
 *@version
 *说明：科普引智大院文章发布列表UI
 */
public class ArticlesPublishedListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {

		MapList unitData = unit.getData();

		for (int i = 0; i < unitData.size(); i++) {

			String dataStatus = unitData.getRow(i).get("status");

			if ("2".equals(dataStatus)){//审核拒绝
				unit.getElement("scan").setDefaultValue(i, "审核");
			}
			
		}

		return unit.write(ac);
	}
}
