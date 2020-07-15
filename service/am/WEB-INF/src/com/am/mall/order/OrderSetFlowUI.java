package com.am.mall.order;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月29日
 * @version 
 * 说明:<br />
 */
public class OrderSetFlowUI implements UnitInterceptor {
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		

		MapList map=unit.getData();
		
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				if("1".equals(map.getRow(i).get("status"))){
					//status	1:停用
					unit.getElement("start_stop").setName(i, "启用");
				}else{
//					2：启用
					unit.getElement("start_stop").setName(i, "停用");
					unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		return unit.write(ac);
	}

}
