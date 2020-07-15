package com.am.frame.task.instance;

import java.util.ArrayList;
import java.util.List;

import com.am.frame.member.MemberManager;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月16日
 * @version 
 * 说明:<br />
 * 推广任务
 * 推广任务，
 * 1，根据推广码查询上级会员。
 * 2，执行推广奖励
 */
public class PromotionTask extends AbstractTask  {
	
	
	//ConsumerInterestTask
	/**企业任务编码**/
	public static final String TASK_ECODE="PromotionTask";
	
	/**推广任务推广码**/
	public static final String INVITATION_CODE="PromotionTask.INVITATION_CODE";
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception{
		/**
		 * 推广任务算法
		 * 1，获取推广码
		 * 2，查询推广码属性
		 * 3，给推广人员进行奖励
		 */
		
		//获取推广码
		String invCode=runTaskParams.getParams(INVITATION_CODE);
		
		//查询邀请码信息 需要判断有效的邀请码
		String queryInvSQL="SELECT id,am_memberid,orgid,invitationcode,ic_role "+
						" FROM am_MemberInvitationCode  "+
						" WHERE invitationcode='"+invCode+"' "+
						" AND am_memberid IN (select id FROM am_member WHERE phone='"+invCode+"' )";
		
		MapList map=db.query(queryInvSQL);
		
		if(!Checker.isEmpty(map)){
			
			for(int i=0;i<map.size();i++){
				runTaskParams.addRewardTargetMember(map.getRow(i).get("am_memberid"));
				
				//邀请码ID
				String invId=map.getRow(i).get("id");
				
				//更新社员 注册时使用的邀请码id
				String updateSQL="UPDATE am_member SET register_inv_code_id=? WHERE id=? ";
				db.execute(updateSQL,new String[]{
						invId,runTaskParams.getMemberId()
				},new int[]{
						Type.VARCHAR,Type.VARCHAR
				});
			}
			
		}

		
		
		//给会员增加上下级关系
		addMemberUpId(db,runTaskParams);
		
		boolean result=false;
		//邀请码不为空，执行任务奖励
		if(!Checker.isEmpty(invCode)){
			result=true;
		}
		
		//理性农业 生产者邀请码处理
		MemberManager mMamager=new MemberManager();
		mMamager.userInvCode(db,invCode,runTaskParams.getMemberId());
		
		return result;
	}
	
	/**
	 * 给会员增加上下级关系
	 * @param db
	 * @param runTaskParams
	 * @throws JDBCException 
	 */
	private void addMemberUpId(DB db, RunTaskParams runTaskParams) throws JDBCException {
		
		//给会员增加上下级关系，上下级关系为 上级会员id+，+上级会员的UPid
		
		StringBuilder insertSQL=new StringBuilder();
		insertSQL.append("UPDATE am_Member SET  ");
		insertSQL.append(" UpID=(SELECT id||','||COALESCE(UpID,'') FROM am_Member WHERE id=? ) ");
		insertSQL.append(" WHERE id=?  ");
		
		List<String[]> sqlParams=new ArrayList<String[]>();
		
		if(runTaskParams.getTargetMemberList().size()>0){
			
			//下级社员id，即当初注册社员的ID
			String sub_member_id=runTaskParams.getMemberId();
			
			//邀请码所有者社员，即当前注册社员的上级社员ID
			String memberId=runTaskParams.getTargetMemberList().get(0);
			
			//防止邀请社员邀请码为自己号码，如果社员id相同，不执行分级处理
			if(memberId!=null&&memberId.equals(sub_member_id)){
				
				logger.info("社员上下级关系，社员ID相同,memberId:"+memberId+"\t sub_member_id:"+sub_member_id);
				return;
			}
			
			int level=1;
			
			sqlParams.add(
					new String[]{
							memberId,//邀请码所有者会员ID
							sub_member_id});// 使用邀请码会员id
			
			//1,生成当初注册用户的直接上级
			Table table=new Table("am_bdp", "AM_MEMBER_DISTRIBUTION_MAP");
			TableRow inserTR=table.addInsertRow();
			//社员id
			inserTR.setValue("member_id",memberId);
			//下级社员id
			inserTR.setValue("sub_member_id", sub_member_id);
			//层级
			inserTR.setValue("level", level);//直接下级，为1级社员
			db.save(table);
			
			
			//2,递归，生成当前注册用户的非直接上级与当前注册用户的关系。
			createLevel(db,memberId,sub_member_id,2);
		}

		db.executeBatch(insertSQL.toString(),
				sqlParams,
				new int[]{Type.VARCHAR,Type.VARCHAR});
		
		
	}
	
	
	private void createLevel(DB db, String memberId, String sub_member_id,int level) throws JDBCException {
		
		//查询memberId的所有直接上级，然后给所哟直接上级生成下级的数据
		String querySQL="SELECT * FROM am_member_distribution_map "
				+ " WHERE sub_member_id=? AND level=1 ";
		
		MapList leveOneMap=db.query(querySQL, memberId, Type.VARCHAR);
		
		if(!Checker.isEmpty(leveOneMap)){
			Table table=new Table("am_bdp", "AM_MEMBER_DISTRIBUTION_MAP");
			
			//1,生成当初注册用户的直接上级
			TableRow inserTR=table.addInsertRow();
			//社员id
			inserTR.setValue("member_id",leveOneMap.getRow(0).get("member_id"));
			//下级社员id
			inserTR.setValue("sub_member_id", sub_member_id);
			//层级
			inserTR.setValue("level", level);//直接下级，为1级社员
			db.save(table);
			
			//递归获取
			createLevel(db, leveOneMap.getRow(0).get("member_id"), sub_member_id, level+1);
		}
		
	}

	/**
	 * 查询memberId的直接上级，返回上级的memberId，如果找不到，则返回null
	 * @param db DB
	 * @param memberId 社员邀请码
	 * @return  如果找不到，则返回null.
	 * @throws JDBCException 
	 */
	public String getDirectlyInvCode(DB db,String memberId) throws JDBCException{
		String result=null;
		
		//1,查询memberId的直接上级
		String querySQL="SELECT * FROM am_MemberInvitationCode "
				+ "	WHERE id IN ( "
				+ " 	SELECT register_inv_code_id "
				+ "  	FROM am_Member "
				+ "		WHERE id=? "
				+ " )";
		
		MapList dirMap=db.query(querySQL, memberId, Type.VARCHAR);
		
		if(!Checker.isEmpty(dirMap)){
			result=dirMap.getRow(0).get("am_memberid");
		}
		
		return result;
	}
	
	
}
