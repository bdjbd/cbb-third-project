package com.am.agriculturalScience.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016年4月26日
 *@version
 *说明：文章管理列表UI
 */
public class PublishingContentListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if("0".equals(dataStatus)){//草稿-》发布
				unit.getElement("operation").setDefaultValue(i,"发布");
			}
			if("2".equals(dataStatus)){//已下架/已结束-》发布
				unit.getElement("operation").setDefaultValue(i,"发布");
			}
			if("1".equals(dataStatus)){//发布-》草稿
				unit.getElement("operation").setDefaultValue(i,"取消发布");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
		}
		
		return unit.write(ac);
	}

}
