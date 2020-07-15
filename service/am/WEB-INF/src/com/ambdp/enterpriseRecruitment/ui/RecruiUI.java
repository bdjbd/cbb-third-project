package com.ambdp.enterpriseRecruitment.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;


/** 
 * @author  作者：qintao
 * @date 创建时间：2016年11月25日
 * @explain 说明 : 企业招聘UI
 */

public class RecruiUI implements UnitInterceptor{

	private static final long serialVersionUID = 1L;
	
	
		@Override
		public String intercept(ActionContext ac, Unit unit) throws Exception {
			
				MapList unitData=unit.getData();
			
				for(int i=0;i<unitData.size();i++){
				
					String dataStatus=unitData.getRow(i).get("data_status");
					
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
