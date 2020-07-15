package com.am.mall.order.distnbution;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月29日
 * @version 
 * 说明:<br />
 * 接单列表UI，在部门接到后将接单和拒绝接单按钮置灰色。
 */
public class DistnbutListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList listMap=unit.getData();
		
		//接受
		Element receiveorderEle=unit.getElement("receiveorder");
		//拒绝接受
		Element refuseorderEle=unit.getElement("refuseorder");
		
		
		if(!Checker.isEmpty(listMap)){
			String acceptorderid="";
			String orderState="9";
			
			for(int i=0;i<listMap.size();i++){
				//接单ID
				acceptorderid=listMap.getRow(i).get("acceptorderid");
				//流程状态
				orderState=listMap.getRow(i).get("orderstate");
				
				if(!Checker.isEmpty(acceptorderid)||"9".equals(orderState)||"10".equals(orderState)){
					receiveorderEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					refuseorderEle.setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		
		return unit.write(ac);
	}

}
