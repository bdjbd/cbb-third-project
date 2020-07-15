package com.am.more.kopp_train.Job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author xiechao
 * 2016年10月24日21:13:12
 * 
 * 每月1日0点更新购买文章金额=0，
 * 提现任务为未完成，
 * 每月阅读量为未完成，
 * 提现状态为否
 * 
 * 阅读量未完成：am_member中的journal_fee（分）小于常量中的monthly_journal_fee（元）
 * 
 * @throws JDBCException 
 */
public class Update_Journal_Fee_Job implements Job {

	@Override
	public void execute(JobExecutionContext ac) throws JobExecutionException {
		
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			//更新报刊费用
			String updatejournal_feeSQL = "UPDATE AM_MEMBER SET journal_fee='0' WHERE 1=1";
			
			db.execute(updatejournal_feeSQL);
			
			//提现任务更新为未完成
			
			String updatetaskSQL = " UPDATE am_UserTask "
					+ " SET TaskRunState = '1' "
					+ " WHERE entertaskid = "
					+ " ( "
					+ " SELECT id "
					+ " FROM am_EnterpriseTask "
					+ " WHERE task_code='GET_VOLUNTEER_ACCOUNT_WITH_QUALIFICATION' "
					+ " ) ";
			
			db.execute(updatetaskSQL);
//			String updateTaskStateSQL="SELECT utask.id,utask.TaskParame,utask.TaskRunState "+
//										" FROM am_UserTask AS utask "+
//										" LEFT JOIN am_EnterpriseTask AS etask ON utask.entertaskid=etask.id  "+
//										" WHERE etask.task_code='GET_VOLUNTEER_ACCOUNT_WITH_QUALIFICATION'";
//			MapList map=db.query(updateTaskStateSQL);
//			
//			JSONObject taskParamsJs=null;
//			
//			String updatelSQL="UPDATE am_UserTask SET TaskParame=?,TaskRunState=1 WHERE id=? ";
//			
//			for(int i=0;i<map.size();i++){
//				String taskParams=map.getRow(i).get("taskparame");
//				String id=map.getRow(i).get("id");
//				if(taskParams!=null){
//					
//					taskParamsJs=new JSONObject(taskParams);
//					taskParamsJs.getJSONObject("TaskExecution").getJSONObject("taskCurrentProcess")
//					.getJSONObject("PURCHASE_ARTICLE_AMOUNT").put("VALUE", "0");
//					
//					db.execute(updatelSQL,new String[]{
//							taskParamsJs.toString(),id
//					},new int[]{
//							Type.VARCHAR,Type.VARCHAR
//					});
//				}
//			}
	
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}finally
		{
			try {
				if(db!=null){
					db.close();
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
	}
}
