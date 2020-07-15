package com.am.frame.cpdb;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年3月15日 下午2:01:27
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class ConsumerDividendListUI implements UnitInterceptor{

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if(Checker.isEmpty(dataStatus)){//草稿-》启用
				unit.getElement("operation").setDefaultValue(i,"启用");
			}
			if("1".equals(dataStatus)){//启用-》停用
				unit.getElement("operation").setDefaultValue(i,"停用");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
			if("0".equals(dataStatus)){//停用->启用
				unit.getElement("operation").setDefaultValue(i,"启用");
				
			}
		}
		
		return unit.write(ac);
	}

}
