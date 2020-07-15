package com.am.frame.task.instance;

import com.am.frame.member.MemberManager;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.am.frame.task.task.ITask;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * 用户注册成功后，奖励空间
 * @author YueBin
 *
 */
public class RegistTask extends AbstractTask implements ITask {

	/**
	 * 更新用户任务为已完成。
	 * 如果返回true，执行奖励规则
	 */
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception {
		
		db=DBFactory.getDB();
		
		String udpateSQL="UPDATE am_usertask SET taskrunstate='0' WHERE id='"+this.id+"' ";
		
		runTaskParams.addRewardTargetMember(runTaskParams.getMemberId());
		
		db.execute(udpateSQL);
		
		return true;
	}
	

	@Override
	public boolean addTaskByUserId(String memberId, String entTaskId, DB db)
			throws JDBCException {
		//1,向用户任务表中插入数据
		boolean result=super.addTaskByUserId(memberId, entTaskId, db);
		//2,给会员生存邀请码
		createMemberInvCode(db,memberId);
		return result;
	}
	
	
	
	/**
	 * 给会员生存邀请码
	 * @param db  DB 
	 * @param memberId 会员ID
	 */
	private void createMemberInvCode(DB db, String memberId) throws JDBCException{
		
		//查询社员类型，如果为生产者社员，为其生存邀请码
		try {
			String querSQL="SELECT * FROM am_member WHERE id=? ";
			MapList map=db.query(querSQL, memberId, Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				String mrole=map.getRow(0).get("mrole");
				new MemberManager().createInvitationCode(db, memberId, mrole);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
