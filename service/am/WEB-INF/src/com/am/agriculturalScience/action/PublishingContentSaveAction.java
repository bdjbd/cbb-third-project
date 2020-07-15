package com.am.agriculturalScience.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月26日
 *@version
 *说明：文章管理保存Action
 */
public class PublishingContentSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 发布内容
		Table table=ac.getTable("mall_publishing_content");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		
		// 费用（元）
		String cost = ac.getRequestParameter("mall_publishing_content.form.money");
		if (!Checker.isEmpty(cost)) {
			//将费用金额转化为分
			String updateSql = "UPDATE mall_publishing_content  SET cost =" + cost+"*100  WHERE id='" + id + "' ";
			db.execute(updateSql);
		}
		ac.setSessionAttribute("mall_publishing_content.form.id", id);
	}
}
