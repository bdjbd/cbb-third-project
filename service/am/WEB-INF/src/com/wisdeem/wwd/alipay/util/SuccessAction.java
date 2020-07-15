package com.wisdeem.wwd.alipay.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 充值成功之后修改到期时间
 * 
 * @author liyushuang
 * @date 2013-12-18
 * */
public class SuccessAction {

	public static String SuccessPara(Map<String, String> sPara)
			throws JDBCException, Exception {
		DB db = DBFactory.getDB();
		String out_trade_no = sPara.get("out_trade_no");
		String buyer_email = sPara.get("buyer_email");
		// 支付宝返回的充值成功的时间
		String notify_time = sPara.get("notify_time");

		String maxDate = "select MAX(expiration_date) as last_expiration_date from ws_alipay_order where orgid in "
				+ "(select orgid from ws_alipay_order where out_trade_no='"
				+ out_trade_no + "' )";
		MapList maxStr = db.query(maxDate);

		String monthSQL = "select a.orgid as orgid,a.out_trade_no,a.monthly_fee_id,a.quantity as num,b.quantity as month from ws_alipay_order a "
				+ "left join ws_enterprise_month_tariff b on a.monthly_fee_id=b.monthly_fee_id where a.out_trade_no='"+ out_trade_no + "'";
		MapList str = db.query(monthSQL);
		
		String quantity = str.getRow(0).get("num");
		String month = str.getRow(0).get("month");
		int mon = Integer.parseInt(quantity) * Integer.parseInt(month);

		// 当前机构最近的到期日期
		String last_expiration_date = maxStr.getRow(0).get("last_expiration_date");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String nowDate = sdf.format(calendar.getTime());
		String expiration_date = "";

		if (last_expiration_date != null && last_expiration_date != "") {
			long s1 = sdf.parse(nowDate).getTime();
			long s2 = sdf.parse(last_expiration_date).getTime();
			if (s1 < s2) {
				Date now = sdf.parse(last_expiration_date);
				calendar.setTime(now);
				calendar.add(Calendar.MONTH, mon);
				expiration_date = sdf.format(calendar.getTime());
			} else {
				Date now = sdf.parse(notify_time);
				calendar.setTime(now);
				calendar.add(Calendar.MONTH, mon);
				expiration_date = sdf.format(calendar.getTime());
			}
		} else {
			Date now = sdf.parse(notify_time);
			calendar.setTime(now);
			calendar.add(Calendar.MONTH, mon);
			expiration_date = sdf.format(calendar.getTime());
		}
		String sql = "update ws_alipay_order set create_time='" + notify_time
				+ "',buyer_email='" + buyer_email + "'," + "expiration_date='"
				+ expiration_date + "',data_status=" + 2
				+ " where out_trade_no='" + out_trade_no + "'";
		db.execute(sql);

		// 用户过期日期：expireddate（auser表）
		String userdateSQL="update auser set expireddate='"+expiration_date+"' where orgid in "
				+ "(select orgid from ws_alipay_order where out_trade_no='"+out_trade_no + "' )";
		db.execute(userdateSQL);
		
		return "SUCCESS";

	}
}
