package com.am.app_plugins.cooperative.agriculture_project;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.ambdp.agricultureProject.server.BuyProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author wangxi
 * @create 2016年5月12日
 * @version 说明：购买农业投资项目
 */
public class PurchaseSharesIWebApi implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {

		JSONObject resultJson = new JSONObject();
		
		BuyProjectStockServer service = new BuyProjectStockServer();
		RuralMarketServer marketServer = new RuralMarketServer();
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			Long balance = 0L;
			Long sumPrice = 0L;
			String stringSumprice = "";
			String code = SystemAccountClass.CASH_ACCOUNT;// "CASH_ACCOUNT";
			String iremakers = "创办农业项目";
			//
			String stockCode =SystemAccountClass.IDENTITY_STOCK_ACCOUNT;
			
			//支付ID 根据支付id判断是否已经支付，如果已经支付则，不在进行支付处理
			String payId=request.getParameter("pay_id");
			
			String checkSQL="SELECT id,user_submit_trade_money,is_process_buissnes "
					+ " FROM mall_trade_detail WHERE id=? ";
			
			MapList checkMap=db.query(checkSQL,payId,Type.VARCHAR);
			
			if(!Checker.isEmpty(checkMap)){
				////业务是否处理0：未处理 ;1：处理
				String is_process_buissnes=checkMap.getRow(0).get("is_process_buissnes");
				if("0".equals(is_process_buissnes)){
					//业务为处理，进行业务处理
					// 购买股数
					int number = Integer.parseInt(request.getParameter("number"));
					// 购买项目ID
					String projectId = request.getParameter("projectid");
					// 购买会员ID
					String memberid = request.getParameter("memberid");
					// 单股价格
					String stockPrice = request.getParameter("stockprice");
					
					TransactionDetail trdl = new TransactionDetail();
					AfterDetailBean bdb = new AfterDetailBean();
					
					// 查询现金
					String balanceSql = " SELECT balance,(" + stockPrice + "*" + number + ") AS sumprice,(" + stockPrice + "*"
							+ number + ")/100 AS stringSumprice " + " FROM mall_account_info WHERE member_orgid_id='" + memberid
							+ "' "
							+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+SystemAccountClass.CASH_ACCOUNT+"') ";
					MapList balanceMap = db.query(balanceSql);
					
					if (!Checker.isEmpty(balanceMap)) {

						balance = Long.parseLong(balanceMap.getRow(0).get("balance"));
						sumPrice = Long.parseLong(balanceMap.getRow(0).get("sumprice"));
						stringSumprice = balanceMap.getRow(0).get("stringsumprice");
					}
					
					if (balance < sumPrice) {
						resultJson.put("code", "999");
						resultJson.put("msg", "现金帐号余额不足！");
					} else {
						resultJson.put("code", "0");
						resultJson.put("msg", "投资成功！");
						
						String uuid = UUID.randomUUID().toString();
						// 给社员/机构项目投资购买股份记录表新增购买记录
						insert(db, uuid, projectId, memberid, number, stockPrice);
						// 更新农业项目表中已经认购份数
						service.updateProject(projectId, number, db);
						
						//扣除购买用户的现金账号余额 增加至身份股金账号的累计金额
						service.updateAccountInfo(code, stockCode, stockPrice, number, memberid, db);
						
						// 给交易记录表中插一条数据
//						 marketServer.transactionrecord(db,memberid,stringSumprice,code,iremakers,trdl,bdb);

						// 理性农业 会员权限徽章任务，具体权限参考需求 社员分类划分
						
						// 任务在WebAPI中触发的主要原因是后台购买无任务功能。 单位 分
						float insertMoeny = Float.parseFloat(stockPrice)*(number);

						// 会员权限徽章任务 START
						TaskEngine taskEngine = TaskEngine.getInstance();
						RunTaskParams params = new RunTaskParams();
						params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); // 会员权限徽章任务

						params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT, MemberAuthorityBadgeTask.INVST_PROJECT_MEONY);
						params.pushParam(MemberAuthorityBadgeTask.INVST_PROJECT_MEONY, insertMoeny);
						params.setMemberId(memberid);
						taskEngine.executTask(params);
						// 会员权限徽章任务 END

						// 获取志愿者账号提现资格任务 START
						params = new RunTaskParams();
						params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
						params.pushParam(GetVolunteerAccountWithQualificationItask.INTVISM_AGRICULT_PROJ_MONEY, insertMoeny);

						params.setMemberId(memberid);
						taskEngine.executTask(params);
						// 获取志愿者账号提现资格任务 END
					}
					
					//更新交易记录为业务已处理状态
					String updatePay="UPDATE mall_trade_detail SET is_process_buissnes=1 WHERE id=? ";
					db.execute(updatePay, payId,Type.VARCHAR);

				}else{
					logger.info("业务重复提交处理拒绝。pay_id："+payId);
					
					resultJson.put("code","0");
					resultJson.put("msg","业务已经处理");
				}
			}
			
			
			
		} catch (JDBCException e1) {
			e1.printStackTrace();
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
	 * 给社员/机构项目投资购买股份记录表新增购买记录
	 * 
	 * @param db
	 * @param uuid
	 *            uuid
	 * @param projectId
	 *            农业项目id
	 * @param memberid
	 *            购买人
	 * @param number
	 *            购买数
	 * @param stockPrice
	 *            单股价格
	 * @throws JDBCException
	 */
	private void insert(DB db, String uuid, String projectId, String memberid, int number, String stockPrice)
			throws JDBCException {
		if (!Checker.isEmpty(projectId)) {
			String sqlinsert = "INSERT INTO mall_buy_stock_record( id, project_id, buyer_id, buyer_type, buy_number, stock_price, create_time,status) "
					+ "values ('" + uuid + "','" + projectId + "','" + memberid + "','1','" + number + "','"
					+ stockPrice + "',now(),'2') ";
			db.execute(sqlinsert);
		}
	}

}