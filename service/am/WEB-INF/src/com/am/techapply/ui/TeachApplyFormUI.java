package com.am.techapply.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月3日 下午7:16:21
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class TeachApplyFormUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String state = unitData.getRow(0).get("is_auth");
			if(!"0".equals(state)){
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("remark").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
		}
		return unit.write(ac);
	}
}
