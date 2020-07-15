package com.am.frame.task.instance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;
/**
 * @author YueBin
 * @create 2016年3月17日
 * @version 
 * 说明:<br />
 * 会员信息完善任务。
 * 
 * 任务是否完成，根据任务参数配置的completeField来判断。
 * 
 * 
 */
public class MemberInfoPerfectionTask extends AbstractTask {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**会员信息完善任务**/
	public static String MEMBER_INFO_COMPLETE_TASK="MEMBER_INFO_COMPLETE_TASK";
	
	/**信用保证金账户缴费**/
	public static String INCREASE_PAYMENT_OF_DEPOSIT="INCREASE_PAYMENT_OF_DEPOSIT";
	
	/**邀请社员信息 任务**/
	public static String TASK_ECODE="MEMBER_INFO_COMPLETE_TASK";
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception{
		
		boolean result=false;
		//会员id
		String memberId=runTaskParams.getMemberId();
		
		String querySQL="SELECT * FROM am_member WHERE id=? ";
		
		MapList memberMap=db.query(querySQL,memberId ,Type.VARCHAR);
		
		if(!Checker.isEmpty(memberMap)){
			//1,根据任务参数，检查是否完成
			result=checkpPrfectMemberInfo(db,memberMap.getRow(0));
			if(result){
				updateUserTaskParams("是","COMPLATE_INFO");
			}
			
			//2,检查是否使用邀请码注册，如果没有使用邀请码注册，则任务无法完成
			//查询注册码
			String register_inv_code_id=memberMap.getRow(0).get("register_inv_code_id");
			
			if(!Checker.isEmpty(register_inv_code_id)){
				String sqlQueryInvcode="SELECT invitationcode FROM am_MemberInvitationCode WHERE id='"+register_inv_code_id+"' ";
				MapList map=db.query(sqlQueryInvcode);
				if(!Checker.isEmpty(map)){
					String invitationcode=map.getRow(0).get("invitationcode");
					updateUserTaskParams("是("+invitationcode+")","IS_INVCODE");
				}
			}else{
				//社员未使用邀请码注册，无发完成任务
				logger.info("社员未使用邀请码注册，无发完成任务，memberId："+runTaskParams.getMemberId());
				result=false;
			}
			
			//3,检查是否缴纳信用保证金，如果没有，则不奖励
			querySQL="SELECT COALESCE(pay_amount,0)/100 AS pay_amount FROM volunteers_pay_record  WHERE member_id=? ";
			MapList querMap=db.query(querySQL, memberId, Type.VARCHAR);
			if(Checker.isEmpty(querMap)){
				result= false;
			}else{
				double pay_amount=querMap.getRow(0).getDouble("pay_amount",-1);
				//缴纳保证金
				updateUserTaskParams(pay_amount+"",INCREASE_PAYMENT_OF_DEPOSIT);
				double volunteers_bai_amount=Var.getDouble("volunteers_bai_amount",0);
				
				//判断是否达到目标值
				if(pay_amount>=volunteers_bai_amount){
					result=result&&true;
				}
			}
			
			if(result){
				runTaskParams.getTargetMemberList().add(memberId);
			}
			
		}
		
		return result;
	}
	
	
	/**
	 * 根据任务参数，检查是否完成
	 * @param db
	 * @param member
	 * @return
	 * @throws JSONException 
	 */
	private boolean checkpPrfectMemberInfo(DB db, Row memberRow) throws Exception{
		
		boolean result=false;
		
		//检查参数 
		JSONObject taskObject=getTaskparames().getTaskObject();
		result=checkkPrefectMemberInfoOver(db, memberRow, taskObject);
		
		return result;
	}
	
	
	public boolean checkkPrefectMemberInfoOver(DB db, Row memberRow,JSONObject taskObject) throws Exception{
		boolean result=false;
		
		if(taskObject==null){
			throw new Exception("任务参数不正确，请检查任务参数！");
		}
		JSONArray completeFields=null;
		try{
			completeFields=taskObject.getJSONArray("completeField");
		}catch(JSONException e){
			e.printStackTrace();
			throw new Exception("任务参数不正确，请检查任务参数！");
		}
		
		for(int i=0;i<completeFields.length();i++){
			//数据库字段
			String fieldName=completeFields.getString(i).toLowerCase();
			
			if(Checker.isEmpty(memberRow.get(fieldName))){
				result=false;
				logger.info("用户任务完善未完成 ");
				break;
			}else{
				result=true;
			}
		}
		
		if(result){
			logger.info("用户任务完善完成 ");
		}
		
		return result;
	}
	
}
