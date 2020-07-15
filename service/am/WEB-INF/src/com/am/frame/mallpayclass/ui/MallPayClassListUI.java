package com.am.frame.mallpayclass.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午2:23:04
 * @version 支付种类列表UI
 */
public class MallPayClassListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		if(!Checker.isEmpty(unitData)){
			for(int i=0;i<unitData.size();i++){
				
				String dataStatus=unitData.getRow(i).get("status");
				
				if("0".equals(dataStatus)){//停用-》启用
					unit.getElement("operation").setDefaultValue(i,"启用");
				}
				if("1".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"停用");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		}
		
		
		return unit.write(ac);
	}

}
