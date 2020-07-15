package com.am.cro.bespeak;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

/**
 * 获取预约时间
 * 
 * @author guorenjie
 *
 */
public class BespeakTime implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		JSONArray rValue = new JSONArray();

		String bespeakDate = request.getParameter("bespeakDate");// 预约日期
		String orgcode = request.getParameter("orgcode");// 所属机构

		// 把当前时间转成日历（处理跨月问题）
		Calendar calendar = Calendar.getInstance();// 日历类
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 日期格式化yyyy-MM-dd
		Date date = null;
		try {
			date = format.parse(bespeakDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		calendar.setTime(date);

		rValue = getBespeakTime(orgcode, calendar);

		return rValue.toString();
	}

	/**
	 * 获取预约时间，如果为空，则预约已满
	 * 
	 * @param orgcode
	 * @param weekday
	 * @return
	 */
	public JSONArray getBespeakTime(String orgcode, Calendar calendar) {
		DBManager db = new DBManager();

		// 获得预约日期的星期
		int weekindex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		// 获得预约日期
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 日期格式化yyyy-MM-dd
		String orderdate = format.format(calendar.getTime());

		JSONArray rValue = new JSONArray();
		

		String booktime = "";// 预约时间
		MapList booktimeMapList = db
				.query("select booktime from cro_bookingmanagement where orgcode = '"
						+ orgcode + "' and weekday = '" + weekindex + "' order by booktime");
		JSONObject res = null;
		for (int i = 0; i < booktimeMapList.size(); i++) {
			res = new JSONObject();
			booktime = booktimeMapList.getRow(i).get("booktime");
			String sql = "select booktime,maxbooknum from cro_bookingmanagement where "
					+ "orgcode = '"
					+ orgcode
					+ "' "
					+ "and weekday = '"
					+ weekindex
					+ "' "
					+ "and booktime = '"
					+ booktime
					+ "' "
					+ "and maxbooknum>(select count(id) from cro_carrepairorder where orgcode='"
					+ orgcode
					+ "' "
					+ "and ordertime='"
					+ booktime
					+ "'"
					+ "and orderdate = '"
					+ orderdate
					+ "') "
					+ "and to_timestamp('"
					+ orderdate
					+ " "
					+ booktime
					+ "', 'yyyy-mm-dd hh24:mi:ss')>now()";
			MapList mapList = db.query(sql);
			if(mapList.size()>0){
				try {
					res.put("BOOKTIME", mapList.getRow(0).get("booktime"));
					res.put("MAXBOOKNUM", mapList.getRow(0).get("maxbooknum"));
					rValue.put(res); 
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
						
		}
		return rValue;
	}

}
