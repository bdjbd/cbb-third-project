package com.fastunit.app;

import com.fastunit.Page;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * 单元top未设为“可访问”（原因：不必授权），本类用于设置js和css引用。
 */
public class TopUI implements UnitInterceptor {

	private static final String CSS = "/domain/app/top/top.css";
	private static final String JS = "/domain/app/top/top.js";

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		Page page = unit.getPage();
		page.addCssPath(CSS);
		page.addTopJsPath(JS);
		return unit.write(ac);
	}

}
