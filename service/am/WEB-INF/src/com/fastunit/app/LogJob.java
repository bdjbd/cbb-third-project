package com.fastunit.app;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.framework.log.TAccessLog;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.util.DBHelper;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;

public class LogJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(LogJob.class);

	private static final String SQL = "delete from " + TAccessLog.TABLENAME
			+ " where " + TAccessLog.TIME.name + "<?";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		String daysString = map.getString("days");
		if (!Checker.isPositiveInteger(daysString)) {
			log.warn("no valid days!");
			return;// 没有设置天数、或不是正整数时，不作操作
		}
		int days = Integer.parseInt(daysString);
		String currentDate = DateUtil.getCurrentDate();
		String date = DateUtil.addDateByDay(currentDate, -days);
		DB db = null;
		try {
			db = DBFactory.newDB();// 必须使用newDB并在finally中手动关闭
			db.execute(SQL, date, Type.TIMESTAMP);
		} catch (Exception e) {
			log.error("error", e);
		} finally {
			DBHelper.close(db);
		}
	}

}
