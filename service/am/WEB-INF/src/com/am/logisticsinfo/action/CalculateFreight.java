package com.am.logisticsinfo.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月4日 下午6:02:30
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class CalculateFreight extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 
		
		double distance = Double.parseDouble(ac.getRequestParameter("mall_logistics_info.form.total_distance"));
		
		ac.getActionResult().setScript("document.getElementsByName('mall_logistics_info.form.freight')[0].value = "+distance+"");
		
		
		super.doAction(db, ac);
	}

}
