package com.am.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.fastunit.util.Checker;

public class DateFormatUtil {

	/**
	 * 日期格式化 日期格式可能有：12-2-2016,
	 * 1-7-2017,
	 * @param dataStr
	 * @return
	 */
	public String getFormatDate(String dataStr){
		
		//12-2-2016,
		//
		
		String result="";
		SimpleDateFormat sdf;
		
		if(Checker.isEmpty(dataStr)){
			return "";
		}
		
		dataStr=dataStr.replaceAll(",","");
		dataStr=dataStr.replaceAll("/","-");
		
		try{
			if(Pattern.compile("^\\d{1,2}\\-+\\d{1,2}\\-+\\d{4}$").matcher(dataStr).matches()){
				String[] dataStrs=dataStr.split("-");
				dataStr=dataStrs[2]+"-"+dataStrs[0]+"-"+dataStrs[1];
				
				sdf=new SimpleDateFormat("yyyy-MM-dd");
				Date d=sdf.parse(dataStr);
				result=sdf.format(d);
			}
			
			if(Pattern.compile("^\\d{4}\\-+\\d{1,2}\\-+\\d{1,2}$").matcher(dataStr).matches()){
				sdf=new SimpleDateFormat("yyyy-MM-dd");
				
				Date d=sdf.parse(dataStr);
				result=sdf.format(d);
			}
			if(Pattern.compile("^\\d{4}\\/+\\d{1,2}\\/+\\d{1,2}$").matcher(dataStr).matches()){
				sdf=new SimpleDateFormat("yyyy-MM-dd");
				
				Date d=sdf.parse(dataStr);
				result=sdf.format(d);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
}
