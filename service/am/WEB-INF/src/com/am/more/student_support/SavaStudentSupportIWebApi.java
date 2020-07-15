package com.am.more.student_support;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author wangxi
 * @create 2016年5月24日
 * @version 说明：大学生资助--资助WebApi
 */
public class SavaStudentSupportIWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {

		DB db = null;
		JSONObject resultJson = null;
		try {
			db = DBFactory.newDB();
			// 交易ID
			String payId = request.getParameter("payid");
			// 资助id
			String collegeId = request.getParameter("collegeId");
			// 资助会员id
			String memberid = request.getParameter("memberid");
			// 被资助会员id
			String tomemberId = request.getParameter("tomemberId");
			// 资助金额
			String paymoney = request.getParameter("paymoney");
			// 根据订单id查询交易是否成功未处理
			String selPaySql = "SELECT * FROM mall_trade_detail WHERE id='" + payId
					+ "' AND trade_state='1' AND is_process_buissnes='0'";

			resultJson = new JSONObject();
			MapList selPayMap = db.query(selPaySql);
			if (!Checker.isEmpty(selPayMap)) {
				UpdatePlayersServer server = new UpdatePlayersServer();
				// 给大学生资助人信息表中添加数据
				updateCollegeHelper(db, memberid, paymoney, collegeId);
				// 修改资助信息表的状态
				updateCollegeInfo(db, collegeId);
				// 修改资助会员的已资助次数
				updateMember(db, memberid);
				// 修改被资助会员的应资助次数和共接受次数
				updateToMember(db, tomemberId);
				// 修改交易表的状态
				server.updateTransaction(db, payId);

				// 大学生资助
				// 获取志愿者账号提现资格任务 START
				TaskEngine taskEngine = TaskEngine.getInstance();
				RunTaskParams params = new RunTaskParams();
				params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				// 获取志愿者账号提现资格任务 END

			} else {
				resultJson.put("code", "999");
				resultJson.put("msg", "交易已处理");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}

		return resultJson.toString();
	}

	/**
	 * 修改被资助会员的应资助次数和共接受次数
	 * 
	 * @param db
	 * @param tomemberId
	 *            被资助会员id
	 * @throws JDBCException
	 */
	private void updateToMember(DB db, String tomemberId) throws JDBCException {

		String frequency = Var.get("every_shold_aid_times");

		String updateToMemberSql = "UPDATE am_member SET total_aid_times=(total_aid_times+1),shold_aid_times=(shold_aid_times+"
				+ frequency + ") WHERE id='" + tomemberId + "' ";
		db.execute(updateToMemberSql);
	}

	/**
	 * 修改资助会员的已资助次数
	 * 
	 * @param db
	 * @param memberid
	 *            资助会员id
	 * @throws JDBCException
	 */
	private void updateMember(DB db, String memberid) throws JDBCException {

		String updateMemberSql = "UPDATE am_member SET already_aid_times=(already_aid_times+1) WHERE id='" + memberid
				+ "' ";
		db.execute(updateMemberSql);

	}

	/**
	 * 修改资助信息表的状态
	 * 
	 * @param db
	 * @param collegeId
	 *            信息表的id
	 * @throws JDBCException
	 */
	private void updateCollegeInfo(DB db, String collegeId) throws JDBCException {

		String updateCollegeInfoSql = "UPDATE mall_college_student_aid SET status='5' WHERE id='" + collegeId + "' ";
		db.execute(updateCollegeInfoSql);

	}

	/**
	 * 给大学生资助人信息表中添加数据
	 * 
	 * @param db
	 * @param memberid
	 *            资助会员id
	 * @param paymoney
	 *            资助金额（元）
	 * @param collegeId
	 *            资助信息id
	 * @throws JDBCException
	 */
	private void updateCollegeHelper(DB db, String memberid, String paymoney, String collegeId) throws JDBCException {

		String uuid = UUID.randomUUID().toString();
		String sqlinsert = "INSERT INTO mall_college_helper( id, aid_id, member_id, amount_of_subsidy,create_time) "
				+ "values ('" + uuid + "','" + collegeId + "','" + memberid + "'," + paymoney + "*100,now()) ";
		db.execute(sqlinsert);

	}

}
