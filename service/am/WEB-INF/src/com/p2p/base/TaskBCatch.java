package com.p2p.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.p2p.base.task.IUserTask;


/**
 * 任务缓存
 * @author Administrator
 *
 */
public class TaskBCatch {
	private static Map<String, IUserTask> taskCatch=new HashMap<String, IUserTask>();
	
	/**
	 * 根据ID获取用户任务实例
	 * @param templeId  模板任务ID
	 * @return
	 */
	public static IUserTask getUserTaskById(String templeId){
		if(taskCatch==null)taskCatch=new HashMap<String, IUserTask>();
		IUserTask userTask=taskCatch.get(templeId);
		if(userTask==null){
			init();
			userTask=taskCatch.get(templeId);
		}
		return userTask;
	}
	
	
	private static void init(){
		
		DB db=null;
		Connection conn=null;
		
		try {
			
			db=DBFactory.newDB();
			
			String getTaskClass="SELECT id, classPath "
					+ " FROM p2p_TaskTemplate  WHERE TemplateState='1' ";
			conn=db.getConnection();
			
			ResultSet rs=conn.createStatement().executeQuery(getTaskClass);
			if(rs.next()){
				String clazzPath=rs.getString("classpath");
				String id=rs.getString("id");
				IUserTask userTask=(IUserTask)Class.forName(clazzPath).newInstance();
				taskCatch.put(id,userTask);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
