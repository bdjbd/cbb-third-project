package com.am.food_safety.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016.06.14
 *@version
 *说明：冷柜申请表单UI
 */
public class FreezerApplyFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("status");
			//审核通过
			if(dataStatus.equals("2")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
			//审核拒绝
			if(dataStatus.equals("3")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
