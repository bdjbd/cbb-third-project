package com;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.VariableAcquisition;
import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

public class TestWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
	
DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			TransactionDetail trdl = new TransactionDetail();
			AfterDetailBean bdb = new AfterDetailBean();
			
			//获取分红金额满足
			String AccountTypeName = "GROUP_BONUS_ACCOUNT";
			//账号类型
			String AccountType = BonusJobclassImpl.getInstance().returnAccountTypeId(AccountTypeName,db);
			//获取平台分红比例
			double  dividendpayoutratio = Double.parseDouble(VariableAcquisition.getInstance().returnstr("Platformdividendratio", db));
			//消费总金额
			double consumptionMoney = BonusJobclassImpl.getInstance().QuerySalesvolume(db)*100;
			//分红总金额
			long Amountofdividends = new Double(consumptionMoney*dividendpayoutratio).longValue();
			//总分红权数 
			int Totalredweight=BonusJobclassImpl.getInstance().SumDividendRight_Bussiness(AccountTypeName,db);
			//每个分红权分红金额
			double averagemoney = BonusJobclassImpl.getInstance().CalculationAveragedividend(Amountofdividends, Totalredweight);
			
			if(averagemoney>=1)
			{
				
				String SQL="SELECT * FROM mall_account_info AS mci LEFT JOIN mall_system_account_class AS msac ON msac.id = mci.a_class_id"
						+ "WHERE msac.sa_code='GROUP_BONUS_ACCOUNT' AND mci.enabled_dividend_sharing>0";
				
				MapList maplist = db.query(SQL);
				
				for (int i = 0; i < maplist.size(); i++) {
					
					long money = BonusJobclassImpl.getInstance().Platformbonus_Business(maplist.getRow(i).get("member_orgid_id"),AccountTypeName,db);
					BonusJobclassImpl.getInstance().transactionrecord_business(maplist.getRow(i).get("member_orgid_id"), trdl, bdb, money,AccountTypeName, db);
					BonusJobclassImpl.getInstance().UpdateMemberbonus_Business(maplist.getRow(i).get("member_orgid_id"),AccountTypeName, db);
				}
			}
			
		} catch (JDBCException e1) {
			
			e1.printStackTrace();
		}finally
		{
			try {
				if(db!=null)
				{
					db.close();
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	
	}
}
