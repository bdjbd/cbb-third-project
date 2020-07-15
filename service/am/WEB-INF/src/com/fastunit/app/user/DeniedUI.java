package com.fastunit.app.user;

import com.fastunit.LangUtil;
import com.fastunit.Unit;
import com.fastunit.app.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.user.Guest;

public class DeniedUI implements UnitInterceptor {
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String message;
		if (ac.getVisitor().getUser() instanceof Guest) {
			message = LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.DENIED_WITHOUT_LOGIN);
		} else {
			message = LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.DENIED);
			unit.removeElement("login");
		}
		unit.getElement("message").setCustom(message);
		return unit.write(ac);
	}

}
