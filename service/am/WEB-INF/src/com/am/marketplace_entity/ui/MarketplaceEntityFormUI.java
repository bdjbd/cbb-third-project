package com.am.marketplace_entity.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午3:33:11
 * @version 
 */
public class MarketplaceEntityFormUI implements UnitInterceptor {
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("status");
			if(dataStatus.equals("4")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("down").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("review_remarks").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}else if(dataStatus.equals("3")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("review_remarks").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}else if(dataStatus.equals("7")){
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("down").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("review_remarks").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
		}
		return unit.write(ac);
	}
}
