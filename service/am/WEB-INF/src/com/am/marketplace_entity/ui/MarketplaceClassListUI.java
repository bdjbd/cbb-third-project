package com.am.marketplace_entity.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年5月5日
 *@version
 *说明：农村大市场分类列表UI
 */
public class MarketplaceClassListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if("0".equals(dataStatus)){//停用-》启用
				unit.getElement("operation").setDefaultValue(i,"启用");
			}
			if("1".equals(dataStatus)){//启用-》停用
				unit.getElement("operation").setDefaultValue(i,"停用");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
		}
		
		return unit.write(ac);
	}

}
