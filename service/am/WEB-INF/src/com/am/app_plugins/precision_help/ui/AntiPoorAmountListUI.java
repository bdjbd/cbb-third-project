package com.am.app_plugins.precision_help.ui;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;


/**
 * 扶贫资金分配 list UI
 * @author yuebin
 *1，控制提交按钮
 *
 */
public class AntiPoorAmountListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList data=unit.getData();
		
		if(!Checker.isEmpty(data)){
			for(int i=0;i<data.size();i++){
				Row row=data.getRow(i);
				
				//申请状态
//				0=草稿
//				1=申请提现
//				2=提现审核中
//				3=提现申请通过
//				4=已支付
//				5=拒绝提现，拒绝提现是要输入拒绝原因。
				String settlementState=row.get("settlement_state");
				if(!"0".equals(settlementState)){
					unit.getElement("submit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
					unit.getElement("edit").setShowMode(i, ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		return unit.write(ac);
	}

}
