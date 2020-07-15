package com.am.cro.OrderSettlement;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/** * @author  作者：张少飞
 * @date 创建时间：2017年8月3日
 * 汽车公社 订单结算列表UI
 */
public class OrderSettlementListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
//'notice'
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
			MapList unitData=unit.getData();
		
			for(int i=0;i<unitData.size();i++){
			    //支付状态  '0'=未支付   '1'=已支付
				String paystate=unitData.getRow(i).get("paystate");
				//未支付 允许结算
				if("0".equals(paystate)){
					unit.getElement("edit").setDefaultValue(i,"结算");
				}
				//已支付 不允许再次结算
				if("1".equals(paystate)){
					unit.getElement("edit").setDefaultValue(i,"已结算");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		
		
		return unit.write(ac);
	}

}
