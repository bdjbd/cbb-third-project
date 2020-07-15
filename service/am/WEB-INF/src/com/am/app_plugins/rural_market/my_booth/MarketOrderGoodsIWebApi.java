package com.am.app_plugins.rural_market.my_booth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016.06.13
 *@version
 *说明：大市场订货WebApi
 */
public class MarketOrderGoodsIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject resultJson = null;
		DB db=null;
		try {
			//因为DBManager 没有提供db对象
			 db = DBFactory.newDB();
		//货ID
		String meId = request.getParameter("meId");
		//购买社员id
		String buyersId = request.getParameter("buyersId");
		//金额（元）
		String price = request.getParameter("price");

		resultJson = new JSONObject();
		//出账类型现金账户
		String outAccountCode  = SystemAccountClass.CASH_ACCOUNT;
		String iremakers ="购买农村大市场";
		String status = "5";
		String selStatus = "";
		Long balance = 0L;
		Long priceMoney = Long.parseLong(price);
		TransactionDetail trdl = new TransactionDetail();
		AfterDetailBean bdb = new AfterDetailBean();
		String selStatusSql = "SELECT * FROM mall_marketplace_entity WHERE id='"+meId+"'";
		MapList selStatusMap = db.query(selStatusSql);
		if(!Checker.isEmpty(selStatusMap)){
			selStatus = selStatusMap.getRow(0).get("status");
		}
		if(!"3".equals(selStatus)){
			resultJson.put("code", "888");
			resultJson.put("msg", "已被购买！");
		}else{
		String balanceSql = " SELECT balance "
				+ " FROM mall_account_info WHERE member_orgid_id='"+buyersId+"' "
				+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='CASH_ACCOUNT') ";
		
		MapList balanceMap = db.query(balanceSql);
		if(!Checker.isEmpty(balanceMap)){
			balance = Long.parseLong(balanceMap.getRow(0).get("balance"));
		}
			if(balance<priceMoney){
				resultJson.put("code", "999");
				resultJson.put("msg", "现金帐号余额不足！");
			}else{
				RuralMarketServer server = new RuralMarketServer();
				//扣除购买者现金帐号余额
//				server.deductionBalance(db,buyersId,price,outAccountCode);
//				//给交易记录表中插一条数据
//				server.transactionrecord(db,buyersId,price,outAccountCode,iremakers,trdl,bdb);
				//给农村大市场交易记录表中插一条数据
				server.inserRecord(db,meId,buyersId,price);
				//修改大市场信息表状态
				server.updateinfo(db,meId,status);
				resultJson.put("code", "0");
				resultJson.put("msg", "订货成功！");
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return resultJson.toString();
	}


}
