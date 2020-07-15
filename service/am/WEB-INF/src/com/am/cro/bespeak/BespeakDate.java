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
 * 获取预约日期
 * 
 * @author guorenjie
 *
 */
public class BespeakDate implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DBManager db = new DBManager();

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//实例化预约时间类
		BespeakTime bespeakTime = new BespeakTime();
		
		JSONArray rValue = new JSONArray();//返回结果
		JSONArray jsonArray = new JSONArray();// 预约时间
		
		//可预约的开始时间
		String bespeakDate = request.getParameter("bespeakDate");  //2017-09-14
		// 所属机构
		String orgcode = request.getParameter("orgcode");
		
		//把当前时间转成日历（处理跨月问题）
		Calendar calendar = Calendar.getInstance();//日历类
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//日期格式化yyyy-MM-dd
		Date date = null;
		try {
			date = format.parse(bespeakDate);  //Thu Sep 14 00:00:00 CST 2017
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		calendar.setTime(date);
				
		//一天的日期和星期值
		JSONObject res=null;
		//获取15天的日期
		for (int i = 0; i < 15; i++) 
		{
			res = new JSONObject();
			calendar.add(Calendar.DAY_OF_MONTH, 1);  //5
			
			String day = format.format(calendar.getTime());// 格式化日期  2017-09-15
			String weekday = getWeekDay(calendar);// 星期值
			
			// 如果返回false则不是假期
			if (isVacation(calendar.getTime(),orgcode)==false) 
			{
				
				//判断该天是否约满
				jsonArray = bespeakTime.getBespeakTime(orgcode, calendar);
				
				if (jsonArray.length() > 0) 
				{
					try {
						res.put("day",day);
						res.put("weekday",weekday);
						rValue.put(res);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
			
			
		}
		logger.info("com.am.cro.bespeak.BespeakDate.rValue.toString()"+rValue.toString());
		return rValue.toString();
	}

	/**
	 * 判断是否是假期
	 * 
	 * @return true:是假期 false：不是假期
	 */
	public boolean isVacation(Date date,String orgcode) 
	{
		boolean rValue = false;
		
		String tSQL = "select * from cro_ExceptionDateSetting where " 
				+ " start_date<='" + date + "'" 
				+ " and end_date>'" + date + "'"
				+ " and orgcode='" + orgcode + "'";
		MapList mapList = db.query(tSQL);
		
		//如果大于0说明是假期，则返回true
		if(mapList.size()>0){
			rValue = true;
		}else {
			rValue=false;
		}
		
		return rValue;
	}
	public String getWeekDay(Calendar calendar){
		String rValue="";
		// 获得预约日期的星期
		int weekindex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		String []weekDay = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
		for (int i = 0; i < weekDay.length; i++) {
			if(weekindex==i){
				rValue = weekDay[i];
			}
		}
		return rValue;
	}

}
