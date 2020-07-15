package com.am.instore.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fastunit.support.IncrementorStringCreator;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：出库-出库单号自增器
 */
public class OutStoreIncreasing implements IncrementorStringCreator{

	@Override
	public String getPrefix() {
		
		Date date = new Date();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
		String time = sFormat.format(date);
		String str ="";
		str+="CK";
		str+=time;
		return str;
	}

	@Override
	public String getSuffix() {
		return null;
	}

}
