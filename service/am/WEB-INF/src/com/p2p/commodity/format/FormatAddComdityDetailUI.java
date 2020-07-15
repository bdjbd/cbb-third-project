package com.p2p.commodity.format;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 添加商品规格UI
 * @author Administrator
 *
 */
public class FormatAddComdityDetailUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList map=unit.getData();
		
		if(!Checker.isEmpty(map)){
			Element ele=unit.getElement("commoditydetail");
			for(int i=0;i<map.size();i++){
				ele.setShowMode(i,ElementShowMode.CONTROL);
			}
		}
		return unit.write(ac);
	}

}
