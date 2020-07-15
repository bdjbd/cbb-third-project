package com.p2p.material.outstore;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class OutstoreListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList map=unit.getData();

		if(!Checker.isEmpty(map)){
			Element editEle=unit.getElement("edit");
			Element outstoreEle=unit.getElement("outstore");
			for(int i=0;i<map.size();i++){
				String datastatus=map.getRow(i).get("datatstatus");
				if("3".equals(datastatus)){//出库
					editEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					outstoreEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		return unit.write(ac);
	}

}
