package com.am.instore.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fastunit.support.IncrementorStringCreator;

/**
 *@author wangxi
 *@create 2016年4月27日
 *@version
 *说明：入库-入库单号自增器
 */
public class InStoreIncreasing implements IncrementorStringCreator{

	@Override
	public String getPrefix() {
		
		Date date = new Date();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
		String time = sFormat.format(date);
		String str ="";
		str+="RK";
		str+=time;
		return str;
	}

	@Override
	public String getSuffix() {
		return null;
	}

}