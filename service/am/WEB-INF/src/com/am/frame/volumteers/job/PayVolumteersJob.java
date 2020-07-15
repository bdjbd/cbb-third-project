package com.am.frame.volumteers.job;

import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 志愿者保证金 每年退换保证金
 * @author mac
 *
 */
public class PayVolumteersJob implements Job {

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException 
	{
		//
		DB db = null;
		String sql = "select * from volunteers_pay_record where status = '0' and to_char(create_time+ interval '1 y','yyyy-mm-dd hh24:mi')  = to_char(now(),'yyyy-mm-dd hh24:mi')";
		MapList mlist = null;
		VirementManager vir = new VirementManager();
		JSONObject jso = null;
		try{
			db = DBFactory.newDB();
			mlist = db.query(sql);
			
			
			if(!Checker.isEmpty(mlist))
			{
				for (int i = 0; i < mlist.size(); i++) 
				{
						vir.getAccountInfo(db, SystemAccountClass.CASH_ACCOUNT,mlist.getRow(i).get("member_id").toString());
						
						jso = vir.execute(db, "",  mlist.getRow(i).get("member_id").toString(), ""
								,SystemAccountClass.CASH_ACCOUNT,mlist.getRow(i).get("pay_amount"),"志愿者信用保证金退还","","",false);
						
						if(jso!=null && "0".equals(jso.get("code").toString()))
						{
							String usql = "update volunteers_pay_record set status = '1',refund_amount = '"+mlist.getRow(i).get("pay_amount")+"',refund_time = 'now()' where id = '"+mlist.getRow(i).get("id").toString()+"'";
							db.execute(usql);
						}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(db!=null)
			{
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
