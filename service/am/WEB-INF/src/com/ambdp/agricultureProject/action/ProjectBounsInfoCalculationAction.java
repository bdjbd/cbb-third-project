package com.ambdp.agricultureProject.action;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月19日
 *@version
 *说明：项目分红管理计算每股分红Action
 */
public class ProjectBounsInfoCalculationAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 总分红金额(元)
		String totalBounsAmount = ac.getRequestParameter("mall_project_bouns_info.form.total_bouns_amount");
		//总股数
		String totalStocks = ac.getRequestParameter("mall_project_bouns_info.form.total_stocks");
		// 分红总股数
		String totalBounsStockNumber = ac.getRequestParameter("mall_project_bouns_info.form.total_bouns_stock_number");
		String stockBounsPrice = "0";
		String actualBounsPrice = "0";
		if (!Checker.isEmpty(totalBounsAmount)) {
			//计算每股分红金额(元)
			String calculationSql = "select trim(to_char(COALESCE("+totalBounsAmount+"::float/"+totalStocks+"),'99999999999990D99')) AS calculation ";
			MapList calculationMap =db.query(calculationSql);
			if(!Checker.isEmpty(calculationMap)){
				stockBounsPrice = calculationMap.getRow(0).get("calculation");
			}
			//计算实际分红金额(元)
			String actualBounsSql = "select trim(to_char(COALESCE("+stockBounsPrice+"::float*"+totalBounsStockNumber+"),'99999999999990D99')) AS actual_bouns ";
			MapList actualBounsMap =db.query(actualBounsSql);
			if(!Checker.isEmpty(actualBounsMap)){
				actualBounsPrice = actualBounsMap.getRow(0).get("actual_bouns");
			}
			Ajax ajax=new Ajax(ac);
			
			ajax.addScript("document.getElementById(\"mall_project_bouns_info.form.price\").value='"+stockBounsPrice+"'");
			ajax.addScript("document.getElementById(\"mall_project_bouns_info.form.actual_bouns\").value='"+actualBounsPrice+"'");
			ajax.send();
		}		
	}

}
