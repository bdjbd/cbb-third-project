package com.am.frame.other.taskInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月26日
 * @version 
 * 说明:<br />
 * 第三方任务接口管理类
 * 
 * 
 create接口任务（接口任务类型code，业务数据ID）
{
	插入接口任务执行数据；
	并调用接口；
}

callOtherInterfaceTask(接口任务类型code)
{
	
}

create被动响应接口数据()
{

} 
 */
public class OtherInterfaceTaskManager {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	public static final String DOMAIN="am_bdp";
	
	/**
	 * 接任务创建，当有新任务需要调用是，需要在第三方接口任务配置
	 * 中配置接口任务，然后调用此方法添加任务。<br>
	 * 调用此方法将会在接口任务执行记录表新增一条任务记录数据。
	 * @param db DB
	 * @param taskImplCode　第三方任务接口配置CODE
	 * @param bussData  业务参数ID
	 */
	public void createTask(DB db,String taskImplCode,String bussData)
	{
		try 
		{
			logger.info("准备创建第三方接口任务");
			
			//1,根据taskImplCode查询具体实现的配置信息
			String querSQL="SELECT * FROM am_other_task_impl WHERE task_code='" + taskImplCode + "' ";
			MapList map=db.query(querSQL);
			
			logger.info("检查" + taskImplCode + "是否存在，map.size()=" + map.size() + " | !Checker.isEmpty(map)=" + (!Checker.isEmpty(map)));
			
			
			//当前记录数
			int reloadCount=0;

			
			
			//该接口实现的配置是存在的,则可创建新任务
			if(!Checker.isEmpty(map))
			{		
				String tIsReload=map.getRow(0).get("isreload").toString();
				
				logger.info("开始创建第三方接口任务，其中：am_other_task_impl.task_code=" + taskImplCode 
						+ ";am_other_task_record.bussData=" + bussData 
						+ ";" + "tIsReload=" + tIsReload + "[" + tIsReload.equals("0") + "];");
				
				//获取该任务是否可以重复1=可重复，0=不可重复
				if(tIsReload.equals("0"))
				{
					//接口任务配置id
					String tOtherTaskImpID=map.getRow(0).get("id");
					
					//检查接口任务表中是否已经有bussData的订单，如果没有则可新增任务，否则不新增任务
					String tSql="select * from am_other_task_record where busse_data='" + bussData + "' and other_task_impl_code='" + tOtherTaskImpID + "'";
					MapList mapOtherTaskRecord=db.query(tSql);
					
					reloadCount=mapOtherTaskRecord.size();
				}
				
				//如果该任务可重复则reloadCount=0，如果不可重复则reloadCount为实际的记录数
				if(reloadCount==0)
				{
					Row row=map.getRow(0);
					
					//2,将数据插入到记录中，由joB执行
					Table recordTable=new Table(DOMAIN, "am_other_task_record");
					TableRow insertRow=recordTable.addInsertRow();
					
					insertRow.setValue("other_task_impl_code", row.get("id"));
					//1:主动请求2:被动响应  默认为主动请求
					insertRow.setValue("access_direction",1);
					insertRow.setValue("busse_data",bussData);
					insertRow.setValue("task_retry_time",0);
					insertRow.setValue("max_retry_time",row.getInt("max_retry_time",0));
					//任务状态（0=待执行【首次执行及重试均为该状态】，1=成功，3=失败【经过重试后失败】）
					insertRow.setValue("task_tate",0);
					
					db.save(recordTable);
					
					//立即调用接口处理当前数据
					String taskRecordId=insertRow.getValue("id");
					callOtherInterfaceTask(db, taskRecordId);
					
					
					logger.info("创建第三方接口任务成功！任务执行完成。");
				}
				else
				{
					logger.info("创建第三方接口任务失败。其中: 任务要求taskImplCode所对应bussData的任务必须唯一，但是发现am_other_task_record.行数=" + reloadCount + ";");
				}
				
			}
			else
			{
				logger.info("第三方接口任务创建失败。其中: am_other_task_impl应有taskImplCode=" + Checker.isEmpty(map) + "的任务配置，但是没有找到该配置。");
			}
			
			
		} 
		catch (JDBCException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 根据接口任务执行记录表中的ID，调用此接口
	 * @param db  DB 数据库
	 * @param recordId  接口任务记录ID  
	 */
	public void callOtherInterfaceTask(DB db,String recordId ){
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("SELECT impl.task_code,task_name,class_path                                ");
		querySQL.append("	FROM am_other_task_record AS rec                                         ");
		querySQL.append("	LEFT JOIN am_other_task_impl AS impl ON rec.other_task_impl_code=impl.id ");
		querySQL.append("	WHERE rec.id=?                                                           ");
		
		try {
			MapList map=db.query(querySQL.toString(),recordId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				String classPath=row.get("class_path");
				String taskName=row.get("task_name");
				if(classPath!=null){
					ITaskOtherInterface otherImplTask=(ITaskOtherInterface) 
							Class.forName(classPath).newInstance();
					
					logger.info("执行第三方接口任务："+taskName+"\tclassPath:"+classPath);
					
					otherImplTask.execute(db, recordId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 保存被动数据到执行记录中
	 * @param db BD
	 * @param requestData　请求数据
	 * @param responseData　相应数据
	 */
	public void createPassive(DB db,String requestData,String responseData,String busseData){
		
		try {
			Table recordTable=new Table(DOMAIN, "am_other_task_record");
			TableRow insertRow=recordTable.addInsertRow();
			
			insertRow.setValue("request_time", requestData);
			insertRow.setValue("response_data", requestData);
			insertRow.setValue("busse_data", busseData);
			
			//1:主动请求2:被动响应  默认为主动请求
			insertRow.setValue("access_direction",2);
			
			//任务状态（0=待执行【首次执行及重试均为该状态】，1=成功，3=失败【经过重试后失败】）
			insertRow.setValue("task_tate",1);
		
			db.save(recordTable);
			
			logger.info("保存被动数据到执行记录中");
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	} 
	
}
