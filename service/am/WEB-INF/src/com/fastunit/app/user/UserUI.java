package com.fastunit.app.user;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.Element;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.context.LocalContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.CookieUtil;

public class UserUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8646633545955360335L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		HttpServletRequest request = LocalContext.getLocalContext()
				.getHttpServletRequest();
		HttpServletResponse response = LocalContext.getLocalContext()
				.getHttpServletResponse();

		String unitId = ActionContextHelper.getActionUnitId(ac);
		Cookie userIdCookie = CookieUtil.getCookie(request, "userid");
		if (userIdCookie != null) {
			String userId = userIdCookie.getValue();
			Element userElement = unit.getElement("userid1");
			userElement.setDefaultValue(userId);
		}

		Cookie passwordCookie = CookieUtil.getCookie(request, "password");
		if (passwordCookie != null) {
			String password = passwordCookie.getValue();
			Element passwordElement = unit.getElement("password1");
			passwordElement.setDefaultValue(password);
		}

		return unit.write(ac);
	}

}
