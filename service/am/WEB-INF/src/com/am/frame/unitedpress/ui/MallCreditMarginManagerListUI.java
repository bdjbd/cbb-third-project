package com.am.frame.unitedpress.ui;

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
 */
public class MallCreditMarginManagerListUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {

		MapList unitData = unit.getData();

		for (int i = 0; i < unitData.size(); i++) {

			String dataStatus = unitData.getRow(i).get("cm_status");

			if ("1".equals(dataStatus)) {// 审核通过
				unit.getElement("scan").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			else if ("2".equals(dataStatus)){//审核拒绝
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			
		}

		return unit.write(ac);
	}
}
