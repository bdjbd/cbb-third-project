package com.ambdp.resourcemanagement.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月5日 下午5:02:29
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class ResourceManagementListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if("0".equals(dataStatus)){//发布
				unit.getElement("operation").setDefaultValue(i,"发布");
			}
			if("1".equals(dataStatus)){//撤销发布
				unit.getElement("operation").setDefaultValue(i,"撤销发布");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);	
				unit.setListSelectAttribute(i, "disabled");
			}
		}
		
		return unit.write(ac);
	}
}
