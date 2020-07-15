package com.fastunit.app.user;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.framework.sso.SSOUtil;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.util.DBHelper;
import com.fastunit.user.config.UserLevel;
import com.fastunit.user.table.AUser;
import com.fastunit.util.DateUtil;

public class ExpiredJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(ExpiredJob.class);
	// 用于处理以下情形：
	// (1)修改服务器时间的情形
	// (2)兼容旧版本、设置初始值
	private static final String UPDATE_UN_EXPIRED = "update " + AUser.TABLENAME
			+ " set " + AUser.EXPIRED + "=0 where (" + AUser.EXPIRED + "=1 or "
			+ AUser.EXPIRED + " is null) and (" + AUser.EXPIRED_DATE + " is null or "
			+ AUser.EXPIRED_DATE + ">? or " + AUser.USER_LEVEL + ">="
			+ UserLevel.SUPER_USER + ")";
	// 查找过期
	private static final String QUERY_EXPIRED = "select " + AUser.USER_ID
			+ " from " + AUser.TABLENAME + " where (" + AUser.EXPIRED + "=0 or "
			+ AUser.EXPIRED + " is null) and " + AUser.EXPIRED_DATE + "<=? and "
			+ AUser.USER_LEVEL + "<" + UserLevel.SUPER_USER;
	// 设置 过期
	private static final String UPDATE_EXPIRED = "update " + AUser.TABLENAME
			+ " set " + AUser.EXPIRED + "=1 where " + AUser.USER_ID + "=?";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String currentDate = DateUtil.getCurrentDate();
		DB db = null;
		try {
			db = DBFactory.newDB();// 必须使用newDB并在finally中手动关闭
			db.execute(UPDATE_UN_EXPIRED, currentDate, Type.DATE);
			MapList list = db.query(QUERY_EXPIRED, currentDate, Type.DATE);
			for (int i = 0; i < list.size(); i++) {
				String userId = list.getRow(i).get(AUser.USER_ID);
				db.execute(UPDATE_EXPIRED, userId, Type.VARCHAR);
				SSOUtil.disableUser(userId);
			}
		} catch (Exception e) {
			log.error("error", e);
		} finally {
			DBHelper.close(db);
		}
	}

}