package com.wisdeem.wwd.management.order;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class OrderListUI implements UnitInterceptor {
	private static final long serialVersionUID = 4862102095772912047L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
	MapList lstMajorApp=unit.getData();
		
		for (int i = 0; i < lstMajorApp.size(); i++) {
			String strState=lstMajorApp.getRow(i).get("data_status");
			//已交易的不能修改删除
			if(strState.equals("3")){
				unit.getElement("edit").setShowMode(i, ElementShowMode.HIDDEN);
			}
		}
		
		return unit.write(ac);
	}

}
