package com.am.frame.unitedpress.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * * @author 作者：wz
 * 
 * @date 创建时间：2016-04-27
 * @version
 * @parameter
 */
public class UnitedPressFormUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
//		if(!Checker.isEmpty(unitData)){
//			String dataStatus=unitData.getRow(0).get("f_status");
//			if(dataStatus.equals("2")){
//				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
//				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
//				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
//			}
//		}
		
		if(!Checker.isEmpty(unitData)){
			
			String area_type = unitData.getRow(0).get("area_type");
			
			//省
			if("02".equals(area_type))
			{
				unit.getElement("city_id").setShowMode(ElementShowMode.HIDDEN);
				unit.getElement("zone_id").setShowMode(ElementShowMode.HIDDEN);
			}else if("03".equals(area_type)) //市
			{
				unit.getElement("zone_id").setShowMode(ElementShowMode.HIDDEN);
			}
			
			
			String dataStatus=unitData.getRow(0).get("f_status");
			if(dataStatus.equals("0")){
				unit.getElement("kill").setShowMode(ElementShowMode.REMOVE);
			}
			if(dataStatus.equals("1")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
