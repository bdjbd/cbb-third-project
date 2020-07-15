package com.cdms.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * 结束时间+1的工具类
 * @author guorenjie
 */
public class DateTool {
	private static Logger logger = Logger.getLogger(DateTool.class);
	public static String dateAddOne(String endTime){
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

		Date date = null;
		try {
			date = sdf.parse(endTime);
		} catch (ParseException e) {
			logger.error("格式化时间失败，时间不是yyyy-MM-dd格式的，time="+endTime);
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
		return sdf.format(calendar.getTime());
	}
}
