package com.am.mall.commodity.selectCommodity;

import com.fastunit.context.ActionContext;
import com.fastunit.support.action.QueryAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月11日
 * @version 
 * 说明:<br />
 * 商品分类，查询商品。
 */
public class QueryCommodityAction extends QueryAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		ac=super.execute(ac);
		
		//$RS('operation','mall_commodity.list_select.operation')
		String operation=ac.getRequestParameter("operation");
		
		if(Checker.isEmpty(operation)){
			operation=ac.getSessionAttribute("mall_commodity.list_select.operation", "");
		}
		
		String url="/am_bdp/mall_commodity_select.do?m=s";
		
		if(!Checker.isEmpty(operation)){
			url+="&operation=details";
		}
		
		ac.getActionResult().setUrl(url);
		
		return ac;
	}
	
}
