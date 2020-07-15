package com.am.marketplace_entity.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午3:30:30
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class MarketplaceEntityListUI implements UnitInterceptor {
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			for(int i=0;i<unitData.size();i++){
				String dataStatus=unitData.getRow(i).get("status");
				
				if(dataStatus.equals("4") || dataStatus.equals("7")){
					unit.setListSelectAttribute(i, "disabled");
				}
			}
		}
		return unit.write(ac);
	}
}
