package com.am.mall.commodity;

import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2014年11月11日
 * @version 
 * 说明:<br />
 * 
 * 商品维护ListUI
 */
public class CommodityListUpOrDownShelvesUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String operation=(String)ac.getSessionAttribute(CommodityUI.COMMODITY_UI_SESSION_MENU_CODE_TAG);
		
		
		//new,delete,down,up
		if("manager".equals(operation)){//维护
			unit.getElement("down").setShowMode(ElementShowMode.HIDDEN);
			unit.getElement("up").setShowMode(ElementShowMode.HIDDEN);
		}
		
		
		return unit.write(ac);
	}

}
