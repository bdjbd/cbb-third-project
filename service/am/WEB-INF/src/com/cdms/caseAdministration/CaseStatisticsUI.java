package com.cdms.caseAdministration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Formatter;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class CaseStatisticsUI implements Formatter {
	//转换*分*秒格式
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());

		@Override
		public String format(String a, String b) {
			String order_time = "";
			if (!Checker.isEmpty(a)) {
				int mm_order_time = (int) (Double.parseDouble(a) / 60);
				int ss_order_time = (int) (Double.parseDouble(a) % 60);
				order_time = mm_order_time + "分" + ss_order_time + "秒";
				logger.info("++++++++++++++++++++++++++++" + order_time);

			}
			return order_time;
		
	}

}
