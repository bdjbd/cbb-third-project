package com.am.instore.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：出库管理ListUI
 */
public class OutstoreListUI implements UnitInterceptor{

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {

		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("datatstatus");
			// 判断状态
			if ("1".equals(dataStatus)) {
				
			}else{	
				// 修改确认出库状态
				unit.getElement("confirm").setName(i, "已出库");
				unit.getElement("confirm").setShowMode(i,
						ElementShowMode.CONTROL_DISABLED);
				
				unit.setListSelectAttribute(i, "disabled");
				// 修改修改按钮状态
				unit.getElement("edit").setShowMode(i,
						ElementShowMode.CONTROL_DISABLED);
			}
			
		}
		
		return unit.write(ac);
	}
}
