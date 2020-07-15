package com.am.cro.tenancytermcheck;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.cro.LeaseState;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;

/**
 * 汽修厂租期检查
 * 
 * @author guorenjie
 *
 */
public class TenancyTermCheck implements Job {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		logger.info("开始执行计划任务-----汽修厂租期检查！");
		DBManager db = new DBManager();
		// 汽修厂租期到期时间
		String LeaseExpireDate = "";
		String today = getToDay();
		String orgid = "";
		String leasestate = "";
		MapList mapList = db
				.query("select * from aorg where leaseexpiredate is not null");
		for (int i = 0; i < mapList.size(); i++) {
			orgid = mapList.getRow(i).get("orgid");
			LeaseExpireDate = mapList.getRow(i).get("leaseexpiredate");
			leasestate = mapList.getRow(i).get("leasestate");
			//如果汽修厂未停用
			if (leasestate.equals("1")) {

				logger.info("汽修厂租期到期时间LeaseExpireDate========="
						+ LeaseExpireDate);
				// 如果租期到期时间等于当前时间，汽修厂设为停用
				if (LeaseExpireDate.substring(0, 10).equals(today)) {
					leasestate = "0";
					String sql = "update aorg set leasestate='" + leasestate
							+ "' where orgid = '" + orgid + "'";
					db.execute(sql);
				}
				// 如果停用，用户过期日期设为前一天，否则清空过期日期
				if (leasestate.equals("0")) {
					logger.info("汽修厂已停用");
					String date = LeaseState.getYesterday();
					String updateSql2 = "UPDATE AUSER set expireddate='" + date
							+ "',expired=1 WHERE orgid like '" + orgid + "%'";
					db.execute(updateSql2);
				} else {
					logger.info("汽修厂未停用");
					String date = null;
					String updateSql3 = "UPDATE AUSER set expireddate=" + date
							+ ",expired=0 WHERE orgid like '" + orgid + "%'";
					db.execute(updateSql3);
				}

			}

		}
		logger.info("计划任务执行完成-----！");
	}

	/**
	 * 获取当天的日期
	 * 
	 * @return
	 */
	public static String getToDay() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(date);
		return dateNowStr;
	}

}
