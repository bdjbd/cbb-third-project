package com.am.frame.bonus.Job;

import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.am.frame.VariableAcquisition;
import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 分红奖励
 * @author xianlin
 *2016年4月15日
 */
public class BonusJob implements Job {

	@Override
	public void execute(JobExecutionContext ac) throws JobExecutionException {
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			VirementManager vir = new VirementManager();
			
			TransactionDetail trdl = new TransactionDetail();
			AfterDetailBean bdb = new AfterDetailBean();
			
			//获取分红金额满足
			String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
			//账号类型
			String AccountType = BonusJobclassImpl.getInstance().returnAccountTypeId(AccountTypeName,db);
			//获取平台分红比例
			double  dividendpayoutratio = Double.parseDouble(VariableAcquisition.getInstance().returnstr("Platformdividendratio", db));
			//消费总金额
			double consumptionMoney = BonusJobclassImpl.getInstance().QuerySalesvolume(db);
			//分红总金额
			long Amountofdividends = new Double(consumptionMoney*dividendpayoutratio).longValue();
			//总分红权数 
			int Totalredweight=BonusJobclassImpl.getInstance().SumDividendRight(db);
			//每个分红权分红金额
			double averagemoney = BonusJobclassImpl.getInstance().CalculationAveragedividend(Amountofdividends, Totalredweight);
			
			//day_sales:当日销售额,proportionality:应分比例,day_dividend:当日分红额,effective_dividend:当日有效分红权,amount_of_each_dividend:每个分红权金额
			String insertSQL = "INSERT INTO lxny_Share_bonus_details("
					+ "ID"
					+ ",day_sales"
					+ ",proportionality"
					+ ",day_dividend"
					+ ",effective_dividend"
					+ ",amount_of_each_dividend"
					+ ",create_datetime)"
					+ " VALUES("
					+ "'"+UUID.randomUUID().toString()+"'"
					+ ",'"+consumptionMoney+"'"
					+ ",'"+dividendpayoutratio+"'"
					+ ",'"+Amountofdividends+"'"
					+ ",'"+Totalredweight+"'"
					+ ",'"+averagemoney+"'"
					+ ",'now()')";
			
			db.execute(insertSQL);
			
			
			if(averagemoney>=1)
			{
				
				String SQL="SELECT am.* FROM am_member AS am LEFT JOIN mall_account_info AS mai ON mai.member_orgid_id = am. ID LEFT JOIN mall_system_account_class AS msac ON msac. ID = mai.a_class_id WHERE mai.enabled_dividend_sharing > 0";
				
				MapList maplist = db.query(SQL);
				
				for (int i = 0; i < maplist.size(); i++) {
					
					//谢超 2017年3月30日11:06:05  将原有分红方式进行修改（手动SQL转账+插入交易记录 改为 使用系统转账+添加一张表存入分红信息）
					long money = BonusJobclassImpl.getInstance().Platformbonus(maplist.getRow(i).get("id"),db,vir);
//					BonusJobclassImpl.getInstance().transactionrecord(maplist.getRow(i).get("id"), trdl, bdb, money, db);
					BonusJobclassImpl.getInstance().UpdateMemberbonus(maplist.getRow(i).get("id"), db);
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
	}
}
