package com.am.frame.systemAccount.ui;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 转账UI
 * @author yuebin
 *1,参数：title 标题。
 *2,参数：ordercode  订单编号。
 *3,参数：paymoney  金额，单位元。
 *4,参数：business   业务参数。
 *5,参数：success_url 成功url
 *6,参数：lost_url   失败url
 */

public class AccountRechangeManagerUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//标题，
		String title=ac.getRequestParameter("title");
		if(!Checker.isEmpty(title)){
			unit.setTitle(title);
		}
		
		
		return unit.write(ac);
	}

}
