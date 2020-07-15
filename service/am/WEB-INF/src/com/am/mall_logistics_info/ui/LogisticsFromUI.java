package com.am.mall_logistics_info.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月16日
 * @version 
 * 说明:<br />
 */
public class LogisticsFromUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList dataMap=unit.getData();
		
		if(!Checker.isEmpty(dataMap)&&"3".equals(dataMap.getRow(0).get("status"))){
			unit.getElement("forward").setShowMode(ElementShowMode.CONTROL);
		}else{
			unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
		}
		
		return unit.write(ac);
	}

}
