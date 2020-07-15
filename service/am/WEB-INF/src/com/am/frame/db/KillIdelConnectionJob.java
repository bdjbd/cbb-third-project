package com.am.frame.db;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class KillIdelConnectionJob implements Job {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		String querySQL="SELECT pid FROM pg_stat_activity WHERE state='idle' AND query='select vid,vvalue from AVAR' ";
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			logger.info("kill idel connection");
			
			MapList map=db.query(querySQL);
			if(!Checker.isEmpty(map)){
				logger.info("kill map size:"+map.size());
				for(int i=0;i<map.size();i++){
					Row row=map.getRow(i);
					String pid=row.get("pid");
					logger.info("kill idel pid:"+pid);
					
					try{
						db.query("select pg_terminate_backend("+pid+")");
					}catch(Exception e){
						logger.info("kill pid error pid:"+pid);
					}
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			logger.info("kill exception "+e.getMessage());
		}finally{
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
