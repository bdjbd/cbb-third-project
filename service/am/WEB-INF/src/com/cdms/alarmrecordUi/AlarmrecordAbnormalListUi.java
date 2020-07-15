package com.cdms.alarmrecordUi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class AlarmrecordAbnormalListUi implements UnitInterceptor{
	private Logger logger = LoggerFactory.getLogger(getClass());
		private static final long serialVersionUID = 1L;

		@Override
		public String intercept(ActionContext ac, Unit unit) throws Exception {
			MapList unitData = unit.getData();
			if (!Checker.isEmpty(unitData)){
			for (int i = 0; i < unitData.size(); i++) {

				// 获取当前行的operation_status
				String operation_status = unitData.getRow(i).get("operation_status");
				logger.info("++++++++++++++++++++++++++++" + operation_status);
			if(operation_status.equals("1")) {
				unit.getElement("remove").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				
			}	
				
				
				
				
			}	
			}
			return unit.write(ac);
		}


}
