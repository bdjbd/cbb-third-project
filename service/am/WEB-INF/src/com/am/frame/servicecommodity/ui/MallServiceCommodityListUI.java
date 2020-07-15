package com.am.frame.servicecommodity.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * * @author 作者：wz
 * 
 * @date 创建时间：2016-04-27
 * @version
 * @parameter
 * 服务品listui
 */
public class MallServiceCommodityListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {

		MapList unitData = unit.getData();

		for (int i = 0; i < unitData.size(); i++) {

			String dataStatus = unitData.getRow(i).get("status");

			if ("0".equals(dataStatus)) {// 待审核
				unit.getElement("operation").setName(i, "上架");
			}
			else if ("1".equals(dataStatus)) {// 审核通过
				unit.getElement("operation").setName(i, "下架");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
		}

		return unit.write(ac);
	}
}
