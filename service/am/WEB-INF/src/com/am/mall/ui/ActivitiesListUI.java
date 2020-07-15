package com.am.mall.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016.06.06
 *@version
 *说明：分类商城列表UI
 */
public class ActivitiesListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
			MapList unitData=unit.getData();
		
			for(int i=0;i<unitData.size();i++){
			
				String dataStatus=unitData.getRow(i).get("activitiesstate");
				
				if("1".equals(dataStatus)){//启用-》停用
//					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		
		
		return unit.write(ac);
	}

}
