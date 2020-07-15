package com.ambdp.mallstore.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author 作者：yangdong
 *@create 时间：2016年6月19日 下午5:32:23
 *@version 说明：
 */
public class MoreSettingFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
			
			//获取商城分类
			String mallclass_id=unitData.getRow(0).get("mallclass_id");
			
			//景区 我要玩
			if("1".equals(mallclass_id)){
				unit.getElement("wyz_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyc_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zccx_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zczws_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zcjg_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("facility_details").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("hotel_voucher").setShowMode(ElementShowMode.REMOVE);
			}
			//酒店 我要住
			else if("2".equals(mallclass_id)){//撤销发布
				unit.getElement("wyw_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyc_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zccx_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zczws_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zcjg_id").setShowMode(ElementShowMode.REMOVE);
			}
			
			//餐饮 我要吃
			else if("3".equals(mallclass_id)){//撤销发布
				unit.getElement("wyw_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyz_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zccx_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zczws_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zcjg_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("facility_details").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("hotel_voucher").setShowMode(ElementShowMode.REMOVE);
			}
			
			//租车
			else if("9".equals(mallclass_id)){//撤销发布
				unit.getElement("wyw_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyz_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyc_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("facility_details").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("hotel_voucher").setShowMode(ElementShowMode.REMOVE);
			}//其他
			else{
				unit.getElement("wyw_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyz_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("wyc_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zccx_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zczws_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("zcjg_id").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("facility_details").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("save").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("hotel_voucher").setShowMode(ElementShowMode.REMOVE);
			}
		
		return unit.write(ac);
	}
}
