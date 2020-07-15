package com.am.mall.commodity;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2014年12月3日
 * @version 
 * 说明:<br />
 */
public class MallCommodityGroupSaleList implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		return unit.write(ac);
	}

}
