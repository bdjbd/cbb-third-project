package com.am.frame.task.task;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

public class TaskManager {
	
	/**
	 * 任务引擎execute json key名
	 */
	public static final String TASK_ENGINE_CLASSNAME="TaskClassName";
	
	/**
	 * 用户id
	 */
	public static final String TASK_ENGINE_MEMBERID="TaskMemberId";
	
	
	
	private static TaskManager instance;  
	
	private TaskManager (){
	   
	}
   
	public static synchronized TaskManager getInstance() {  
	   if (instance == null) {  
	       instance = new TaskManager();  
	   }  
   		return instance;  
	}  
	
	/**
	 * 查询系统所有用户
	 * @return
	 */
	public MapList getAllUser(){
		
		DB db = null;
		
		MapList list = null;
		
		String sql = "SELECT ID FROM AM_MEMBER";
		
		try {
			
			db = DBFactory.newDB();
			
			list=db.query(sql);
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	/**
	 * 查询系统用户任务
	 * @return
	 */
	public MapList getUserTask(String memberId,String taskId,String enterTaskId){
		
		DB db = null;
		
		MapList list = null;
		
		String sql = "SELECT * FROM AM_USERTASK WHERE 1=1 ";
		
		if(memberId!=null&&!memberId.isEmpty()){
			sql +="AND MEMBERID='"+memberId+"' ";
		}
		
		if(taskId!=null&&!taskId.isEmpty()){
			sql +="AND id='"+taskId+"' ";
		}
		
		if(enterTaskId!=null&&!enterTaskId.isEmpty()){
			sql +="AND ENTERTASKID='"+enterTaskId+"' ";
		}
		
		try {
			
			db = DBFactory.newDB();
			
			list=db.query(sql);
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	
	/**
	 * 获取实体任务 
	 * @return
	 */
	public MapList getEnterpriseTask (String EnterTaskId){
		
		DB db = null;
		
		MapList list = null;
		
		String sql = "SELECT *  FROM AM_ENTERPRISETASK  WHERE MEMBERID='"+EnterTaskId+"'";
		
		try {
			
			db = DBFactory.newDB();
			
			list=db.query(sql);
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	/**
	 * 获取实体任务 
	 * @return
	 */
	public MapList getAllEnterpriseTask (){
		
		DB db = null;
		
		MapList list = null;
		
		String sql = "SELECT *  FROM AM_ENTERPRISETASK WHERE ETASKSTATE='1'";
		
		try {
			
			db = DBFactory.newDB();
			
			list=db.query(sql);
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	
	/**
	 * 获取实体任务 
	 * @return
	 */
	public String getUserEnterpriseTask (String memberid,String className){
		
		DB db = null;
		
		MapList list = null;
		
		MapList userList = null;
		
		String userTaskId = "";
		
		String sql = "SELECT AES.ID FROM AM_ENTERPRISETASK AS AES";
		sql += " LEFT JOIN AM_TASKTEMPLATE AS ATS ON ATS. ID = AES.TASKTEMPLATEID ";
		sql += " WHERE 1=1 ";
		
		String usql ="SELECT ID　FROM AM_USERTASK WHERE 1=1 ";
		
		if(!className.isEmpty()){
			sql += " AND ATS.CLASSPATH='"+className+"' ";
		}
		
		try {
			
			db = DBFactory.newDB();
			
			list=db.query(sql);
			
			for (int i = 0; i < list.size(); i++) {
				
				usql += " ENTERPRISETASKID = '"+list.getRow(i).get("id")+"'";
				
				usql += " AND MEMBERID = '"+memberid+"'";
				
				userList = db.query(sql);
				
			}
			
			if(userList.size()>0){
				
				userTaskId = userList.getRow(0).get("id");
			
			}
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				if(db!=null){
					db.close();
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return userTaskId;
		
	}
}
