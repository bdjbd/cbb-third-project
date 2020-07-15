package com.am.frame.favorites.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年4月1日
 *@version
 *说明：我的收藏列表UI
 */
public class FavoritesListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("data_state");
			
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
