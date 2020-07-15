package com.am.frame.pay;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.cro.entity.CarRepairOrder;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.member.MemberPayment;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.util.DBUtil;

/**
 * 支付管理类
 * 
 * @author Administrator
 *
 */
public class PayManager {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// private static PayManager payManager;
	// private PayManager(){}

	//
	// public static PayManager getInstance(){
	// if(payManager==null){
	// payManager=new PayManager();
	// }
	// return payManager;
	// }

	/**
	 * 保存会员支付数据到数据库
	 * 
	 * @param mebPay
	 *            MemberPayment
	 * @return true 保存成功，false 保存失败
	 * 
	 */
	public boolean createMemberPayment(MemberPayment memberPay) {

		boolean result = false;
		DB db = null;
		try {
			db = DBFactory.newDB();

			String sql = "INSERT INTO mall_memberpayment(                                               "
					+ "            id, paycode, paydatetime, paymoney, alipayordercode, paycontent, "
					+ "            paysource,memberid)                                                       "
					+ "    VALUES (uuid_generate_v4(), ?, now(), ?, ?, ?,"
					+ "            ?,?)";

			int res = db.execute(
					sql,
					new String[] { memberPay.getPayCode(),
							String.valueOf(memberPay.getPayMoney()),
							memberPay.getAlipayOrderCode(),
							memberPay.getPayContent(),
							String.valueOf(memberPay.getPaySource()),
							memberPay.getMemberID() },

					new int[] { Type.VARCHAR, Type.DECIMAL, Type.VARCHAR,
							Type.VARCHAR, Type.INTEGER, Type.VARCHAR });

			if (res > 0) {
				result = true;
			}

		} catch (JDBCException e) {
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

		return result;
	}

	/**
	 * 获取我的支付信息JSON格式数据
	 * 
	 * @param memberCode
	 * @return JSON格式数据 {"DATA":[{item},{item}]}
	 */
	public String getMyPaymentToJSON(String memberCode) {
		String result = "{\"DATA\":[]}";
		DB db = null;
		try {
			db = DBFactory.newDB();

			String sql = "SELECT * FROM mall_MemberPayment  WHERE memberid=?";

			ResultSet rst = db.getResultSet(sql, new String[] { memberCode },
					new int[] { Type.VARCHAR });

			result = DBUtil.resultSetToJSON(rst).toString();

		} catch (Exception e) {
			e.printStackTrace();
			result = "{'errcode':40007,'errmsg':'" + e.getMessage() + "'}";
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 现金支付
	 * 
	 * @param memberCoder
	 *            会员编号
	 * @param payMoney
	 *            支付金额
	 * @return
	 */
	public boolean cashPay(String memberCode, Double payMoney) {

		boolean result = false;
		DB db = null;
		try {
			// 检查现金是否足够

			db = DBFactory.newDB();

			String checkSQL = "SELECT cash FROM am_member  WHERE id='"
					+ memberCode + "' AND cash>=" + payMoney;

			MapList map = db.query(checkSQL);

			if (!Checker.isEmpty(map)) {
				// 足够扣除现金
				String sql = "UPDATE  am_member  SET cash=COALESCE(cash,0)-"
						+ payMoney + " WHERE id='" + memberCode + "'";

				if (db.execute(sql) > 0) {
					result = true;
				}
			}

		} catch (JDBCException e) {
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
		return result;
	}

	/**
	 * 创建我的支付
	 * 
	 * @param orderIds
	 *            支付内容，多个订单编号用逗号分隔
	 * @param memberId
	 *            会员ID
	 * @param paysource
	 *            支付来源 :1,订单 2,充值
	 * @param amOrders
	 *            订单ID。多个订单ID用英文逗号分隔
	 * @return MemberPayment
	 * @throws JDBCException
	 */
	public MemberPayment createPaymentByOrder(String payContent,
			String memberId, String paysource, double paymoney, String amOrders)
			throws JDBCException {

		DB db = DBFactory.newDB();

		// 我的支付，支付ID
		String payMentId = UUID.randomUUID().toString().toUpperCase()
				.replaceAll("-", "0");

		String payCodePrix = "d";

		if ("2".equals(paysource)) {
			payCodePrix = "c";
		}

		String insertSQL = "INSERT INTO mall_memberpayment(                         "
				+ "            id, paycode, paymoney,      "
				+ "            paycontent, paysource,memberid, paydatetime,"
				+ "            iscomplete,amorders)                                 "
				+ "    VALUES (                                            "
				+ "    				?, ?, ?,                                "
				+ "    				?, ?, ?,now(),                                  "
				+ "    				'0',?)                                      ";

		db.execute(insertSQL, new String[] { payMentId,
				payCodePrix + "@" + memberId + "@" + payMentId, paymoney + "",
				payContent, paysource, memberId, amOrders }, new int[] {
				Type.VARCHAR, Type.VARCHAR, Type.DECIMAL, Type.VARCHAR,
				Type.INTEGER, Type.VARCHAR, Type.VARCHAR });

		MemberPayment memberPaymen = getMyPaymentById(payMentId, db);

		if (db != null) {
			db.close();
		}

		return memberPaymen;
	}

	/**
	 * 获取我的支付信息JSON格式数据
	 * 
	 * @param payMentId
	 *            支付ID
	 * @return
	 */
	public MemberPayment getMyPaymentById(String payMentId) {
		MemberPayment result = null;
		DB db = null;
		try {
			db = DBFactory.newDB();
			result = getMyPaymentById(payMentId, db);
		} catch (Exception e) {
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
		return result;
	}

	/**
	 * 获取我的支付信息JSON格式数据
	 * 
	 * @param payMentId
	 *            支付ID
	 * @param DB
	 *            db
	 * @return
	 * @throws JDBCException
	 */
	public MemberPayment getMyPaymentById(String payMentId, DB db)
			throws JDBCException {

		MemberPayment result = null;

		String findSQL = "SELECT * FROM mall_MemberPayment  WHERE id=?";

		MapList map = db.query(findSQL, payMentId, Type.VARCHAR);

		result = new MemberPayment(map);

		return result;
	}

	/**
	 * 处理支付完成功能
	 * 
	 * @param paycode
	 *            支付编码 '订单支付，在订单号前面加字符“d” ,格式如 d@membercode@支付ID;
	 *            充值支付，在充值号前面加字符"c",格式如 c@membercode@UUID';
	 * @throws JDBCException
	 */
	public MemberPayment processPaymentComplete(Map<String, String> params)
			throws Exception {

		DB db = DBFactory.newDB();

		// 商户订单号
		String outTradeNo = params.get("out_trade_no");
		// 支付宝订单号
		String tradeNo = params.get("trade_no");
		// 支付金额
		String payMoney = params.get("total_fee");

		// 更新支付成功状态
		String updateSQL = "UPDATE mall_memberpayment SET  "
				+ " isComplete='1',alipayordercode=?,paymoney=? WHERE id=?";

		db.execute(updateSQL, new String[] { tradeNo, payMoney, outTradeNo },
				new int[] { Type.VARCHAR, Type.DECIMAL, Type.VARCHAR });

		// 获取支付
		MemberPayment payment = getMyPaymentById(outTradeNo, db);

		// 更新订单状态
		String[] orders = payment.getAmOrders().split(",");

		for (int i = 0; i < orders.length; i++) {
			updateOrdersState(orders[i], db);
		}

		if (db != null) {
			db.close();
		}

		return payment;
	}

	/**
	 * 通过订单ID更新订单状态
	 * 
	 * @param orderId
	 *            订单ID
	 * @param db
	 *            DB
	 * @throws Exception
	 */
	private void updateOrdersState(String orderId, DB db) throws Exception {
		/** 一，获取当前流程状态 **/
		logger.info("updateOrdersState  orderId:" + orderId);
		// 订单信息
		MemberOrder order = new OrderManager().getMemberOrderById(orderId, db);

		// 订单对应的商品信息
		Commodity commodity = CommodityManager.getInstance().getCommodityByID(
				order.getCommodityID());

		/** 2,执行当前流程状态配置动作，跳转流程 **/

		Map<String, String> otherParam = new HashMap<String, String>();

		// 执行订单完成Action
		otherParam.put(OrderFlowParam.STATE_ACTON_CODE, "102");

		/** 2,执行当前流程状态配置动作，跳转流程 **/
		StateFlowManager.getInstance().setNextState(
				commodity.getOrderStateID(), "1", order.getId(), otherParam);

		order = new OrderManager().getMemberOrderById(orderId, db);

		// 执行派单 如果订单配单模式为预约送货的，则不立即派单
		if ("3".equals(order.getOrderState())
				&& "1".equals(order.getShippingMethod())) {
			// 需要检查订单的类型是否需要派单
			otherParam.remove(OrderFlowParam.STATE_ACTON_CODE);
			StateFlowManager.getInstance()
					.setNextState(commodity.getOrderStateID(), "3",
							order.getId(), otherParam);
		}

		logger.info("OrderFlowParam.STATE_ACTON_CODE :"
				+ otherParam.get(OrderFlowParam.STATE_ACTON_CODE));

	}

	/**
	 * 现金支付
	 * 
	 * @param memberCoder
	 *            会员编号
	 * @param payMoney
	 *            支付金额
	 * @param payId
	 *            我的支付，支付ID
	 * @return
	 */
	public String cashPay(String memberCode, Double payMoney, String payId,
			String accountId) {

		String result = "{}";
		JSONObject msgJSON = new JSONObject();
		DB db = null;
		try {
			// 检查现金是否足够

			db = DBFactory.newDB();

			// String
			// checkSQL="SELECT cash FROM am_member  WHERE id='"+memberCode+"' AND cash>="+payMoney;
			String checkSQL = "SELECT balance FROM mall_account_info  WHERE id='"
					+ accountId + "' AND balance>=" + payMoney;

			MapList map = db.query(checkSQL);

			if (!Checker.isEmpty(map)) {
				// 足够扣除现金
				// String
				// sql="UPDATE  am_member  SET cash=COALESCE(cash,0)-"+payMoney
				// +" WHERE id='"+memberCode+"'";
				String sql = "UPDATE  mall_account_info  SET balance=COALESCE(balance,0)-"
						+ payMoney + " WHERE id='" + accountId + "'";

				if (db.execute(sql) > 0) {
					// 执行插入交易记录
					String isql = "insert into mall_trade_detail (id,member_id,account_id,trade_time,trade_total_money,counter_fee,rmarks,create_time)"
							+ " values('"
							+ UUID.randomUUID()
							+ "',"
							+ "'"
							+ memberCode
							+ "'"
							+ ",'"
							+ accountId
							+ "'"
							+ ",'now()','-"
							+ (long) Math.floor(payMoney)
							+ "'"
							+ ",'0'" + ",'账户支付操作'" + ",'now()')";
					db.execute(isql);

					result = "{'CODE':'0','MSG':'支付成功','SUCCESS':true,'ERRCODE':'0'}";
				}

				// 根据支付号获取订单号
				String getOrderSQL = "SELECT id,paycode,amorders,iscomplete FROM mall_memberpayment WHERE id='"
						+ payId + "' ";

				MapList ordersMap = db.query(getOrderSQL);

				String stateActonCode = "102";
				MemberOrder order = null;
				Commodity commodity = null;

				// 检查订单是否已经支付
				if (!Checker.isEmpty(ordersMap)
						&& "0".equals(ordersMap.getRow(0).get("iscomplete"))) {

					String[] orders = ordersMap.getRow(0).get("amorders")
							.split(",");
					String stateValue = "1";

					for (int i = 0; i < orders.length; i++) {
						/** 一，获取当前流程状态 **/
						// 订单信息
						order = new OrderManager().getMemberOrderById(
								orders[i], db);

						// 订单对应的商品信息
						commodity = CommodityManager.getInstance()
								.getCommodityByID(order.getCommodityID());

						Map<String, String> otherParam = new HashMap<String, String>();

						if (!Checker.isEmpty(stateActonCode)) {
							otherParam.put(OrderFlowParam.STATE_ACTON_CODE,
									stateActonCode);
						}
						/** 2,执行当前流程状态配置动作，跳转流程 **/
						result = StateFlowManager.getInstance().setNextState(
								commodity.getOrderStateID(), stateValue,
								order.getId(), otherParam);
						/** 3,更新订单支付时间 ***/
						updateOrderPayTime(order, db);

						try {
							JSONObject resultObj = new JSONObject(result);

							if (resultObj.getBoolean("SUCCESS")) {
								// 支付成功，更新支付状态 iscomplete 0未完成，1完成
								String updateSQL = "UPDATE mall_memberpayment SET iscomplete='1' WHERE id='"
										+ payId + "' ";
								db.execute(updateSQL);
							}

							resultObj.put("MSG", "支付成功");
							result = resultObj.toString();

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				} else {
					throw new Exception("支付不合法，请检查你的订单是否取消或者已支付");
				}

			} else {
				// result="{'CODE':'1','MSG':'余额不足','SUCCESS':false,'ERRCODE':'1'}";
				msgJSON.put("CODE", 1);
				msgJSON.put("MSG", "余额不足");
				msgJSON.put("SUCCESS", false);
				msgJSON.put("ERRCODE", 1);
				result = msgJSON.toString();

			}

		} catch (Exception e) {
			e.printStackTrace();

			try {
				JSONObject res = new JSONObject();
				res.put("CODE", 0);
				res.put("ERRCODE", 1);
				res.put("MSG", e.getMessage());
				res.put("SUCCESS", false);

				result = res.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 更新订单支付时间
	 * 
	 * @param order
	 *            订单
	 * @param db
	 *            DB
	 * @throws JDBCException
	 */
	private void updateOrderPayTime(MemberOrder order, DB db)
			throws JDBCException {
		String updateSQL = "UPDATE mall_memberorder SET paymentdate=now() WHERE id='"
				+ order.getId() + "'";
		db.execute(updateSQL);
	}

	/**
	 * 订单处理业务
	 * 
	 * @param payId
	 *            支付ID
	 * @throws JDBCException
	 * @params DB db
	 */
	public JSONObject processPaymentComplete(DB db, String payId)
			throws Exception {

		JSONObject result = new JSONObject();

		String queryPayInfoSQL = "SELECT * FROM mall_trade_detail WHERE id=? ";

		MapList payMap = db.query(queryPayInfoSQL, payId, Type.VARCHAR);

		if (!Checker.isEmpty(payMap)) {
			Row row = payMap.getRow(0);
			String is_process_buissnes = row.get("is_process_buissnes");
			String bs = row.get("business_json");

			if ("0".equals(is_process_buissnes)) {
				JSONObject businessJson = new JSONObject(bs);
				if (bs.contains("orders")) {
					String[] orders = businessJson.getString("orders").split(
							",");
					for (int i = 0; i < orders.length; i++) {
						updateOrdersState(orders[i], db);
					}
				}
				// 更新交易记录业务处理状态
				new UpdatePlayersServer().updateTransaction(db, payId);

				result.put("ERROR", 0);
				result.put("MSG", "下单成功，正在处理中.");
			} else {
				result.put("ERROR", 1);
				result.put("MSG", "支付已经处理。");
			}
		}

		return result;
	}

	/**
	 * 根据交易记录id获取业务参数，从业务参数中得到订单id，进而查出该笔交易的订单详情
	 * 
	 * @param payid
	 * @param db
	 * @return
	 * @throws JDBCException
	 * @throws JSONException
	 */
	public CarRepairOrder getOrder(String payid, DB db) throws JDBCException,
			JSONException {
		//查询交易记录信息
		String queryOrderIdSQL = "SELECT * FROM mall_trade_detail WHERE id= '"
				+ payid + "'";

		MapList mapList = db.query(queryOrderIdSQL);
		String json = mapList.getRow(0).get("business_json");
		//把业务字符串先转成JSON对象，再从JSON对象中取出订单id，最后把订单id转成字符串
		JSONObject jObject = new JSONObject(json);
		//获得订单ID
		String orderid = String.valueOf(jObject.get("orders"));
		String querySql = "SELECT * FROM cro_carrepairorder where id='"
				+ orderid + "'";
		//查出这笔交易的订单详情
		MapList mapList2 = db.query(querySql);
		//mapList2.getRow(0) 刚好是一个订单对象
		CarRepairOrder carRepairOrder = new CarRepairOrder(mapList2.getRow(0));

		return carRepairOrder;
	}

	/**
	 * 更新订单支付方式：支付方式（PayMode）为线上支付，支付状态（PayState）为已支付
	 * 
	 * @param order
	 *            订单
	 * @param db
	 *            DB
	 * @throws JDBCException
	 */
	public void updateOrderPayState(String orderId, DB db) throws JDBCException {
		String updateSQL = "UPDATE cro_CarRepairOrder SET PayMode ='1' ,PayState='1' WHERE id='"
				+ orderId + "'";
		db.execute(updateSQL);
	}

	/**
	 * 更新会员积分
	 * 
	 * @param order
	 *            订单
	 * @param db
	 *            DB
	 * @throws JDBCException
	 */
	public void updateMemberScore(String memberId, int money, DB db)
			throws JDBCException {
		int score = 0;
		MapList scoreMapList = db.query("select score from am_member where id='"+memberId+"'");
		if(!Checker.isEmpty(scoreMapList)){
			score = scoreMapList.getRow(0).getInt(0, 0);
		}
		String updateSQL = "UPDATE am_member SET score="+(score+money)
				+ " WHERE id='" + memberId + "'";
		db.execute(updateSQL);
	}

	/**
	 * 新增会员积分记录
	 * 
	 * @param memberId
	 * @param money
	 * @param explain
	 *            说明
	 * @param db
	 * @throws JDBCException
	 */
	public void addMemberScore(String memberId, int money, String explain, DB db)
			throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String addSQL = "INSERT INTO am_MemberIntegralRecord VALUES('" + uuid
				+ "','" + memberId + "'," + money + ",'" + explain + "','"
				+ format.format(new Date()) + "')";
		db.execute(addSQL);
	}

	/**
	 * 判断支付账户是否是现金账户
	 *  入参：支付ID    出参：账户类型
	 * @param payid
	 * @param db
	 * @return
	 * @throws JDBCException
	 */
	public String getSaCode(String payid, DB db) throws JDBCException {
		String sql = "select msac.sa_code from mall_trade_detail mtd,mall_system_account_class msac where mtd.sa_class_id = msac.id and mtd.id ='"
				+ payid + "'";
		MapList mapList = db.query(sql);
		String sa_code = mapList.getRow(0).get(0);
		return sa_code;
	}

	/**
	 * 根据充值pay_id获取订单信息
	 * 
	 * @param pay_id
	 * @param db
	 * @return
	 * @throws JDBCException
	 * @throws JSONException
	 */
	public CarRepairOrder getRachange(String pay_id, DB db)
			throws JDBCException, JSONException {

		String queryOrderIdSQL = "SELECT * FROM mall_trade_detail WHERE id= '"
				+ pay_id + "'";
		MapList mapList = db.query(queryOrderIdSQL);
		String json = mapList.getRow(0).get("business_json");
		JSONObject jObject = new JSONObject(json);
		String paymoney = String.valueOf(jObject.get("paymoney"));
		String querySql = "update  mall_account_info set balance=((select t.balance from  (select  row_number() OVER (order by balance desc) as No , M.* from (select  balance from mall_account_info where member_orgid_id='1') M)  t where t.No = '1')+'"
				+ paymoney
				+ "') where balance=(select t.balance from  (select  row_number() OVER (order by balance desc) as No , M.* from (select  balance from mall_account_info where member_orgid_id='1') M)  t where t.No = '1') ";
		MapList mapList2 = db.query(querySql);
		CarRepairOrder carRepairOrder = new CarRepairOrder(mapList2.getRow(0));

		return carRepairOrder;
	}

	/**
	 * 根据会员id查询会员积分,会员等级，所属机构， 再根据所属机构查询相应的会员等级信息，
	 * 如果会员积分大于升级所需消费额度，则升级会员等级，如果等级已是最大，则不进行操作
	 * 
	 * @param memberId
	 * @param db
	 * @throws JDBCException
	 */
	public void updateMemberLevel(String memberId, DB db) throws JDBCException {
		// 首先从会员等级表中，查询会员等级、机构和积分（因为每个机构都有各自的会员等级标准）
		String queryMemberInfo = "select member_level_id,orgcode,score from am_member where id = '"
				+ memberId + "'";
		MapList memberInfo = db.query(queryMemberInfo);
		if (!Checker.isEmpty(memberInfo)) {
			// 会员积分
			int score = memberInfo.getRow(0).getInt("score", 0);
			// 会员等级ID
			String member_level_id = memberInfo.getRow(0)
					.get("member_level_id");
			// 机构ID
			String orgcode = memberInfo.getRow(0).get("orgcode");

			// 查询当前机构所允许的会员最大等级
			String maxLevel = "select max(levelCode) from cro_MemberLevel where orgcode = '"
					+ orgcode + "'";
			int maxLevelCode = 0;
			if(!Checker.isEmpty(db.query(maxLevel))){
				maxLevelCode = db.query(maxLevel).getRow(0).getInt(0, 0);
			}
			

			// 当前会员等级
			String memberLevel = "select levelCode from cro_MemberLevel where id = '"
					+ member_level_id + "'";
			int memberLevelCode = 0;
			if(!Checker.isEmpty(db.query(memberLevel))){
				memberLevelCode = db.query(memberLevel).getRow(0).getInt(0, 0);
			}
			
			// 如果当前会员等级不是最大等级（则应首先查出当前会员等级的下一等级、所需总积分，再判断是否允许升级）
			if (memberLevelCode < maxLevelCode) {
				// 查询当前机构的大于当前会员等级的会员等级的ID和升级所需额度
				String queryQuota = "select id,quota from cro_MemberLevel where orgcode = '"
						+ orgcode
						+ "' and levelCode >"
						+ memberLevelCode
						+ "";
				//升级所需总积分
				int quota = 0;
				MapList queryLevel = db.query(queryQuota);
				if(!Checker.isEmpty(queryLevel)){
					for (int i = 0; i < queryLevel.size(); i++) {
						quota =new Double(queryLevel.getRow(i).getDouble("quota", 0)).intValue();
						// 如果当前会员的积分(已更新)达到升级所需额度，则升级会员等级
						// 循环操作，若不考虑升级顺序和升级次数，则可以升级
						if (score >= quota) {
							String updateMemberLevel = "update am_member set member_level_id = '"
									+ queryLevel.getRow(i).get("id")
									+ "' where id = '" + memberId + "'";
							db.execute(updateMemberLevel);
						}
					}
					
				}
				

			}
		}

	}
}
