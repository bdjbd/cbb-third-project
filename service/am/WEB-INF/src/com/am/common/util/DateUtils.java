package com.am.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author YueBin
 * @create 2016年6月20日
 * @version 
 * 说明:<br />
 * Java 日期处理类
 */
public class DateUtils {

	/**
	 * 获取两个日期之间的所有的日期
	 * @param startDateStr 开始日期
	 * @param endDateStr  结束日期
	 * @return 返回开始日期和结束日期直接的差，如果结束日期小于开始日期，则返回null
	 */
	public List<Date> getDiffDate(String startDateStr,String endDateStr){
		
		List<Date> result=new ArrayList<Date>();
		
		Date startDate=null;
		Date endDate=null;
		try {
			//开始日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = sdf.parse(startDateStr);
			
			//结束日期
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			endDate= sdf.parse(endDateStr);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(startDate.getTime()>endDate.getTime()){
			//开始日期大于结束日期
			return null;
		}
		
		
		Calendar start = Calendar.getInstance();  
	    start.setTime(startDate);
	    
	    Long startTIme = start.getTimeInMillis();  
	    Calendar end = Calendar.getInstance();  
	    end.setTime(endDate);
	    
	    
	    Long endTime = end.getTimeInMillis();  
	    Long oneDay = 1000 * 60 * 60 * 24l;  
	    Long time = startTIme;  
	    
	    while (time <= endTime) {
	        Date d = new Date(time);  
	        result.add(d);
	        time += oneDay;  
	    }
	    
	    return result;
	}
 	
}
