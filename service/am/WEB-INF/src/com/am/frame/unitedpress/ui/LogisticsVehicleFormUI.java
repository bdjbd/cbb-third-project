package com.am.frame.unitedpress.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月4日
 *@version
 *说明：物流车辆表单UI
 */
public class LogisticsVehicleFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("f_status");
			//待审核
			if(dataStatus.equals("0")){
				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
			}
			//审核通过
			if(dataStatus.equals("1")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
			//审核拒绝
			if(dataStatus.equals("2")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
			}
			//撤销
			if(dataStatus.equals("3")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
