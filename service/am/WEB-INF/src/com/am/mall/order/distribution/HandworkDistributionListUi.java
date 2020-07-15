package com.am.mall.order.distribution;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author Mike
 * @create 2014年11月17日
 * @version 
 * 说明:<br />
 * 手工配单UI
 */
public class HandworkDistributionListUi implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList map=unit.getData();
		
		for(int i=0;i<map.size();i++){
			
			//派单状态为7时手工分配订单。
			if(!"7".equals(map.getRow(i).get("diststate"))){
				unit.getElement("edit").setShowMode(i,ElementShowMode.READONLY);;
			};
		}
		
		return unit.write(ac);
	}

}
