package com.am.frame.unitedpress.ui;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class ServiceCenterQueryUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
//		$RS{areaType,sc.area_type}
		
//		全国/省/市/区县 01：全国;02：省;03：市;04：区县
		
		String areaType=ac.getRequestParameter("areaType");
		
		if(Checker.isEmpty(areaType)){
			areaType=ac.getSessionAttribute("sc.area_type", "");
		}
		//proname  省
		//cityname 市
		//zonename 区
			
		if("02".equals(areaType)){//02：省;
			unit.getElement("cityname").setShowMode(ElementShowMode.REMOVE);
			unit.getElement("zonename").setShowMode(ElementShowMode.REMOVE);
		}
		
		if("03".equals(areaType)){//03：市
			unit.getElement("zonename").setShowMode(ElementShowMode.REMOVE);
		}
		
		
		return unit.write(ac);
	}


}
