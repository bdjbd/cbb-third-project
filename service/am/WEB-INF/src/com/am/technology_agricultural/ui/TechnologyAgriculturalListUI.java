package com.am.technology_agricultural.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016.06.14
 *@version
 *说明：科技农技协列表ui
 */
public class TechnologyAgriculturalListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
//'notice'
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
			MapList unitData=unit.getData();
		
			for(int i=0;i<unitData.size();i++){
			
				String dataStatus=unitData.getRow(i).get("status");
				
				if("1".equals(dataStatus)){//停用-》启用
					unit.getElement("operation").setDefaultValue(i,"发布");
				}
				if("2".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"撤销");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		
		
		return unit.write(ac);
	}

}
