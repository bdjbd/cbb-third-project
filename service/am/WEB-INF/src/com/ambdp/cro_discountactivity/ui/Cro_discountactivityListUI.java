package com.ambdp.cro_discountactivity.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author 张少飞
 *@create 2017/7/11
 *@version
 *说明：汽车公社 汽修厂优惠列表UI   列表各项在启用后不能编辑，停用后方可编辑
 */
public class Cro_discountactivityListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			//启用状态  '0'为未启用 (不作任何操作)  '1'为已启用(此时编辑按钮应当被屏蔽)
			String activitystate = unitData.getRow(i).get("activitystate");

			if("0".equals(activitystate)){//未启用
				unit.getElement("operation").setDefaultValue(i,"启用");
			}
			if("1".equals(activitystate)){//已启用 
				unit.getElement("operation").setDefaultValue(i,"停用");
				//设定当前行不能被选中 (这一条有争议 视情况而定吧)
				unit.setListSelectAttribute(i, "disabled");  
				//设定当前行不能修改
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
		}
		
		return unit.write(ac);
	}

}
