package com.am.frame.unitedpress.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class IprccOfficeUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
       MapList unitData=unit.getData();
		
		String areaType=ac.getRequestParameter("areaType");
		if(Checker.isEmpty(areaType)){
			areaType=ac.getSessionAttribute("sc.iprcc.areaType", "");
		}
		
		if("04".equals(areaType)){
			//区县级别，不屏蔽字段
		}
		if("03".equals(areaType)){
			//市级别，屏蔽区县字段
			unit.getElement("zone_id").setShowMode(ElementShowMode.REMOVE);
		}
		if("02".equals(areaType)){
			//省级别，屏蔽市，区县
			unit.getElement("zone_id").setShowMode(ElementShowMode.REMOVE);
			unit.getElement("city_id").setShowMode(ElementShowMode.REMOVE);
		}
		
		
		for (int i = 0; i < unitData.size(); i++) {

			String dataStatus = unitData.getRow(i).get("f_status");

			if ("0".equals(dataStatus)) {// 待审核
				unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			else if ("1".equals(dataStatus)) {// 审核通过
				unit.getElement("examine").setDefaultValue(i, "已处理");
				unit.getElement("examine").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			else if ("2".equals(dataStatus)){//审核拒绝
				unit.getElement("examine").setDefaultValue(i, "已处理");
				unit.getElement("examine").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
				unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			else if ("3".equals(dataStatus)){//审核拒绝
				unit.getElement("examine").setDefaultValue(i, "已处理");
				unit.getElement("examine").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
				unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
			}
			
		}
		
		return unit.write(ac);
	}

}
