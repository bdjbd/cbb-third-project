package com.mall.commodity.ui;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2016年6月19日
 * @version 
 * 说明:<br />
 * 呼旅 商品表单UI
 */
public class CommodityFromUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
//		unit.getElement("maxsalenumber").setShowMode(ElementShowMode.REMOVE);
		
		return unit.write(ac);
	}

}
