package com.am.frame.elect;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class ElectTicketListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("datastatus");
			
			if("1".equals(dataStatus)){//草稿-》启用
				unit.getElement("operation").setDefaultValue(i,"启用");
			}
			if("2".equals(dataStatus)){//启用-》停用
				unit.getElement("operation").setDefaultValue(i,"停用");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			if("3".equals(dataStatus)){//停用->启用
				unit.getElement("operation").setDefaultValue(i,"启用");
				
			}
		}
		
		return unit.write(ac);
	}

}
