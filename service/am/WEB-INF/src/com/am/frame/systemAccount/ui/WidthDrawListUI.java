package com.am.frame.systemAccount.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wz
 *@create 2016年4月19日
 *@version
 *说明：提现申请UI
 */
public class WidthDrawListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		String types= ac.getRequestParameter("am_bdp.withdrawals.type");
		
		for(int i=0;i<unitData.size();i++){
			
			//
			String dataStatus=unitData.getRow(i).get("settlement_state");
			
			//
			String audit_state = unitData.getRow(i).get("audit_state");
			
			String id = unitData.getRow(i).get("id");
			
			String account_type = unitData.getRow(i).get("account_type");
			
			
			if("1".equals(account_type)){
				unit.getElement("operation").setLink(i,
						"/am_bdp/withdrawals.form.do?m=e&withdrawals.form.id="+id
						+"&autoback=/am_bdp/withdrawals.do");
			}
			
			
			
			if(!"3".equals(dataStatus)&&("0".equals(dataStatus) || "0".equals(audit_state))){
				unit.getElement("operation").setDefaultValue(i,"操作");
				unit.setListSelectAttribute(i, "disabled");
				if("1".equals(types))
				{
					unit.getElement("operation").setLink(i,
							"/am_bdp/account_withdrawals.apply.do?m=e&withdrawals.form.id="+id+"&autoback=/am_bdp/withdrawals.do?m=s&am_bdp.withdrawals.type=1&clear=am_bdp.withdrawals.query");
				}
			}
			if(!"0".equals(audit_state)){
				unit.getElement("operation").setDefaultValue(i,"审核中");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			if("1".equals(dataStatus)){
				unit.getElement("operation").setDefaultValue(i,"审核中");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL);
				unit.getElement("operation").setLink(i,
						"/am_bdp/withdrawals.form.do?m=e&withdrawals.form.id="+id
						+"&autoback=/am_bdp/withdrawals.do");
				
			}
			if("2".equals(dataStatus)){
				unit.getElement("operation").setDefaultValue(i,"审核中");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.setListSelectAttribute(i, "disabled");
			}
			if("3".equals(dataStatus)){//发布-》草稿
				unit.getElement("operation").setDefaultValue(i,"审核通过");
				unit.setListSelectAttribute(i, "");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			if("4".equals(dataStatus)){
				
				unit.getElement("operation").setDefaultValue(i,"审核拒绝");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.setListSelectAttribute(i, "disabled");
			}
			if("5".equals(dataStatus)){
			
				unit.getElement("operation").setDefaultValue(i,"拒绝提现");
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.setListSelectAttribute(i, "disabled");
			}
		
		}
		
		return unit.write(ac);
	}

}
