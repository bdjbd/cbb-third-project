package com.am.app_plugins.cooperative.agriculture_project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.ambdp.agricultureProject.server.WithdrawProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月13日
 *@version
 *说明：确认退股
 */
public class withdrawShareIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		
		try {
			 db = DBFactory.newDB();
		
			String returnAmount="0";
			String code = "IDENTITY_STOCK_ACCOUNT";
			String outmakers ="农业项目退股";
			String shareMoney="0";
			TransactionDetail trdl = new TransactionDetail();
			AfterDetailBean bdb = new AfterDetailBean();
			//购买记录id
			String buyId = request.getParameter("buyId");
			//购买份额
			String buyNumber = request.getParameter("buyNumber");
			//购买人
			String memberId = request.getParameter("memberid");
			//购买项目id
			String projectId = request.getParameter("projectId");
			//购买金额
			String sumPrice = request.getParameter("sumPrice");
			//违约金比例
			String penaltyRatio = request.getParameter("penaltyRatio");
			//过期类型
			String difference = request.getParameter("difference");
			//判断过没过期限 false是没过
			if("false".equals(difference)){
				//购买金额-购买金额*违约金比例
				String returnAmountSql = "SELECT "+sumPrice+"-"+sumPrice+"*"+penaltyRatio+"/100 AS returnamount";
				MapList returnAmountMap = db.query(returnAmountSql);
				if(!Checker.isEmpty(returnAmountMap)){
					returnAmount = returnAmountMap.getRow(0).get("returnamount");
				}
			}else{
				returnAmount=sumPrice;
			}
			WithdrawProjectStockServer service=new WithdrawProjectStockServer();
			//更新农业项目表中已经认购份数 (减去退去股份)
			service.updateProject(projectId,buyNumber,db);
			//更新社员/机构项目投资购买股份记录表中状态 (状态改为3--退股)
			service.updateStockRecord(buyId,db);
			//增加购买用户的身份股金账号余额
			service.updateAccountInfo(code,memberId,returnAmount,db);
			//将退股金额（分）转换成元
			String selMoneySql = "SELECT "+returnAmount+"/100 AS sharemoney";
			MapList selMoneyMap = db.query(selMoneySql);
			if(!Checker.isEmpty(selMoneyMap)){
				shareMoney = selMoneyMap.getRow(0).get("sharemoney");
			}
			//给交易记录表中插一条数据
			service.transactionrecord(db,memberId,shareMoney,code,outmakers,trdl,bdb);
		} catch (JDBCException e) {
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
		
		return null;
	}

}
