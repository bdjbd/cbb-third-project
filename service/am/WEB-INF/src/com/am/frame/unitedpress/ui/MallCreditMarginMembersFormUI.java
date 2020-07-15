package com.am.frame.unitedpress.ui;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * * @author 作者：wz
 * 
 * @date 创建时间：2016-04-27
 * @version
 * @parameter
 */
public class MallCreditMarginMembersFormUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		String id = ac.getRequestParameter("mall_credit_margin_manager.form.id");
		
		String m =ac.getRequestParameter("m");
		
		UUID uuid = null;
		
		if("a".equals(m) && Checker.isEmpty(id)){
			uuid = UUID.randomUUID();
			unit.getElement("id").setDefaultValue(uuid.toString());
		}
	
		return unit.write(ac);
		
	}
}
