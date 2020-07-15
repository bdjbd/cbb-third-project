package com.am.app_plugins.Credit_card_account.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

//信誉卡每月还款操作
public class CreditCardAccountRepayJob implements Job {

	@Override
	public void execute(JobExecutionContext jc) throws JobExecutionException 
	{
		DB  db = null;
		
		VirementManager vir = new VirementManager();
		
		String code = SystemAccountClass.CREDIT_CARD_ACCOUNT;
		
		//查询每个社员的信息
		String msql = "select mai.* from am_member as am "
				+ " left join mall_account_info as mai on am.id = mai.member_orgid_id"
				+ " left join mall_system_account_class as myac on  myac.id=mai.a_class_id"
				+ " where am.member_type='3' "	
				+ " and myac.status_valid='1' "
				+ "and myac.sa_code = '"+code+"'";	;
		
		try {
			db = DBFactory.newDB();
			MapList mlist = db.query(msql);
			//逾期比例
			String creditcardaccount_late_fee = Var.get("creditcardaccount_late_fee");
			
			for (int i = 0; i < mlist.size(); i++) 
			{
				
				//查询社员信誉卡信息
				//判断是已用额度是否大于0 则有借款操作
				if(mlist.getRow(i).getLong("used_amount",0)>0L)
				{
					execCreditCardAccountOr(mlist.getRow(i),db,vir);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(db!=null)
			{
				try {
					db.close();
				} catch (JDBCException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	//更新信誉卡操作  更新已用额度 和可用额度
	public void execCreditCardAccountOr(Row row,DB db,VirementManager vir) throws Exception
	{
		 
		//已用额度
		Long used_amount = row.getLong("used_amount", 0);
		//可用额度
		Long available_amount = row.getLong("available_amount", 0);
		//已用取现额度
		Long used_in_cash = row.getLong("used_in_cash", 0);
		//取现次数
		int withdraw_num = row.getInt("withdraw_num",0);
		//可用取现额度
		Long available_cash_amount = row.getLong("available_cash_amount", 0);
		//授信额度
		Long credit_amount = row.getLong("credit_amount", 0);
		//滞纳金
		Long late_fee = row.getLong("late_fee",0);
		//获取还款日期
		String repayment_date = row.get("repayment_date");
		
		//滞纳金比例
		float creditcardaccount_late_fee = Float.parseFloat(Var.get("creditcardaccount_late_fee"))/100;
		
		//当前日期 上个月
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		//日期格式化 将还款日期格式化
		Date repaymentFmt = null;
		Date countDate = null;
		
		//判断还款日期 有则扣款
		if(!Checker.isEmpty(repayment_date))
		{
			countDate = sdf.parse(sdf.format(new Date()));
			
			repaymentFmt = sdf.parse(repayment_date) ;
			
	        Long day = 0L;
	        JSONObject jsoa = null;
	        //还款金额
	        Long repaymony =0L;
			//判断 
			//还款日期小于等于当前日期减去一个月即上个月 执行扣款 如果扣款没有成功则计算滞纳金 当前月1号-还款日期 算的天数*滞纳金比例*已用授信额度 
			if(countDate.after(repaymentFmt))
			{
		        	 //当前日期大于本月第一天
		        	 day=(countDate.getTime()-repaymentFmt.getTime())/(24*60*60*1000);
				
		        //滞纳金
		        late_fee = VirementManager.changeY2F(String.valueOf((creditcardaccount_late_fee*day*(used_amount/100))));
		        
		        repaymony = used_amount+late_fee;
		        if(late_fee>0)
		        {
		        	jsoa = vir.execute(db, row.get("member_orgid_id"), SystemAccountClass.CASH_ACCOUNT
		        			, String.valueOf(repaymony/100)
		        			, "信誉卡还款逾期自动扣款(滞纳金:'"+(late_fee/100)+"')", "", "");
		        	if("0".equals(jsoa.get("code")))
		        	{
		        		late_fee = 0L;
		        		used_amount = 0L;
		        		used_in_cash =0L;
		        		repayment_date = null;
		        		available_amount = credit_amount - used_amount;
		        	}
		        }

				String sql = "update mall_account_info set used_amount = '"+used_amount+"'"
						    + ",available_amount = '"+available_amount+"',"
				   			+ "used_in_cash = '"+used_in_cash+"',"
							+ "late_fee = '"+late_fee+"' ";
				
				if(used_amount<=0)
				{
					sql += ",repayment_date = "+repayment_date+" ";
				}
							
				sql += " where id = '"+row.get("id")+"'";
				
				db.execute(sql);
		        
			}
		}
		
	}
	
	//获取下一个月日期
	public Date nextMonthFirstDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }
	
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DB db,String outAccountCode,String outMemberId){
		
		MapList list = null;
		
		String sql = "";	
		//后台
			sql = "select mai.*"
					+ " ,myac.max_gmv"
					+ " ,myac.min_gmv"
					+ " ,myac.sa_code"
					+ " ,myac.id as class_id"
					+ " ,myac.transfer_fee_ratio"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE mai.member_orgid_id ='"+outMemberId+"'"
					+ " and myac.status_valid='1' and myac.sa_code = '"+outAccountCode+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
}
