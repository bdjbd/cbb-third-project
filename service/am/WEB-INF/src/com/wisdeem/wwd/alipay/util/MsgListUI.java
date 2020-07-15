package com.wisdeem.wwd.alipay.util;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;

public class MsgListUI implements UnitInterceptor {
	private static final long serialVersionUID = 4862102095772912047L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		DB db = DBFactory.getDB();
		String orgid = ac.getVisitor().getUser().getOrgId();

		String maxMsgSQL = "select max_textmsg,explain from WS_ENTERPRISE_MONTH_TARIFF a left join WS_ORG_BASEINFO b "
				+ "on a.monthly_fee_id=b.monthly_fee_id where b.orgid='"
				+ orgid + "' ";
		MapList list = db.query(maxMsgSQL);
		// 此公众帐号最大可发消息数
		if (list.size() > 0) {
			String maxmsg = list.getRow(0).get("max_textmsg");
			String explain = list.getRow(0).get("explain");
			unit.getElement("max_textmsg").setDefaultValue(maxmsg);
			unit.getElement("explain").setDefaultValue(explain);
		}
		return unit.write(ac);
	}
}
