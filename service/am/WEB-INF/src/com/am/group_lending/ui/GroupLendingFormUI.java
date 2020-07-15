package com.am.group_lending.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月10日
 *@version
 *说明：联保贷款表单UI
 */
public class GroupLendingFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("lp_stauts");
			//审核通过
			if(dataStatus.equals("4")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
			//审核拒绝
			if(dataStatus.equals("5")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
