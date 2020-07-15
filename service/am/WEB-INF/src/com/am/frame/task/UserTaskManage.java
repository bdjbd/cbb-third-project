package com.am.frame.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * 用户任务管理类
 * @author Administrator
 */
public class UserTaskManage {
	
	public static final String tag="UserTaskManage";
	
	private static UserTaskManage usertTaskManage;
	
	private UserTaskManage(){}
	
	/**
	 * 获取任务管理类
	 * @return
	 */
	public static UserTaskManage getInstance(){
		if(usertTaskManage==null){
			usertTaskManage=new UserTaskManage();
		}
		return usertTaskManage;
	}
	
	/**
	 * 获取任务HTML
	 * @param memberCode  会员编号
	 * @param taskId    任务ID
	 * @return
	 */
	public String getHtml(String memberCode ,String taskId){
		String html="";
		try{
			String getTaskSQL=
					"SELECT tt.classpath                                               "+
							"	FROM am_UserTask  AS ut                                         "+
							"	LEFT JOIN am_EnterpriseTask AS et ON ut.enterprisetaskid=et.id  "+
							"	LEFT JOIN am_TaskTemplate AS tt ON et.tasktemplateid=tt.id      "+
							"	WHERE  ut.id='"+taskId+"'                "+
							"	AND ut.memberid='"+memberCode+"'";
			DB db=DBFactory.getDB();
			
			//查询任务实现类
			MapList taskMap=db.query(getTaskSQL);
			
			if(!Checker.isEmpty(taskMap)){
				String clazzName=taskMap.getRow(0).get("classpath");
				IUserTask userTask=(IUserTask)Class.forName(clazzName).newInstance();
				
				//初始化任务
				userTask.init(memberCode, taskId);
				
				//获取任务的HTML代码
				html=userTask.getHtml();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return html;
	}
	
	
	/**
	 * 初始化用户任务
	 * @param orgid
	 * @param memberCode
	 */
	public  void initUserTask(String orgid, String memberCode) {
		
		String sql="SELECT id,OrgID FROM am_EnterpriseTask  WHERE orgid='"+orgid+"'  AND ETaskState='1'";
		try {
			DB db=DBFactory.getDB();
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)){
				
				for(int i=0;i<map.size();i++){
					UserTaskManage.initTask(orgid, map.getRow(i).get("id"),memberCode);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	    * 为机构下的所有用户初始化任务
	    * @param userId 机构ID
	    * @param enterparisId 企业任务ID
	    */
	public static void initTask(String orgid, String enterparisId) {
//		try{
//			//已经有此任务的用户不会重新生成任务
//			String sql=
//					" INSERT INTO am_UserTask "+ 
//					"(id,entertaskid,taskrunstate,memberid,taskparame,createdate)"+
//					"( "+
//					" SELECT uuid_generate_v4(),"+
//					" et.id,'1'::character varying(2) AS TaskRunState,"+ 
//					" me.id,et.TaskParame,now() "+ 
//					" FROM am_EnterpriseTask AS et "+
//					" LEFT JOIN am_member AS me   "+
//					" ON me.orgcode=et.orgid  "+
//					" WHERE et.orgid='"+orgid+"'  "+
//					" AND et.id='"+enterparisId+"' "+
//					" AND me.id NOT IN ("+ 
//					" SELECT memberid FROM am_usertask "+
//					" WHERE entertaskid='"+enterparisId+"') "
//					+ ")";
//			DBFactory.getDB().execute(sql);
//			
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			String getEntTaskSQL="SELECT * FROM am_enterprisetask WHERE id='"+enterparisId+"' ";
			
			MapList map=db.query(getEntTaskSQL);
			String taskparame="";
			//范围 0 所有；1消费者社员；3生产者社员；
			String scope="0";
			
			if(!Checker.isEmpty(map)){
				taskparame=map.getRow(0).get("taskparame");
			}
			
			
			String queryTaskSQL=" SELECT * FROM am_member  WHERE 1=1";
//			if(orgid!=null){
//				queryTaskSQL+=" AND orgcode LIKE '"+orgid+"%' ";
//			}
			if(!"0".equals(scope)){//如果不为0 则只给对应类型的社员分配任务
				queryTaskSQL+=" AND am_member_type='"+scope+"' ";
			}
			
			MapList maps=db.query(queryTaskSQL);
			
			//1,查询所有的会员id
			//2，为所有的会员更新任务
			
			String instSQL="INSERT INTO am_usertask( "
					+ "  taskrunstate, memberid, taskparame, createdate, id, entertaskid) "
					+ "VALUES ('1', ?, ?, now(), ?, ?) ";
			
			List< String[]> values=new ArrayList<String[]>();
			
			for (int i = 0; i < maps.size(); i++){
				Row row=maps.getRow(i);
				values.add(new String[]{row.get("id"),
						taskparame,
						UUID.randomUUID().toString(),
						enterparisId});
			}
			
			db.executeBatch(instSQL,values, new int[]{
					Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR});
			
		}catch(Exception e){
			e.printStackTrace();
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
	
	
	/**
	    * 为机构下的指定的用户初始化任务
	    * @param userId 机构ID
	    * @param enterparisId 企业任务ID
	    * @param  memberCode  用户ID
	    */
	public static void initTask(String orgid, String enterparisId,String memberCode) {
		try{
			//已经有此任务的用户不会重新生成任务
			String sql=
					" INSERT INTO am_UserTask ( "+
					" SELECT uuid_generate_v4(),"+
					" et.id,'1'::character varying(2) AS TaskRunState,"+ 
					" me.id,et.TaskParame "+ 
					" FROM am_EnterpriseTask AS et "+
					" LEFT JOIN am_member AS me   "+
					" ON me.orgcode=et.orgid  "+
					" WHERE et.orgid='"+orgid+"'  "+
					" AND et.id='"+enterparisId+"' "+
					" AND me.id='"+memberCode
					+ "')";
			
			DBFactory.getDB().execute(sql);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
