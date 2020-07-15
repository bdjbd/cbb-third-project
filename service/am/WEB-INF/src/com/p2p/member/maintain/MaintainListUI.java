package com.p2p.member.maintain;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class MaintainListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList map=unit.getData();
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				//p2p_maintain.list.
				String type=map.getRow(i).get("type");
				if("2".equals(type)){
					unit.getElement("edit").setShowMode(i, ElementShowMode.REMOVE);
				}
			}
		}
		return unit.write(ac);
	}

}
