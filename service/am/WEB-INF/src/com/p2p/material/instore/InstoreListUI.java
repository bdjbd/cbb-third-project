package com.p2p.material.instore;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;


/**
 * 入库类别UI
 * @author Administrator
 *
 */
public class InstoreListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList map=unit.getData();

		if(!Checker.isEmpty(map)){
			Element editEle=unit.getElement("edit");
			Element instoreEle=unit.getElement("instore");
			for(int i=0;i<map.size();i++){
				if("3".equals(map.getRow(i).get("instorestatus"))){//已入库
					editEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					instoreEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		return unit.write(ac);
	}

}
