package com.am.frame.memberlogout.ui;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2016年6月14日
 * @version 
 * 说明:<br />
 * 社员注销审核UI
 */
public class MemberLogoutApplyUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String status=unit.getData().getRow(0).get("status");
		
		if(!"1".equals(status)){
			unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
			unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			unit.getElement("remarks").setShowMode(ElementShowMode.READONLY);
			
		}
		
		return unit.write(ac);
	}

}
