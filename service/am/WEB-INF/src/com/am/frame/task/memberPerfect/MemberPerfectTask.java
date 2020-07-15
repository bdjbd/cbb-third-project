package com.am.frame.task.memberPerfect;

import com.am.frame.task.IUserTask;
import com.am.frame.task.UserTaskAbstract;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
/**
 * 会员信息完善任务
 * @author Administrator
 *<br>当会员完善下面字段时，任务完成。<br>
 *登录名，昵称，姓名，身份证，性别，电话，邮箱地址，出生日期。
 *
 */
public class MemberPerfectTask  extends UserTaskAbstract implements IUserTask {

	/**会员信息完善任务 任务模板ID**/
	private static final String TO_MEMBER_PERFECT_TEMPID="226d9948-466b-4e1a-aae6-28214352aeab";
	
	/**完善会员信息**/
	public static final String PERFECT_MEMBER_INFO="完善会员信息";
	/**信息完善**/
	public static final String PERFECT_MEMBER_INFO_COMPLETE="信息完善";
	
	@Override
	public String getHtml() {
		
		String html="【任务说明】<br>";
		html+=getParamSetHtml(taskExplainParams);
		
		html+="<br>【任务完成情况】<br>";
		html+=getParamSetHtml(taskProgressParams);
		
		return html;
	}
	
	
	/**
	 * 初始化会员任务
	 * @param memberCode
	 * 
	 */
	public void init(String memberCode) {
		try{
			String sql=
					"SELECT ut.* FROM am_usertask AS ut                                "+
							"	LEFT JOIN am_EnterpriseTask AS et ON ut.enterprisetaskid=et.id   "+
							"	WHERE ut.memberid='"+memberCode+"'"+
							"	AND et.tasktemplateid='"+TO_MEMBER_PERFECT_TEMPID+"'      ";
			
			DB db=DBFactory.getDB();
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)){
				String taskid=map.getRow(0).get("id");
				super.init(memberCode, taskid);
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 会员信息完善更新任务
	 * @param  memberCode 会员编号
	 */
	public void updateTaskProgress(String memberCode){
		
		//判断任务是否完成，如果完成，不进入下面的业务判断
		if("1".equals(this.taskRunState)){
			//判断信息是否完善
			if(checkMemberInfoPerfect(memberCode)){
				this.taskRunState="0";
			}
			
			//更新任务参数到数据库
			saveTaskParams();
			
			if("0".equals(taskRunState)){
				//完成任务，执行任务奖励
				executeReward();
			}
		}
	}
	
	
	@SuppressWarnings("finally")
	private boolean checkMemberInfoPerfect(String memberCode){
		boolean result=false;
		
		try{
			
			if(!Checker.isEmpty(memberCode)){
			//查询会议信息
			String sql="SELECT member_code,wshop_name,nickname,mem_name,idcardno,gender,phone,email,birthday "
					+ " FROM ws_member "
					+ " WHERE member_code="+memberCode;
			
			DB db=DBFactory.getDB();
			MapList map=db.query(sql);
			
			if(!Checker.isEmpty(map)&&
					!Checker.isEmpty(map.getRow(0).get("wshop_name"))&&/**登录名**/
					!Checker.isEmpty(map.getRow(0).get("nickname"))&&/**昵称**/	
					!Checker.isEmpty(map.getRow(0).get("mem_name"))&&/**姓名**/
					!Checker.isEmpty(map.getRow(0).get("idcardno"))&&/**身份证**/
					!Checker.isEmpty(map.getRow(0).get("gender"))&&/**性别**/
					!Checker.isEmpty(map.getRow(0).get("phone"))&&/**电话**/
					!Checker.isEmpty(map.getRow(0).get("email"))&&/**邮箱地址**/
					!Checker.isEmpty(map.getRow(0).get("birthday"))){/**出生日期**/
				
				result=true;
			}
			
		}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}finally{
			return result;
		}
	}
}
