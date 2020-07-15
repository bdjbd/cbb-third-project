package com.cdms.caseAdministration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.support.Formatter;
import com.fastunit.util.Checker;

public class HourUi implements Formatter{
	//转换*时格式
			private static final long serialVersionUID = 1L;
			private Logger logger = LoggerFactory.getLogger(getClass());
			@Override
			public String format(String a, String b) {
				String order_time = "";
				if (!Checker.isEmpty(a)) {
					int hh_order_time = (int) (Double.parseDouble(a) / 3600);
					
					order_time =hh_order_time+ "时" ;
					logger.info("++++++++++++++++++++++++++++" + order_time);
				}
				return order_time;
			}
}
