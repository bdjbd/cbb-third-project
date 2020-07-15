package com.cdms.generalStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class TotalReportUI implements UnitInterceptor{

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		//是否有调度（编辑案件）权限
		boolean isDiaoDu = ac.getVisitor().getUser().hasUnitPrivilege("cdms", "cdms_case_edit.form", "e");
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.info("是否有调度权限="+isDiaoDu);
		if (isDiaoDu==false) {
			unit.removeElement("task_oil_sum");
			unit.removeElement("notask_oil_sum");
		}
		return unit.write(ac);
	}

}
