package com.am.mall.commodity;

import com.fastunit.Element;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2014年12月3日
 * @version 
 * 说明:<br />
 * 商品推挤UI
 */
public class MallCommodityRecommendList implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String state=ac.getRequestParameter("commoditystate");
		
		Element addBtn=unit.getElement("add");
		Element revBtn=unit.getElement("remove");
		Element saveBtn=unit.getElement("save");
		
		if("1".equals(state)){
			if(addBtn!=null){
				addBtn.setShowMode(ElementShowMode.REMOVE);
			}
			
			if(revBtn!=null){
				revBtn.setShowMode(ElementShowMode.REMOVE);
			}
			if(saveBtn!=null){
				saveBtn.setShowMode(ElementShowMode.REMOVE);
			}
		}
		
		return unit.write(ac);
	}

}
