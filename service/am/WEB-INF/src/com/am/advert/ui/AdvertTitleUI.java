package com.am.advert.ui;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月2日
 * @version 
 * 说明:<br />
 * 广告标题这是
 */
public class AdvertTitleUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String menuCode=ac.getRequestParameter("menucode");
		
		if(!Checker.isEmpty(menuCode)){
			ac.setSessionAttribute("menucode",menuCode);
		}
		if(Checker.isEmpty(menuCode)){
			menuCode=ac.getRequestParameter("am_mobliemenuid");
		}
		
		
		
		return unit.write(ac);
	}

}
