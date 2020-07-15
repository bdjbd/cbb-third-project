package com.am.app_plugins.food_safety;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月23日
 * @version 说明:<br />
 *          冷柜支付处理类
 */
public class FreezerOpenPaymenyProcessBusinessCallBack implements
		IBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type)
			throws Exception {
		JSONObject resultJson = null;
		try {
			db = DBFactory.newDB();
			// 支付ID
			String payId =id;// request.getParameter("payId");
			

			// 根据订单id查询交易是否成功未处理
			String selPaySql = "SELECT * FROM mall_trade_detail WHERE id='"
					+ payId
					+ "' AND trade_state='1' AND is_process_buissnes='0'";

			resultJson = new JSONObject();
			
			//支付信息
			MapList selPayMap = db.query(selPaySql);
			if (!Checker.isEmpty(selPayMap)) {
				// 会员id
				String memberid =selPayMap.getRow(0).get("member_id");
				// 转账金额
				String paymoney =selPayMap.getRow(0).get("trade_total_money");
				
				
				// 冷柜开通记录表插数据
				freezerOpen(db, memberid, paymoney);
				
				// 修改交易表的状态
				UpdatePlayersServer server = new UpdatePlayersServer();
				server.updateTransaction(db, payId);
				
				//理性农业  会员权限徽章任务，开通冷柜
				float insertMoeny=Float.parseFloat(paymoney)/100;
				TaskEngine taskEngine=TaskEngine.getInstance();
				RunTaskParams params=new RunTaskParams();
				params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
				
				params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,MemberAuthorityBadgeTask.COMUNT_FREEZER_DEPOSIT);
				params.pushParam(MemberAuthorityBadgeTask.COMUNT_FREEZER_DEPOSIT, insertMoeny);
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				
			} else {
				resultJson.put("code", "999");
				resultJson.put("msg", "交易失败");
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
	 * 冷柜开通记录表插数据
	 * 
	 * @param db
	 * @param memberid
	 *            会员iD
	 * @param paymoney
	 *            转账金额
	 * @throws JDBCException
	 */
	private void freezerOpen(DB db, String memberid, String paymoney)
			throws JDBCException {
		String uuid = UUID.randomUUID().toString();

		String insertFreezerOpenSql = "INSERT INTO mall_freezer_open_record( id, member_id, transfer_amount, transfer_time) "
				+ "values ('"
				+ uuid
				+ "','"
				+ memberid
				+ "','"
				+ paymoney
				+ "',now()) ";
		db.execute(insertFreezerOpenSql);

	}

}
