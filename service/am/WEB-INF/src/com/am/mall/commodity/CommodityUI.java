package com.am.mall.commodity;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2014年11月11日
 * @version 
 * 说明:<br />
 * 商品维护，审核共享单元UI
 */
public class CommodityUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;
	
	/**商品维护菜单SESSION标记**/
	public static final String COMMODITY_UI_SESSION_MENU_CODE_TAG="COMMODITY_UI_SESSION_MENU_CODE_TAG";
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//获取树返回的菜单参数
		String operation=ac.getRequestParameter("oper");
		
		if(!Checker.isEmpty(operation)){
			ac.setSessionAttribute(COMMODITY_UI_SESSION_MENU_CODE_TAG, operation);
		}else{
			operation=(String)ac.getSessionAttribute(COMMODITY_UI_SESSION_MENU_CODE_TAG);
		}
		
		if("manager".equals(operation)){//维护
			unit.setTitle("商品维护");
		}
		if("review".equals(operation)){//审核
			unit.setTitle("商品审核");
		}
		
		return unit.write(ac);
	}

}
