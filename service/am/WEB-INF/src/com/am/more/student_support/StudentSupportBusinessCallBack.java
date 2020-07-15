package com.am.more.student_support;

import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * 大学生资助数据完成回调
 * 
 * @author yuebin
 *
 */
public class StudentSupportBusinessCallBack extends AbstraceBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject result = new JSONObject();
		
		//解析业务参数
		JSONObject jobBus=new JSONObject(business);
		
		//1,交易成功，并且业务为处理，则处理业务
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			// 交易ID
			String payId = jobBus.getString("payid");
			// 资助id
			String collegeId = jobBus.getString("collegeId");
			// 资助会员id
			String memberid = jobBus.getString("memberid");
			// 被资助会员id
			String tomemberId =jobBus.getString("tomemberId");
			//现金账户
			String CASH_ACCOUNT = SystemAccountClass.CASH_ACCOUNT;
			// 资助金额
			String paymoney = jobBus.getString("paymoney");
			
			// 根据订单id查询交易是否成功未处理
			String selPaySql = "SELECT * FROM mall_trade_detail WHERE id='" + payId
					+ "' AND trade_state='1' AND is_process_buissnes='0'";

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
				String selesql = "select msac.sa_code from mall_trade_detail as mtd "
						+ "left join mall_account_info  as mai on mai.id = mtd.account_id "
						+ "left join mall_system_account_class as msac on msac.id = mai.a_class_id where mtd.id= '"+id+"'";
				MapList listss = db.query(selesql);
				//查询判断是否是志愿者账户支付，如果是志愿者账户支付则更新志愿者服务账户提现额度
				if("VOLUNTEER_ACCOUNT".equals(listss.getRow(0).get("sa_code")))
				{
					TaskEngine taskEngine = TaskEngine.getInstance();
					RunTaskParams params = new RunTaskParams();
					params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
					params.pushParam(GetVolunteerAccountWithQualificationItask.AID_STUDENT,VirementManager.changeY2F(paymoney)+"");
					params.setMemberId(memberid);
					taskEngine.executTask(params);
				}
				// 获取志愿者账号提现资格任务 END
				
				String sqls = "select * from mall_account where id = '"+memberid+"'";
				MapList lists = db.query(sqls);
				
				updateProcessBuissnes(payId, db, "1");
				
				result.put("code", "0");
				result.put("msg", "资助大学生完成！");
				VirementManager vm=new VirementManager();
				
				String inremake = "大学生资助";
				if(!Checker.isEmpty(lists))
				{
					inremake += ","+lists.getRow(0).get("loginaccount")+"用户资助,";
				}
				//转账给被资助人现金账户
				vm.execute(db, "", tomemberId, "", CASH_ACCOUNT, paymoney, inremake, "", "", false);
				
//					String updateAccountSQL = "UPDATE mall_account_info SET balance = balance + "+money+" WHERE member_orgid_id = '"+tomemberId+"' "
//							+ "and a_class_id= (SELECT id FROM mall_system_account_class WHERE sa_code = '"+CASH_ACCOUNT+"' ) ";
//					db.execute(updateAccountSQL);
			} else {
				result.put("code", "999");
				result.put("msg", "交易已处理");
			}
		}
		
		
		return result.toString();
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
