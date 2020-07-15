package com.wisdeem.wwd.msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;

public class MsgCountListUI implements UnitInterceptor {
	private static final long serialVersionUID = 4862102095772912047L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		DB db = DBFactory.getDB();
		String orgid = ac.getVisitor().getUser().getOrgId();

		String maxMsgSQL = "select a.max_textmsg as maxmsg from ws_enterprise_month_tariff  a left join ws_org_baseinfo b "
				+ "on a.monthly_fee_id=b.monthly_fee_id where b.orgid='"
				+ orgid + "' ";
		MapList list = db.query(maxMsgSQL);
		if (list.size() > 0) {
			// 此公众帐号最大可发消息数
			String maxMsgLen = list.getRow(0).get("maxmsg");
			// 获取系统当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = df.format(new Date());
			String countSQL = "SELECT count(*) as count from WS_STATISTICS where orgid='"
					+ orgid
					+ "' and to_char(callinter_time,'yyyy-mm-dd')='"
					+ nowDate + "'";
			MapList mapList = db.query(countSQL);
			// 此公众帐号今日已发消息数
			String count = mapList.getRow(0).get("count");
			// 此公众帐号今日剩余可发消息数
			int unUsedLen = Integer.parseInt(maxMsgLen)
					- Integer.parseInt(count);
			unit.getElement("msgcount").setDefaultValue(
					" <span style='color:red'>(今日剩余 "
							+ Integer.toString(unUsedLen) + " 条消息可发送)</span>");
		}else{
			unit.getElement("msgcount").setDefaultValue(
					" <span style='color:red'>(今日剩余 "
							+ 0 + " 条消息可发送)</span>");
		}
		return unit.write(ac);
	}
}
