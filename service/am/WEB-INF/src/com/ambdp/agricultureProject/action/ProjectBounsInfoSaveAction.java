package com.ambdp.agricultureProject.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月19日
 *@version
 *说明：项目分红管理保存Action
 */
public class ProjectBounsInfoSaveAction extends DefaultAction{
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 项目分红管理
		Table table=ac.getTable("mall_project_bouns_info");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		// 每股分红金额(元)
		String moneyValue = ac.getRequestParameter("mall_project_bouns_info.form.price");
		// 实际分红金额(元)
		String actualBouns = ac.getRequestParameter("mall_project_bouns_info.form.actual_bouns");
		if (!Checker.isEmpty(moneyValue)) {
			//将金额转化为分
			String updateSql = "UPDATE mall_project_bouns_info  SET stock_bouns_price =" + moneyValue+"*100,actual_total_bouns_amount="+actualBouns+"*100  WHERE id='" + id + "' ";
			db.execute(updateSql);
		}
		ac.setSessionAttribute("mall_project_bouns_info.form.id", id);
	}
}
