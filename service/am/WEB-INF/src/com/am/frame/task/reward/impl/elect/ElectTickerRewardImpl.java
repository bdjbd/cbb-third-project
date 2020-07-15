package com.am.frame.task.reward.impl.elect;

import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITask;
import com.am.frame.task.task.ITaskReward;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月30日
 * @version 
 * 说明:<br />
 * 电子券任务奖励规则
 */
public class ElectTickerRewardImpl implements ITaskReward {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean execute(RunTaskParams param) {
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			//获取奖励优惠券参数 ，优惠券ID
			String eetId=(String) param.getParams().get(ITask.REWARD_VALUE);
			
			//1查询优惠券的信息，需要判断优惠券是否使用完，如果使用完，则不奖励
			String querySQL="SELECT * FROM am_EterpElectTicket WHERE id=? AND datastatus=2 ";
			MapList map=db.query(querySQL, eetId, Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				//有效天数
				String effectiveDate =row.get("effectivedate");
				String  queryExpiredDateSQL="SELECT now()+interval '"+effectiveDate+" days' AS expired ";
				MapList dataMap=db.query(queryExpiredDateSQL);
				if(!Checker.isEmpty(dataMap)){
					
					String expired=dataMap.getRow(0).get("expired");
					
					StringBuilder inserSQL=new StringBuilder();
					
					String id=UUID.randomUUID().toString();
					
					inserSQL.append("INSERT INTO am_orgelectticker(                                           ");
					inserSQL.append("            id, eterpelectticketid, am_memberid, usestatus, getdatetime, ");
					inserSQL.append("             expired)                                        ");
					inserSQL.append("    VALUES ('"+id+"','"+row.get("id")+"','"+param.getMemberId()+"','1',now() ,                                               ");
					inserSQL.append("            '"+expired+"');                                                       ");
					
					db.execute(inserSQL.toString());
					
				}
				
			}else{
				logger.info("奖励的优惠券未启用 或优惠券不存在，优惠券ID："+eetId);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return true;
	}


}
