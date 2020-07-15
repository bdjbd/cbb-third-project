package com.am.expert_compound.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月3日
 *@version
 *说明：科普引智大院文章发布表单UI
 */
public class ArticlesPublishedFormUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {


		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			String dataStatus=unitData.getRow(0).get("status");
			//状态是审核通过  发布按钮不显示
			if(dataStatus.equals("3")){
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
			//状态是审核驳回  驳回按钮和发布按钮不显示
			if(dataStatus.equals("4")){
				unit.getElement("backward").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("forward").setShowMode(ElementShowMode.REMOVE);
			}
		}
		return unit.write(ac);
		
	}
}
