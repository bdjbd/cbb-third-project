package com.ambdp.associationManage.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年6月15日
 *@version
 *说明：单位帮扶还款列表UI
 */
public class OrgHelpInfoListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		for(int i=0;i<unitData.size();i++){
			
			String repaymentStatus=unitData.getRow(i).get("repayment_status");

			if("1".equals(repaymentStatus)){//无需还款
				unit.getElement("repayment").setDefaultValue(i,"无需还款");
				unit.getElement("repayment").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			if("2".equals(repaymentStatus)){//未还款
				unit.getElement("repayment").setDefaultValue(i,"还款");
			}
			if("3".equals(repaymentStatus)){//已还款
				unit.getElement("repayment").setDefaultValue(i,"已还款");
				unit.getElement("repayment").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			}
		
		
		return unit.write(ac);
	}

}
