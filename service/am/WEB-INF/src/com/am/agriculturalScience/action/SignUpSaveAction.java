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
 *说明：报名管理保存Action
 */
public class SignUpSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 发布内容
		Table table=ac.getTable("mall_publishing_content");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		// 最大人数
		int maxNumber = Integer.parseInt(ac.getRequestParameter("mall_sign_up.form.max_number"));
		// 最小人数
		int minNumber = Integer.parseInt(ac.getRequestParameter("mall_sign_up.form.min_number"));
		// 费用（元）
		String cost = ac.getRequestParameter("mall_sign_up.form.money");
		if(maxNumber<minNumber){
			ac.getActionResult().addErrorMessage("最大人数应大于最小人数");
			ac.getActionResult().setSuccessful(false);
			return ;
		}
		if (!Checker.isEmpty(cost)) {
			//将费用金额转化为分
			String updateSql = "UPDATE mall_publishing_content  SET cost =" + cost+"*100  WHERE id='" + id + "' ";
			db.execute(updateSql);
		}
	}
}
