package com.wisdeem.wwd.management;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.UnitShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class SetupFormUI implements UnitInterceptor{
	private static final long serialVersionUID = -3349672306115769003L;
    /**
     * 企业管理：判断是新增模式还是修改模式
     * 2013-11-13
     * */ 
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 获得DB链接
		DB db = DBFactory.getDB();

		String orgid = ac.getRequestParameter("ws_org_baseinfo.form.orgid");
		
		String SQL_SETUP = "select e_type from ws_org_baseinfo where orgid='"
				+ orgid + "'";

		if (!Checker.isEmpty(orgid)) {

			MapList lstSetup = db.query(SQL_SETUP);

			// a为新增模式
			if (lstSetup.size() < 1) {
				// 将主表单元设为新增模式
				unit.setShowMode(UnitShowMode.ADD);
			} else if (lstSetup.size() >= 1) {
				// 将附件单元设为修改模式
				unit.setShowMode(UnitShowMode.EDIT);
			}
		}

		return unit.write(ac);
	}

}
