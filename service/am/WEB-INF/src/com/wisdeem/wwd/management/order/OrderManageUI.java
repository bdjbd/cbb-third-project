package com.wisdeem.wwd.management.order;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
/**
 * 订单管理List UI
 */
public class OrderManageUI implements UnitInterceptor {

	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList map=unit.getData();
		for(int i=0;i<map.size();i++){
			String dataStatus=map.getRow(i).get("data_status");
			if("6".equals(dataStatus)||"7".equals(dataStatus)){
				unit.getElement("data_status").setShowMode(i,ElementShowMode.READONLY);
			}
		}
		return unit.write(ac);
	}

}
