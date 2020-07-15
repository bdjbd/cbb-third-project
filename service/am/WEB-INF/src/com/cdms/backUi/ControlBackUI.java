package com.cdms.backUi;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class ControlBackUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String b=ac.getRequestParameter("b");
		System.out.println(b);
		if(!"true".equals(b)){
			unit.getElement("back").setShowMode(ElementShowMode.REMOVE);
			
		}
//		else{
//			unit.getElement("excel").setShowMode(ElementShowMode.REMOVE);
//		}
		return unit.write(ac);
	}

}
