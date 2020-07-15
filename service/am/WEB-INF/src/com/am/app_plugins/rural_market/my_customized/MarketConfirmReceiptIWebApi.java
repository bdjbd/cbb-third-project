package com.am.app_plugins.rural_market.my_customized;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016.06.13
 *@version
 *说明：大市场确认收货WebApi
 */
public class MarketConfirmReceiptIWebApi implements IWebApiService{

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
		//记录ID
		String mtrId = request.getParameter("mtrId");
		//发布社员id
		String memberId = request.getParameter("memberId");
		//金额（元）
		String price = request.getParameter("price");

		resultJson = new JSONObject();
		//入账类型现金账户
		String inAccountCode  = SystemAccountClass.CASH_ACCOUNT;
		String iremakers ="购买农村大市场确认收货";
		String status = "6";
		String recordStatus = "2";
		VirementManager virementManager = new VirementManager();
		RuralMarketServer server = new RuralMarketServer();
		//给发布者现金帐号增加余额
		resultJson = virementManager.execute(db,"",memberId,"",inAccountCode,price,iremakers,iremakers,"",false);
		//修改农村大市场交易记录表状态
		server.updateRecord(db,mtrId,recordStatus);
		//修改大市场信息表状态
		server.updateinfo(db,meId,status);
		resultJson.put("code", "0");
		resultJson.put("msg", "收货成功！");
		
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
