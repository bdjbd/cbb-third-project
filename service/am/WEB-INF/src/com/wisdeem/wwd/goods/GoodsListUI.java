package com.wisdeem.wwd.goods;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class GoodsListUI implements UnitInterceptor {
	private static final long serialVersionUID = 4862102095772912047L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
	MapList lstMajorApp=unit.getData();
		
		for (int i = 0; i < lstMajorApp.size(); i++) {
			//上架状态
			String strState=lstMajorApp.getRow(i).get("data_status");
			//已上架：不能修改删除
			if(strState.equals("2")){
				unit.getElement("edit").setShowMode(i, ElementShowMode.REMOVE);
			}
		}

		return unit.write(ac);
	}
}


