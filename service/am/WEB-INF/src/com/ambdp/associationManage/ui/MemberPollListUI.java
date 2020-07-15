package com.ambdp.associationManage.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author wangxi
 *@create 2016.06.04
 *@version
 *说明：社员代表大会列表UI
 */
public class MemberPollListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		for(int i=0;i<unitData.size();i++){
			
			String dataStatus=unitData.getRow(i).get("status");
			
			if("0".equals(dataStatus)){//停用-》启用
				unit.getElement("operation").setDefaultValue(i,"发布");
			}
			if("1".equals(dataStatus)){//启用-》停用
				unit.getElement("operation").setDefaultValue(i,"撤销");
				unit.setListSelectAttribute(i, "disabled");
				unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
			}
		}
		
		return unit.write(ac);
	}

}
