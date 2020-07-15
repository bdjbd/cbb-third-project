package com.p2p.task;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;


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
							"	AND memberId='"+memberCode+"'";
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
		try{
			//已经有此任务的用户不会重新生成任务
			String sql=
					" INSERT INTO am_UserTask ( "+
					" SELECT uuid_generate_v4(),"+
					" et.id,'1'::character varying(2) AS TaskRunState,"+ 
					" me.member_code,et.TaskParame "+ 
					" FROM am_EnterpriseTask AS et "+
					" LEFT JOIN ws_member AS me   "+
					" ON me.orgid=et.orgid  "+
					" WHERE et.orgid='"+orgid+"'  "+
					" AND et.id='"+enterparisId+"' "+
					" AND me.member_code NOT IN ("+ 
					" SELECT member_code FROM am_usertask "+
					" WHERE enterprisetaskid='"+enterparisId+"')"
					+ ")";
			DBFactory.getDB().execute(sql);
		}catch(Exception e){
			e.printStackTrace();
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
					" me.member_code,et.TaskParame "+ 
					" FROM am_EnterpriseTask AS et "+
					" LEFT JOIN ws_member AS me   "+
					" ON me.orgid=et.orgid  "+
					" WHERE et.orgid='"+orgid+"'  "+
					" AND et.id='"+enterparisId+"' "+
					" AND me.member_code="+memberCode
					+ ")";
			DBFactory.getDB().execute(sql);
			
			Utils.Log(tag,"为新用户:"+memberCode+"\t 初始化任务。SQL:"+sql );
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
