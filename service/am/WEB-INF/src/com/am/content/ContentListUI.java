package com.am.content;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月18日 下午3:46:33
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class ContentListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
//'notice'
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
			MapList unitData=unit.getData();
		
			for(int i=0;i<unitData.size();i++){
			
				String dataStatus=unitData.getRow(i).get("datastate");
				
				if("1".equals(dataStatus)){//停用-》启用
					unit.getElement("operation").setDefaultValue(i,"发布");
				}
				if("2".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"撤销");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
//					unit.getElement("moresetting").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
			}
		
		
		return unit.write(ac);
	}

}
