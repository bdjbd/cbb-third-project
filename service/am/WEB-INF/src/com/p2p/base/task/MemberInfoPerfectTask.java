package com.p2p.base.task;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.ParametersList;

/**
 * Author: Mike
 * 2014年7月17日
 * 说明：
 *  信息完善任务
 **/
public class MemberInfoPerfectTask extends UserTaskAbstract {

	public MemberInfoPerfectTask(){
		this.targetParame=new ParametersList();
	}
	@Override
	public ParametersList getTaskParam() {
		return targetParame;
	}

	/***
	 * 在用户修改信息时可以调用次接口
	 */
	@Override
	public boolean saveTaskParam() {
		try {
			
			DB db=DBFactory.getDB();
			String sql="SELECT * FROM ws_member WHERE member_code="+this.memberCode;
			MapList membMap=db.query(sql);
			//检查用户数据是否完善
			if(Checker.isEmpty(membMap))return false;
			if(Checker.isEmpty(membMap.getRow(0).get("nickname"))) return false;
			if(Checker.isEmpty(membMap.getRow(0).get("phone"))) return false;
			if(Checker.isEmpty(membMap.getRow(0).get("mem_name"))) return false;
			if(Checker.isEmpty(membMap.getRow(0).get("email"))) return false;
			//信息完善
			String type=this.targetParame.getValueOfName("奖品");
			String score=this.targetParame.getValueOfName("奖励");
			if("1".equals(type)){
				//1 积分
				//更新积分
				String updaeSQL="UPDATE ws_member SET integration=("
						+ "SELECT integration FROM ws_member WHERE member_code="
						+this.memberCode+")+"+score+" WHERE member_code="+this.memberCode;
				db.execute(updaeSQL);
				//更新任务状态
				String updateTaskStatus="UPDATE p2p_usertask SET taskrunstate=0 WHERE id='"+this.userTaskId+"'";
				db.execute(updateTaskStatus);
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public String getTaskCompletionStatus() {
		return null;
	}

	@Override
	public String getUserTaskInterface() {
		return null;
	}
	@Override
	public void init(String userID, String userTaskID) {
		// TODO Auto-generated method stub
		
	}

}
