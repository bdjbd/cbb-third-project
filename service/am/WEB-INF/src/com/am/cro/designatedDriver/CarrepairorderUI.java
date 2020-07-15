package com.am.cro.designatedDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * 代驾费用管理UI:代驾费结算状态为已结算，审核按钮失效
 * @author 王成阳
 * 2017-9-7
 */
public class CarrepairorderUI implements UnitInterceptor{
	//打印日志
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		logger.debug("------------------------------------------进入代驾结算UI----------------------------------------------------");
		//获得页面数据
		MapList unitData = unit.getData();
		//循环页面数据
		for (int i = 0; i < unitData.size(); i++) {
			//获得代驾结算状态
			String driving_settlement_state = unitData.getRow(i).get("driving_settlement_state");
			//获得代驾审核状态
			String driving_settlement_audit_state = unitData.getRow(i).get("driving_settlement_audit_state");
			//如果代驾结算状态为2(已结算)审核按钮失效、结算按钮失效
			if ("1".equals(driving_settlement_state)) {
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				logger.debug("-----------------------------------审核按钮已失效----------------------------------------------------");
				unit.setListSelectAttribute(i, "disabled");
				logger.debug("-----------------------------------结算按钮已失效----------------------------------------------------");
			}
			//如果审核状态为1(待审核)则结算按钮失效	代驾审核状态:driving_settlement_audit_state					结算按钮:forward
			if ("1".equals(driving_settlement_audit_state)) {
				unit.setListSelectAttribute(i, "disabled");
				logger.debug("-----------------------------------结算按钮已失效----------------------------------------------------");
			}
			//如果审核状态为2(已审核)则审核按钮失效 
			if ("2".equals(driving_settlement_audit_state)) {
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				logger.debug("-----------------------------------审核按钮已失效----------------------------------------------------");
			}
		}
		return unit.write(ac);
	}
}
