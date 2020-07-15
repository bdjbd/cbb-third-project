package com.am.frame.unitedpress.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月30日
 *@version
 *说明：合作社表单UI
 */
public class CooperativeFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("f_status");
			if(dataStatus.equals("0")){
				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
			}
			if(dataStatus.equals("1")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
