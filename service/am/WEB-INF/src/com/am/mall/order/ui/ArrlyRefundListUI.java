package com.am.mall.order.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />
 */
public class ArrlyRefundListUI implements UnitInterceptor {


	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList data=unit.getData();
		
		if(!Checker.isEmpty(data)){
			for(int i=0;i<data.size();i++){
				if(!"91".equals(data.getRow(i).get("orderstate"))){
					//如果订单状态不等于退货申请，则处理状态不可操作
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		return unit.write(ac);
	}

}
