package com.cdms;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.push.server.JPushNotice;
import com.am.frame.webapi.db.DBManager;
import com.cdms.remind.AnnualInspectionRemindSql;
import com.cdms.remind.InsuranceExpireRemindSql;
import com.cdms.remind.MaintainRemindSql;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class SendRemindNews implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 计划任务，调用insertAndSendNews()方法
	 */
	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {

		DBManager db = new DBManager();
		try {
			// 保养
			insertAndSendNewsMaintenance(db);
			// 年检
			insertAndSendNewsAnnualInspection(db);
			// 保险
			insertAndSendNewsInsurance(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 检查保养到期的车辆，并将数据存入消息表并推送
	private void insertAndSendNewsMaintenance(DBManager db) throws Exception {

		// 查询所有的车辆(保养sql返回的数据)
		MaintainRemindSql mrs = new MaintainRemindSql();
		MapList carMapList = mrs.getRemindMapList();

		// 获得当前时间戳
		Date datenew = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = df.format(datenew);

		// 如果查询为空，继续执行
		if (!Checker.isEmpty(carMapList)) {
			for (int i = 0; i < carMapList.size(); i++) {

				// 获取当前车辆的id
				String carId = carMapList.getRow(i).get("id");
				// 获取当前车辆所在机构的机构id
				String orgId = carMapList.getRow(i).get("orgcode");
				// 获取当前车辆的车牌号码
				String license_plate_number = carMapList.getRow(i).get(
						"license_plate_number");

				// 获取当前车辆的:当前保养里程---已行驶里程
				String current_mileage = carMapList.getRow(i).get(
						"current_mileage");

				// 如果保养到期，保养提醒插入
				// 当前保养里程>=保养提醒里程，进行提醒

				// 主键
				UUID uuid = UUID.randomUUID();
				String title = "车牌号码为" + license_plate_number + "的车辆保养即将到期";
				String content = "车牌号码为" + license_plate_number + "的车辆当前保养里程为"
						+ current_mileage + "km，请及时保养";

				String querySql = "select * from cdms_Message where remind_type='1' and car_id='"
						+ carId + "'";
				if (Checker.isEmpty(db.query(querySql))) {

					String sql = "insert into cdms_Message (id,car_id,remind_type,remind_title,remind_content,remind_state,createtime) values ('"
							+ uuid
							+ "','"
							+ carId
							+ "','1','"
							+ title
							+ "','"
							+ content + "','1','" + dateNowStr + "')";
					db.execute(sql);
					push(db, orgId, title, content, uuid.toString());

				}
			}
		}
	}

	// 检查年检到期的车辆，并将数据存入消息表并推送
	private void insertAndSendNewsAnnualInspection(DBManager db)
			throws Exception {

		// 查询所有的车辆(年检sql返回的数据)
		AnnualInspectionRemindSql airs = new AnnualInspectionRemindSql();
		MapList carMapList = airs.getRemindMapList();

		// 获得当前时间戳
		Date datenew = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = df.format(datenew);

		if (!Checker.isEmpty(carMapList)) {
			for (int i = 0; i < carMapList.size(); i++) {

				// 获取当前车辆的id
				String carId = carMapList.getRow(i).get("id");
				// 获取当前车辆所在机构的机构id
				String orgId = carMapList.getRow(i).get("orgcode");
				// 获取当前车辆的车牌号码
				String license_plate_number = carMapList.getRow(i).get(
						"license_plate_number");
				// 获取当前车辆的年检到期时间
				String annualInspectionMaturity = carMapList.getRow(i).get(
						"annualinspectionmaturity");
				// 获取当前车辆的:年检到期剩余天数
				double annualInspectionLastDay = Double.parseDouble(carMapList
						.getRow(i).get("annualinspectionlastday"));

				// 如果年检到期，年检提醒插入
				// 剩余天数<=提醒天数，进行提醒
				// 年检剩余天数=（（上次年检时间+年检间隔）-当前时间）

				// 主键
				UUID uuid = UUID.randomUUID();

				String title = "车牌号码为" + license_plate_number + "的车辆年检即将到期";
				String content = "车牌号码为" + license_plate_number + "的车辆年检将于"
						+ annualInspectionMaturity + "到期，请及时处理";

				String querySql = "select * from cdms_Message where remind_type='2' and car_id='"
						+ carId + "' ";
				// 未读时如果没有相同的车相同类型则添加一条年检提醒消息
				if (Checker.isEmpty(db.query(querySql))) {
					String sql = "insert into cdms_Message (id,car_id,remind_type,remind_title,remind_content,remind_state,createtime) values ('"
							+ uuid
							+ "','"
							+ carId
							+ "','2','"
							+ title
							+ "','"
							+ content + "','1','" + dateNowStr + "')";
					db.execute(sql);
					push(db, orgId, title, content, uuid.toString());
				}

			}
		}
	}

	// 检查保险到期的车辆，并将数据存入消息表并推送
	private void insertAndSendNewsInsurance(DBManager db) throws Exception {

		// 查询所有的车辆(保险sql返回的数据)
		InsuranceExpireRemindSql iers = new InsuranceExpireRemindSql();
		MapList carMapList = iers.getRemindMapList();

		// 获得当前时间戳
		Date datenew = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateNowStr = df.format(datenew);

		// 获取当前车辆的:保险提前提醒天数

		// 如果查询结果不为空，继续执行
		if (!Checker.isEmpty(carMapList)) {
			for (int i = 0; i < carMapList.size(); i++) {

				// 获取当前车辆的id
				String carId = carMapList.getRow(i).get("id");
				// 获取当前车辆所在机构的机构id
				String orgId = carMapList.getRow(i).get("orgcode");
				// 获取当前车辆的车牌号码
				String license_plate_number = carMapList.getRow(i).get(
						"license_plate_number");
				// 获取当前车辆的保险到期时间
				String duration_of_insurance = carMapList.getRow(i).get(
						"duration_of_insurance");

				// 获取当前车辆的:保险到期剩余天数
				double insurancelastday = Double.parseDouble(carMapList.getRow(
						i).get("insurancelastday"));

				// 如果保险到期，保险提醒插入
				UUID uuid = UUID.randomUUID();
				// String title = "保险提醒";
				// String content = "您的车辆保险快到期了！！！";
				String title = "车牌号码为" + license_plate_number + "的车辆保险即将到期";
				String content = "车牌号码为" + license_plate_number + "的车辆保险将于"
						+ duration_of_insurance + "到期，请及时处理";
				// 查询当前报警表里面是否有相同类型的消息

				String querySql = "select * from cdms_Message where remind_type='3' and car_id='"
						+ carId + "'";

				// 未读时如果没有相同的车相同类型则添加一条年检提醒消息
				if (Checker.isEmpty(db.query(querySql))) {
					String sql = "insert into cdms_Message (id,car_id,remind_type,remind_title,remind_content,remind_state,createtime) values ('"
							+ uuid
							+ "','"
							+ carId
							+ "','3','"
							+ title
							+ "','"
							+ content + "','1','" + dateNowStr + "')";
					db.execute(sql);
					push(db, orgId, title, content, uuid.toString());
				}
			}
		}

	}

	/**
	 * 推送
	 * 
	 * @param db
	 * @param orgid
	 * @param title
	 * @param content
	 * @param id
	 */
	public void push(DBManager db, String orgid, String title, String content,
			String id) {
		// 根据当前车辆所在机构的机构id，查询人员，得到同一机构下的所有人员的xtoken注册码
		String memberSql = "select * from mall_mobile_type_record where member_id in (select userid from auser where orgid='"
				+ orgid + "')";
		MapList memberMapList = db.query(memberSql);
		if (!Checker.isEmpty(memberMapList)) {
			for (int i = 0; i < memberMapList.size(); i++) {
				// 取得要发送的消息的json对象
				JSONObject json = new JSONObject();// 报警内容
				JSONObject pushData = new JSONObject();
				JSONObject params = new JSONObject();// 报警内容ID
				params.put("id", id);
				json.put("describe", content);
				json.put("title", title);
				pushData.put("url", "commodity.newsDetail");
				pushData.put("params", params);
				json.put("DATA", pushData);

				// 进行消息推送(mobile_type: 手机类型，1为android，2为ios)
				if ("1".equals(memberMapList.getRow(i).get("mobile_type"))) {
					Collection<Object> coll = new LinkedList<Object>();
					coll.add(memberMapList.getRow(i).get("xtoken"));
					// 发送
					JPushNotice.sendNoticeById(json, coll, "1");
				} else if ("2".equals(memberMapList.getRow(i)
						.get("mobile_type"))) {
					Collection<Object> coll = new LinkedList<Object>();
					coll.add(memberMapList.getRow(i).get("xtoken"));
					// 发送
					JPushNotice.sendNoticeById(json, coll, "2");
				}
			}

		}
	}
}