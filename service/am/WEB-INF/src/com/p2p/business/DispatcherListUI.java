package com.p2p.business;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class DispatcherListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList map=unit.getData();
		
		if(!Checker.isEmpty(map)){
			Element ele=unit.getElement("e1");
			for(int i=0;i<map.size();i++){
				if(!"2".equals(map.getRow(i).get("odstatus"))){
					ele.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		return unit.write(ac);
	}

}
