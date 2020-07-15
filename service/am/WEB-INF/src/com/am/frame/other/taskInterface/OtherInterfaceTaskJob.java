package com.am.frame.other.taskInterface;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * 第三方接口回调JOB
 */
public class OtherInterfaceTaskJob implements Job{

	@Override
	public void execute(JobExecutionContext jb) throws JobExecutionException {
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("SELECT rec.id AS recid,impl.task_code,task_name,class_path                  ");
		querySQL.append("	FROM am_other_task_record AS rec                                         ");
		querySQL.append("	LEFT JOIN am_other_task_impl AS impl ON rec.other_task_impl_code=impl.id "); 
		querySQL.append("	WHERE rec.access_direction=1 AND rec.task_tate=0                         ");
		
		DB db=null;
		
		try{
			
			db=DBFactory.newDB();
			
			OtherInterfaceTaskManager taskManager=new OtherInterfaceTaskManager();
			
			//查询所有的待执行的任务记录
			MapList waitTaskMap=db.query(querySQL.toString());
			
			if(!Checker.isEmpty(waitTaskMap)){
				for(int i=0;i<waitTaskMap.size();i++){
					Row row=waitTaskMap.getRow(i);
					String recordId=row.get("recid");
					
					//执行记录中的任务 
					taskManager.callOtherInterfaceTask(db, recordId);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
		  if(db!=null){
			  try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
}
