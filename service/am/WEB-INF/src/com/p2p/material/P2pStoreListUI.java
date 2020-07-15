package com.p2p.material;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class P2pStoreListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {		

		String m=ac.getRequestParameter("m");
		
		if("a".equalsIgnoreCase(m)){//新增模式
			unit.getElement("add").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("remove").setShowMode(ElementShowMode.HIDDEN);
		}
		
		return unit.write(ac);
	}

}
