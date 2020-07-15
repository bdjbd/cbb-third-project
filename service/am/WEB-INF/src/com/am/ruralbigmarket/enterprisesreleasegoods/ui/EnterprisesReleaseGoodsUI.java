package com.am.ruralbigmarket.enterprisesreleasegoods.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
/** 
 * @author  作者：qintao
 * @date 创建时间：2016年11月26日
 * @explain 说明 : 企业发布商品
 */

public class EnterprisesReleaseGoodsUI implements UnitInterceptor{
	
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
			MapList unitData=unit.getData();
		
			for(int i=0;i<unitData.size();i++){
			
				String dataStatus=unitData.getRow(i).get("status");
				
				if("1".equals(dataStatus)){//停用-》启用
					unit.getElement("operation").setDefaultValue(i,"发布");
				}
				if("2".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"申请上架");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				if("3".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"招商中");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				if("4".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"强行下架");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				if("5".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"已支付到平台");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				if("6".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"已收货");
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
					unit.getElement("operation").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				if("7".equals(dataStatus)){//启用-》停用
					unit.getElement("operation").setDefaultValue(i,"发布");
				}
			}
		
		
		return unit.write(ac);
	}


}
