package com.ambdp.agricultureProject.action;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.pay.PayManager;
import com.ambdp.agricultureProject.server.BuyProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月20日
 *@version
 *说明：投资农业项目保存Action
 */
public class BuyStockRecordSaveAction implements Action{
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		DB db = DBFactory.getDB();
		//1,检查剩余股数
		//2,检查账号余额
		//3,转账
		//4,处理业务（处理业务在转账的回调方法中实现。此Action不会处理的）
		
		//总股数
		String totalStocks = "0";
		//每股金额
		String stockPrice="0";
		//剩余股数
		int shengYu = 0;
		//最小购买股数
		int minBuyNumber = 0;
		
		String code = SystemAccountClass.GROUP_CASH_ACCOUNT;//"GROUP_CASH_ACCOUNT";
		String stockCode =SystemAccountClass.GROUP_IDENTITY_STOCK_ACCOUNT;//"GROUP_IDENTITY_STOCK_ACCOUNT";
		
		// 项目ID
		String projectId = ac.getRequestParameter("mall_buy_stock_record.form.project_id");
		String buyerId = ac.getRequestParameter("mall_buy_stock_record.form.buyer_id");
		
		buyerId=ac.getVisitor().getUser().getOrgId();
		
		// 购买份额
		int buyNumber = Integer.parseInt(ac.getRequestParameter("mall_buy_stock_record.form.buy_number"));
		
		String projectsSql = "SELECT total_stocks,(total_stocks-COALESCE(already_buy_number,0))AS shengyu,min_buy_number,stock_price FROM mall_agriculture_projects WHERE id='"+projectId+"' ";
		MapList projectMap =db.query(projectsSql);
		
		if(!Checker.isEmpty(projectMap)){
			totalStocks = projectMap.getRow(0).get("total_stocks");
			shengYu = Integer.parseInt(projectMap.getRow(0).get("shengyu"));
			minBuyNumber = Integer.parseInt(projectMap.getRow(0).get("min_buy_number"));
			stockPrice = projectMap.getRow(0).get("stock_price");
		}
		
		//判断购买份数是否小于最小购买份数
		if(buyNumber<minBuyNumber){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("购买份数不能小于最小购买股数");
			return ac;
		}
		
		//判断购买份数是否大于剩余股份
		if(buyNumber>shengYu){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("购买份数大于剩余股数");
			return ac;
		}
		
		BuyProjectStockServer service = new BuyProjectStockServer();
		RuralMarketServer marketServer = new RuralMarketServer();
		
		Long balance = 0L;
		Long sumPrice = 0L;
		String stringSumprice = "";
		//判读余额是否够购买股份
		// 查询现金
		String balanceSql = " SELECT balance,(" + stockPrice + "*" + buyNumber + ") AS sumprice,(" + stockPrice
				+ "*" + buyNumber + ")/100 AS stringSumprice " + " FROM mall_account_info WHERE member_orgid_id='"
				+ buyerId + "' "
				+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"
				+ SystemAccountClass.GROUP_CASH_ACCOUNT + "') ";
		
		MapList balanceMap = db.query(balanceSql);

		if (!Checker.isEmpty(balanceMap)) {

			balance = Long.parseLong(balanceMap.getRow(0).get("balance"));
			sumPrice = (Long.parseLong(balanceMap.getRow(0).get("sumprice"))/100);
			stringSumprice = balanceMap.getRow(0).get("stringsumprice");
		}

		if (balance < sumPrice||balance<=0) {
			//余额不住
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("现金帐号余额不足！");
			return ac;
		} else {
			//转账
			String pay_id=UUID.randomUUID().toString();
			JSONObject business=new JSONObject();
			//{"payment_id":"196E06A7-F969-4B70-9BF7-CB68004581AA",
			//"projectid":"150c2f7c-34e5-420b-8cc8-e278d937fe1e",
			//"memberid":"d69ceff9-086b-4d13-b05b-d8cc84731bdf",
			//"number":"1",
			//"stockprice":"10000",
			//"orders":"196E06A7-F969-4B70-9BF7-CB68004581AA",
			//"success_call_back":"com.am.app_plugins.cooperative.agriculture_project.AgricultureProjectBusinessCallBack"}
			String uuid = UUID.randomUUID().toString();
//			// 给社员/机构项目投资购买股份记录表新增购买记录
			
			if (!Checker.isEmpty(projectId)) {
				String sqlinsert = "INSERT INTO mall_buy_stock_record( id, project_id, buyer_id, buyer_type, buy_number, stock_price, create_time,status) "
						+ "values ('" + uuid + "','" + projectId + "','" + buyerId + "','1','" + buyNumber + "','"
						+ stockPrice + "',now(),'1') ";
				db.execute(sqlinsert);
			}
//			// 更新农业项目表中已经认购份数
			service.updateProject(projectId, buyNumber, db);
			
			business.put("is_group",true);
			business.put("payment_id", pay_id);
			business.put("projectid", projectId);
			business.put("membername", "");
			business.put("pname", "");
			business.put("totalmoney", sumPrice);
			business.put("memberid", buyerId);
			business.put("number", buyNumber);
			business.put("stockprice", stockPrice);
			business.put("recodeId", uuid);
			business.put("orders",UUID.randomUUID().toString());
			business.put("success_call_back", "com.am.app_plugins.cooperative.agriculture_project.AgricultureProjectBusinessCallBack");
			
			logger.info("机构账号："+buyerId+"投资农业项目："+projectId+" 投资"+buyNumber+"份，总价为："+sumPrice+"元");
			logger.info("支付参数："+business.toString());
			
			String outremakes="投资农业项目";
			
			//系统账户 支付
			PayManager payManager = new PayManager();
			
			JSONObject resultJson = payManager.excunte(buyerId,SystemAccountClass.GROUP_CASH_ACCOUNT, sumPrice+"", pay_id,business.toString(), outremakes);
			
			if("0".equals(resultJson.getString("code"))){
//				String uuid = UUID.randomUUID().toString();
//				// 扣除购买用户的现金账号余额 增加至身份股金账号的累计金额
//				service.updateAccountInfo(code, stockCode, stockPrice, buyNumber, buyerId, db);
			}
			
			
			
			
			
//			TransactionDetail trdl=new TransactionDetail();
//			AfterDetailBean bdb=new AfterDetailBean();
//			//记录交易记录
//			marketServer.transactionrecord(db,buyerId,sumPrice+"", SystemAccountClass.GROUP_CASH_ACCOUNT, iremakers, trdl, bdb);
		}

		// 更新交易记录为业务已处理状态
//		String updatePay = "UPDATE mall_trade_detail SET is_process_buissnes=1 WHERE id=? ";
//		db.execute(updatePay, payId, Type.VARCHAR);
		
		
//		//获取 MALL_BUY_STOCK_RECORD 组件 ，记录投资记录
//		Table table=new Table("am_bdp","MALL_BUY_STOCK_RECORD");
//		TableRow insertTr=table.addInsertRow();
//		insertTr.setValue("project_id", projectId);
//		insertTr.setValue("buyer_id", buyerId);
//		insertTr.setValue("buyer_type", 2);
//		insertTr.setValue("buy_number", buyNumber);
//		db.save(table);
//		
//		// id
//		String id =insertTr.getValue("id");
//		//更新农业项目表中已经认购份数
//		service.updateProject(projectId,buyNumber,db);
//		//更新社员/机构项目投资购买股份记录表中单股价格和状态
//		service.updateBuyStock(id,stockPrice,db);
//		//扣除购买用户的现金账号余额  增加至身份股金账号的累计金额
//		service.updateAccountInfo(code,stockCode,stockPrice,buyNumber,buyerId,db);
		if(db!=null)
		{
			db.close();
		}
		return ac;
	}

}
