package com.fastunit.demo.flow;

import com.fastunit.Page;
import com.fastunit.constants.UnitShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.framework.config.DomainsSettings;
import com.fastunit.support.PageInterceptor;

/**
 * demo域的默认模板是page.html，系统的默认模板可能是page.bar.html<br>
 * 因此可能造成demo下两个流程的发起和后续执行处于不同布局下<br>
 * 此类依据app域的设置动态设模板。
 */
public class BarPI implements PageInterceptor {

	@Override
	public void intercept(ActionContext ac, Page page) throws Exception {
		if (DomainsSettings.getPageTemplate("app").equals("page.bar.html")
				&& !UnitShowMode.ADD.equals(page.getUnit().getShowMode())) {
			page.setTemplate("page.bar.html");
		}
		page.write(ac);
	}

}
