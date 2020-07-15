package com.am.mall.commodity;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2016年6月20日
 * @version 
 * 说明:<br />
 * 商品更多设置界面UI，如果商品状态为上架，则用只读模式进入界面
 * mall_Commodity.CommodityState 
 * 0=下架，可以编辑、删除，不在商品列表显示；
 * 1=上架，不可编辑，可在商品列表显示；
 */
public class CommodityMoreSettingFormUI implements UnitInterceptor {


	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//e4  /am_bdp/mall_commodity_feature.form.do?m=$R{m}&
		//mall_commodity.form.id=$R{mall_commodity.form.id}
		//&commodity_feature.mall_class=$R{commodity_feature.mall_class}
		//&autoback=/am_bdp/mall_commodity.do?m=s
		unit.getElement("");
		
		return unit.write(ac);
	}

}
