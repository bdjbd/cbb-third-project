package com.am.more.kopp_train;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;

/**
 * @author wangxi
 * @create 2016年5月18日
 * @version 说明：确定报名WebApi
 */
public class ConfirmSignUpBusinessCallBack implements IBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type)
			throws Exception {

		JSONObject resultJson = null;
		
		try {
			db = DBFactory.newDB();
			// 支付ID
			String payId = id;

			JSONObject businessJob = new JSONObject(business);

			// 内容id
			String pcid = businessJob.getString("pcid");
			// 会员id
			String memberid = businessJob.getString("memberid");
			// 人数
			String count = businessJob.getString("count");

			// 根据订单id查询交易是否成功未处理
			String selPaySql = "SELECT * FROM mall_trade_detail WHERE id='"
					+ payId
					+ "' AND trade_state='1' AND is_process_buissnes='0'";

			resultJson = new JSONObject();
			MapList selPayMap = db.query(selPaySql);
			if (!Checker.isEmpty(selPayMap)) {
				UpdatePlayersServer server = new UpdatePlayersServer();
				// 给报名表中添加数据
				server.updatePlayer(db, memberid, pcid, count);
				// 修改交易表的状态
				server.updateTransaction(db, payId);
			} else {
				resultJson.put("code", "999");
				resultJson.put("msg", "交易已处理");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultJson.toString();

	}

}
