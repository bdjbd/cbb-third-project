package com.am.cooperation.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年7月7日
 *@version
 *说明：我要合作FromUI
 */
public class CooperateFromUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("status");
			if(dataStatus.equals("2")){
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("remarks").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
		}
		return unit.write(ac);
	}
}
