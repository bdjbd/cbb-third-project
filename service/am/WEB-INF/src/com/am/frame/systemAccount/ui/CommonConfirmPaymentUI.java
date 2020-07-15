package com.am.frame.systemAccount.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/***
 * 公共支付页面UI
 * @author yuebin
 *
 */
public class CommonConfirmPaymentUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//标题
		String title=ac.getRequestParameter("title");
		if(!Checker.isEmpty(title)){
			unit.setTitle(title);
		}
		
		return unit.write(ac);
	}

}
