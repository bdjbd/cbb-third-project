package com.am.logisticsinfo.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月4日 下午3:13:21
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class LogisticsInfoListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if("1".equals(dataStatus)){//草稿状态
				unit.getElement("operation").setDefaultValue(i,"发布");
			}
			if(!"1".equals(dataStatus)){//发布状态
				unit.getElement("operation").setDefaultValue(i,"撤销发布");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.setListSelectAttribute(i, "disabled");
			}
			if("3".equals(dataStatus)||"4".equals(dataStatus)){//配送中状态
				unit.getElement("operation").setDefaultValue(i,"撤销发布");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				unit.setListSelectAttribute(i, "disabled");
			}
		}
		
		return unit.write(ac);
	}

}
