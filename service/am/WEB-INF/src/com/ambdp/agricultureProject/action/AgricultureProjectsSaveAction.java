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
 *说明：农业项目管理保存Action
 */
public class AgricultureProjectsSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 农业项目
		Table table=ac.getTable("mall_agriculture_projects");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		// 每股金额
		String moneyValue = ac.getRequestParameter("mall_agriculture_projects.form.price");
		if (!Checker.isEmpty(moneyValue)) {
			//将金额转化为分
			String updateSql = "UPDATE mall_agriculture_projects  SET stock_price =" + moneyValue+"*100  WHERE id='" + id + "' ";
			db.execute(updateSql);
		}
	}
}
