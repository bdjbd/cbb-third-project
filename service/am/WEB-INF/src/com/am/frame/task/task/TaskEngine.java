package com.am.frame.task.task;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 任务管理类
 * @author Administrator
 *例如：{"TaskDescription" : "","TaskObject" : "",
 *	"TaskReward" : {"现金" : "10","积分" : "20","徽章" : "ID","优惠券" : "ID"},
 *	"TaskExecution" : {"目标推广人数" : "10","已推广人数" : "0"}}
 */
public class TaskEngine  implements ITaskEngine {
	
	private static TaskEngine instance;
	
	/**社员任务ID**/
	public static String USER_TASK_ID="TaskEngine.USER_TASK_ID"; 
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private TaskEngine(){
		
	}
	
	public static synchronized TaskEngine getInstance(){
		
		if(instance == null){
			
			instance = new TaskEngine();
			
		}
		return instance;
	}
	
	/**
	 * 为所有会员分配任务
	 */
	@Override
	public boolean distributionTaskToUsers(String EntityTaskID) {
		
		/**
		 * 所有用户
		 */
		MapList memberList = null;
		/**
		 * 用户任务
		 */
		MapList userTask = null;
		DB db = null;
		
		memberList = TaskManager.getInstance().getAllUser();
		
		int rest = 0;
		
		String sql = "INSERT INTO AM_USERTASK ID,MEMBERID,TASKPARAME,TASKRUNSTATE,CREATEDATE,ENTERTASKID";
		
		try {
			
			db = DBFactory.newDB();
			
			if(memberList.size()>0){
				
				for (int i = 0; i < memberList.size(); i++) {
					
					userTask = TaskManager.getInstance().getUserTask(memberList.getRow(i).get("memberid"),"","");
					
					if(userTask.size()<=0){
						
						sql += " VALUES('"+UUID.randomUUID()+"',";
						sql += " VALUES('"+memberList.getRow(i).get("ID")+"',";
						sql += " VALUES('"+TaskManager.getInstance().getEnterpriseTask(EntityTaskID).getRow(0).get("taskparame")+"',";
						sql += " VALUES('1',";
						sql += " VALUES(now(),";
						sql += " VALUES('"+TaskManager.getInstance().getEnterpriseTask(EntityTaskID).getRow(0).get("id")+"')";
						rest = db.execute(sql);
					}
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * 为所有会员删除任务，实体任务ID
	 */
	@Override
	public boolean deleteTaskToUsers(String EntityTaskID) {
		
		/**
		 * 所有的用户
		 */
		MapList memberList = null;
		
		/**
		 * 用户任务
		 */
		MapList userTask = null;
		
		memberList = TaskManager.getInstance().getAllUser();
		
		String sql = "DELETE FROM AM_USERTASK WHERE ";
		
		int rest = 0;
		DB db=null;
		try {
			
			db = DBFactory.newDB();
			
			if(memberList.size()>0){
				
				for (int i = 0; i < memberList.size(); i++) {
					
					userTask = TaskManager.getInstance().getUserTask(memberList.getRow(i).get("memberid"),"","");
					
					if(userTask.size()<=0){
						
						sql += " ENTERTASKID = '"+EntityTaskID+"'";
							rest = db.execute(sql);
					}
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * 给指定用户分配任务
	 */
	@Override
	public boolean distributionAllTaskToUser(String memberID) {
		
		/**
		 * 所有实体任务
		 */
		MapList enterpriseTask = null;
		
		/**
		 * 用户任务
		 */
		MapList userTask = null;
		
		enterpriseTask = TaskManager.getInstance().getAllEnterpriseTask();
		
		int rest = 0;
		
		String sql = "INSERT INTO AM_USERTASK ID,MEMBERID,TASKPARAME,TASKRUNSTATE,CREATEDATE,ENTERTASKID";
		DB db=null;
		try {	
			db = DBFactory.newDB();
			if(enterpriseTask.size()>0){
				for (int i = 0; i < enterpriseTask.size(); i++) {
					userTask = TaskManager.getInstance().getUserTask(
							memberID,"",
							enterpriseTask.getRow(i).get("id"));
					if(userTask.size()<=0){
							sql += " VALUES('"+UUID.randomUUID()+"',";
							sql += " VALUES('"+enterpriseTask.getRow(i).get("id")+"',";
							sql += " VALUES('"+enterpriseTask.getRow(i).get("taskparame")+"',";
							sql += " VALUES('1',";
							sql += " VALUES(now(),";
							sql += " VALUES('"+enterpriseTask.getRow(i).get("id")+"')";
							rest = db.execute(sql);
					}
				}
			}
		
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		if(rest>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 执行任务
	 * 
	 */
	@Override
	public boolean executTask(RunTaskParams parame) {
		boolean result = false;
		
		logger.info("executTask:"+parame.getTaskName());
		
		if(parame==null||parame.getMemberId()==null){
			try {
				throw new Exception("任务参数不正确，必须要有任务参数和任务会员ID");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			//任务编码启动任务优先
			if(parame!=null&&!Checker.isEmpty(parame.getTaskCode())){
				result=executeTaskByTaskCode(parame);
			}else{
				if(parame!=null&&!Checker.isEmpty(parame.getTaskClassName())){
					result=executeTaskByClassName(parame);
				}else{
					result=executeTaskByTaskName(parame);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据任务参数，任务参数中包含了任务的实现类
	 * @param parame
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected boolean executeTaskByClassName(RunTaskParams parame) throws Exception {
		boolean result = false;
		
		ITask clazz = (ITask)Class.forName(parame.getTaskClassName()).newInstance();
		clazz.initTask(parame);
		result = clazz.execute(parame);
							
		return result;
	}
	
	/**
	 * 根据企业任务名称，执行任务
	 * @param parame 任务参数
	 * @return
	 */
	protected boolean executeTaskByTaskName(RunTaskParams parame) throws Exception {
		boolean result=false;
		DB db=DBFactory.newDB();
		
		//根据任务名称，查询企业任务信息
		String querySQL="SELECT tp.classpath,et.* FROM am_enterprisetask AS et "
				+ " LEFT JOIN am_tasktemplate AS tp "
				+ " ON et.tasktemplateid=tp.id WHERE 1=1   ";
		
		if(!Checker.isEmpty(parame.getOrgId())){
			querySQL+=" AND et.OrgID='"+parame.getOrgId()+"'  ";
		}
		querySQL+=" AND et.name='"+parame.getTaskName()+"' ";
		
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			parame.setTaskClassName(map.getRow(0).get("classpath"));
		}
		
		result=executeTaskByClassName(parame);
		
		db.close();
		
		return result;
	}
	
	
	/**
	 * 根据企业任务编码，执行任务
	 * @param parame 任务参数
	 * @return
	 */
	protected boolean executeTaskByTaskCode(RunTaskParams parame) throws Exception {
		boolean result=false;
		
		DB db=DBFactory.newDB();
		
		//根据任务名称，查询企业任务信息
		String querySQL="SELECT tp.classpath,et.* FROM am_enterprisetask AS et "
				+ " LEFT JOIN am_tasktemplate AS tp "
				+ " ON et.tasktemplateid=tp.id WHERE 1=1   ";
		
		if(!Checker.isEmpty(parame.getOrgId())){
			querySQL+=" AND et.OrgID='"+parame.getOrgId()+"'  ";
		}
		querySQL+=" AND et.task_code='"+parame.getTaskCode()+"' ";
		
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			parame.setTaskClassName(map.getRow(0).get("classpath"));
		}
		
		result=executeTaskByClassName(parame);
		
		db.close();
		
		return result;
	}
	
	
	/**
	 * 获得任务详情
	 * @memberId  会员ID
	 * @userTaskID  会员任务ID
	 */
	
	public String getTaskHtmlView(RunTaskParams parame) {
		
		String htmlView="";
		
		//社员ID
		String memberId=parame.getMemberId();
		//用户任务ID
		String userTaskID=parame.getParams(USER_TASK_ID);
		
		String querySQL = " SELECT at.classpath,au.memberid "+
				" FROM AM_USERTASK AS au "+
				" LEFT JOIN am_enterprisetask AS ae ON ae.id = au.entertaskid  "+
				" LEFT JOIN am_TaskTemplate AS at ON at.id=ae.tasktemplateid  "+
				" WHERE au.memberid='"+memberId+"' AND au.id='"+userTaskID+"' ";
		
		DB db=null;
		
		try {
			db = DBFactory.newDB();
			
			MapList userTaskMap=db.query(querySQL);
			
			if(!Checker.isEmpty(userTaskMap)){
				for(int i=0;i<userTaskMap.size();i++){
					Row row=userTaskMap.getRow(i);
					//任务实现类路径
					String classPath=row.get("classpath");
					parame.setTaskClassName(classPath);;
					
					ITask userTask = (ITask)Class.forName(classPath).newInstance();
					userTask.initTask(parame);
					
					htmlView=userTask.getTaskHtmlView(userTaskID);
					
				}
			}
			
			
		} catch (Exception e) {
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
		return htmlView;
	}
	
	/**
	 * 给用户初始化任务
	 * @param memberId
	 */
	public void addUserAllTask(String memberId,String orgId,DB db){
		//1,查询当前系统的所有启用的企业任务
		//2,给指定的用户初始化任务
		
		 orgId = "org";
		
		String queryTaskSQL="SELECT et.*,t.classpath "+
				" FROM am_enterprisetask AS et "+
				" LEFT JOIN am_tasktemplate AS t ON et.tasktemplateid=t.id "+
				" WHERE et.etaskstate='1' ";
		
				/**
				 * 2017年03月25日
				 * 由于后台需要使用该方法初始化任务，而任务是通过组织机构代码寻找，所以导致后台无法使用，将该方法注释掉，如后期需要各个机构配各自的任务则解掉该注释
				 */
				if(orgId!=null){
					queryTaskSQL+=" AND et.orgid='"+orgId+"' ";
				}
				
		try{
			MapList eTaskMap=db.query(queryTaskSQL);
			if(!Checker.isEmpty(eTaskMap)){
				for(int i=0;i<eTaskMap.size();i++){
					Row row=eTaskMap.getRow(i);
					String taskClassPath=row.get("classpath");
					String entTaskId=row.get("id");
					ITask task=(ITask) Class.forName(taskClassPath).newInstance();
					task.addTaskByUserId(memberId, entTaskId, db);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据企业任务编码重置用户任务
	 * @param db  DB
	 * @param memberId  会员ID
	 * @param entTaskCode 企业任务编码
	 */
	public void resetUserTaskByTaskCode(DB db,String memberId,String entTaskCode,String orgId)throws Exception{
		//根据企业编码查询任务
		String delTaskSQL="DELETE FROM am_UserTask WHERE id=? ";
		
		String querySQL="SELECT ut.*  "+
						"  FROM am_UserTask AS ut  "+
						"  LEFT JOIN am_EnterpriseTask AS et ON ut.entertaskid=et.id  "+
						"  WHERE ut.memberId=? AND et.task_code=? ";
		MapList map=db.query(querySQL,new String[]{memberId,entTaskCode},new int[]{Type.VARCHAR,Type.VARCHAR});
		if(!Checker.isEmpty(map)){
			String utId=map.getRow(0).get("id");
			
			//1,删除用户任务
			db.execute(delTaskSQL, utId,Type.VARCHAR);
		}
		
		//2,给用户初始化此任务
		addUseTaskByTaskCode(db, memberId, orgId, entTaskCode);
		
	}
	
	
	public void addUseTaskByTaskCode(DB db,String memberId,String orgId,String etTaskCode){
		String queryTaskSQL="SELECT et.*,t.classpath "+
				" FROM am_enterprisetask AS et "+
				" LEFT JOIN am_tasktemplate AS t ON et.tasktemplateid=t.id "+
				" WHERE et.etaskstate='1' "+
				" AND  et.task_code='"+etTaskCode+"' ";
		
				if(orgId!=null){
					queryTaskSQL+=" AND et.orgid='"+orgId+"' ";
				}
				
		try{
			MapList eTaskMap=db.query(queryTaskSQL);
			if(!Checker.isEmpty(eTaskMap)){
				for(int i=0;i<eTaskMap.size();i++){
					Row row=eTaskMap.getRow(i);
					String taskClassPath=row.get("classpath");
					String entTaskId=row.get("id");
					ITask task=(ITask) Class.forName(taskClassPath).newInstance();
					task.addTaskByUserId(memberId, entTaskId, db);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public String getTaskHtmlView(String MemberID, String userTaskID) {
		TaskEngine taskEngine=TaskEngine.getInstance();
		RunTaskParams params=new RunTaskParams();
		params.setMemberId(MemberID);
		params.pushParam(TaskEngine.USER_TASK_ID, userTaskID);
		
		//获取任务视图代码
		String viewResult=taskEngine.getTaskHtmlView(params);
		return viewResult;
	}
	
}
