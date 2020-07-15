package com.am.frame.task.instance;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.member.MemberManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.service.AccountService;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年8月3日
 * @version 
 * 说明:<br />
 * 获取志愿者账号提现资格任务
 * 
 * {
		"TaskDescription" : {},
		"TaskObject" : {
		 "INTVISM_AGRICULT_PROJ_MONEY":3000,
		"AID_STUDENT":0,
		"AID_FARMER":0,
		"SELF_INFO_COMPLATE":0
		"PURCHASE_ARTICLE_AMOUNT":10,
		},
		"TaskReward" : [
		{"rewName":"","rewardParams":{}}
		],
		"TaskExecution":{}
	}
 * 
 */
public class GetVolunteerAccountWithQualificationItask extends AbstractTask {

	/** 获取志愿者账号提现资格任务 任务编码**/
	public static String TASK_ECODE="GET_VOLUNTEER_ACCOUNT_WITH_QUALIFICATION";
	
	/**投资农业项目结额，单位元**/
	public static String INTVISM_AGRICULT_PROJ_MONEY="INTVISM_AGRICULT_PROJ_MONEY";
	
	/**资助大学生数量**/
	public static String AID_STUDENT="AID_STUDENT";
	
	/**资助农民数量**/
	public static String AID_FARMER="AID_FARMER";
	
	/**本人资料完善**/
	public static String SELF_INFO_COMPLATE="SELF_INFO_COMPLATE";
	
	/**购买科普培训文章**/
	public static String PURCHASE_ARTICLE_AMOUNT="PURCHASE_ARTICLE_AMOUNT";
	
	/**交纳保证金  废弃 2016年11月21日**/
	public static String INCREASE_PAYMENT_OF_DEPOSIT="INCREASE_PAYMENT_OF_DEPOSIT";
	
	/**大众创业**/
	public static String START_BUSINESS="START_BUSINESS";
	
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams, DB db)
			throws Exception {
		
		String meberId=runTaskParams.getMemberId();
		String memberName="";
		String memberType="";//1=消费者社员;2=单位消费者社员;3=生产者社员
		
		MemberManager mm=new MemberManager();
		MapList memberMap=mm.getMemberById(meberId, db);
		if(!Checker.isEmpty(memberMap)){
			memberName=memberMap.getRow(0).get("membername");
			memberType=memberMap.getRow(0).get("member_type");
		}
		
		boolean reuslt=true;
		
		//任务目标模版
		JSONObject taskObject=null;
		if(this.taskparames!=null){
		 taskObject=this.taskparames.getTaskObject();
		}
		
		//用户当前任务参数,不同的触发点，有不同的计算,任务会更新此对象
		if(this.taskparames==null){
			logger.error("执行任务失败,任务名称:"+this.taskName+"\n任务信息："+this.toString());
			reuslt=false;
		}
		
		//1,检查投资农业项目的资金
		String querySQL="SELECT sum( stock_price*buy_number/100) AS total_price  "
				+ " FROM mall_buy_stock_record WHERE buyer_id=?  AND status<>1 ";
		MapList querMap=db.query(querySQL, meberId, Type.VARCHAR);
		
		if(!Checker.isEmpty(querMap)){
			
			//投资金额
			long totalPrice=querMap.getRow(0).getLong("total_price",0);
			//任务目标金额
			long intvismAgricultProjMoney=0;
			if(taskObject!=null&&taskObject.has(INTVISM_AGRICULT_PROJ_MONEY)){
				intvismAgricultProjMoney=taskObject.getLong(INTVISM_AGRICULT_PROJ_MONEY);
			}
			
			
			//更新任务字段中的数据
			updateUserTaskParams(totalPrice+"","INTVISM_AGRICULT_PROJ_MONEY");
			
			//如果投资金额小于目标金额，则返回false，负责检查下一个
			if(intvismAgricultProjMoney>totalPrice){
				reuslt=reuslt&&false;
			}else{
				reuslt=reuslt&&true;
			}
			
		}else{
			reuslt= reuslt&&false;
		}
		
		//2,检查资助大学生数量
		querySQL="SELECT count(*) AS count FROM mall_college_helper WHERE member_id=? ";
		querMap=db.query(querySQL, meberId, Type.VARCHAR);
		if(Checker.isEmpty(querMap)){
			reuslt= reuslt&&false;
		}else{
			
			//实际资助大学生数量 
			long count=querMap.getRow(0).getLong("count", 0);
			
			//更新任务字段中的数据
			updateUserTaskParams(count+"","AID_STUDENT");
			
			if(taskObject!=null&&taskObject.has(AID_STUDENT)&&count<taskObject.getLong(AID_STUDENT)){
				reuslt= reuslt&&false;
			}else{
				reuslt=reuslt&&true;
			}
		}
		
		//3,检查资助农民数量
		//如果是生产者，则直接返回true
		if("3".equals(memberType)){
			//生产者无法帮扶生产者，此处直接返回true
			reuslt=reuslt&&true;
			//更新任务字段中的数据
			updateUserTaskParams("生产者无需资助","AID_FARMER");
		}else{
			querySQL="SELECT  count(*) AS count  FROM mall_help_info WHERE  help_id=? ";
			
			querMap=db.query(querySQL, meberId, Type.VARCHAR);
			if(Checker.isEmpty(querMap)){
				reuslt= reuslt&&false;
			}else{
				//已经资助农民的数量
				long count=querMap.getRow(0).getLong("count", 0);
				
				//更新任务字段中的数据
				updateUserTaskParams(count+"","AID_FARMER");
				
				
				if(taskObject!=null&&taskObject.has(AID_FARMER)&&count<taskObject.getLong(AID_FARMER)){
					reuslt= reuslt&&false;
				}else{
					reuslt=reuslt&&true;
				}
			}
		}
		
		
		
		//4,本人资料完善
		querySQL="SELECT ut.* FROM am_UserTask AS ut "
				+ " LEFT JOIN am_EnterpriseTask AS et ON et.id=ut.entertaskid "
				+ " WHERE et.task_code='"+MemberInfoPerfectionTask.MEMBER_INFO_COMPLETE_TASK+"'"
				+ "  AND ut.memberid=?  ";
		
		querMap=db.query(querySQL, meberId, Type.VARCHAR);
		if(Checker.isEmpty(querMap)){
			reuslt= false;
		}else{
			String taskparame=querMap.getRow(0).get("taskparame");
			
			JSONObject taskParams=new JSONObject(taskparame);
			JSONObject mTaskObject=taskParams.getJSONObject("TaskObject");
			
			String memberId=runTaskParams.getMemberId();
			
			querySQL="SELECT * FROM am_member WHERE id=? ";
			memberMap=db.query(querySQL,memberId ,Type.VARCHAR);
			
			boolean isComp=new MemberInfoPerfectionTask().checkkPrefectMemberInfoOver(db, memberMap.getRow(0), mTaskObject);
			
			if(isComp){
				//更新任务字段中的数据
				updateUserTaskParams("完成","SELF_INFO_COMPLATE");
				reuslt=reuslt&&true;
			}else{
				reuslt=reuslt&&false;
			}
		}
		
		//5，购买科普培训资料金额PURCHASE_ARTICLE_AMOUNT
		querySQL="SELECT COALESCE(journal_fee,0)/100 AS actual_journal_fee,COALESCE(journal_fee,0)/100-"
				+ " (SELECT vvalue::double precision FROM avar WHERE vid='monthly_journal_fee' LIMIT 1) AS journal_fee  "
				+ " FROM am_member WHERE id=? ";
		
		querMap=db.query(querySQL, meberId, Type.VARCHAR);
		if(Checker.isEmpty(querMap)){
			reuslt= false;
		}else{
			double journal_fee=querMap.getRow(0).getDouble("journal_fee", -1);
			double actual_journal_fee=querMap.getRow(0).getDouble("actual_journal_fee", -1);
			
			//更新实际额度
			updateUserTaskParams(actual_journal_fee+"",PURCHASE_ARTICLE_AMOUNT);
			
			if(journal_fee>=0){//已经购买的金额大于等于变量设置的金额，则此项任务条件完成
				reuslt=reuslt&&true;
			}else{
				reuslt=reuslt&&false;
			}
		}
		
		//6，交纳保证金   INCREASE_PAYMENT_OF_DEPOSIT
		//检查是否缴纳保证金金额
//		querySQL="SELECT COALESCE(pay_amount,0)/100 AS pay_amount FROM volunteers_pay_record  WHERE member_id=? ";
//		querMap=db.query(querySQL, meberId, Type.VARCHAR);
//		if(Checker.isEmpty(querMap)){
//			reuslt= false;
//		}else{
//			double pay_amount=querMap.getRow(0).getDouble("pay_amount",-1);
//			//缴纳保证金
//			updateUserTaskParams(pay_amount+"",INCREASE_PAYMENT_OF_DEPOSIT);
//			double volunteers_bai_amount=Var.getDouble("volunteers_bai_amount",0);
//			
//			//判断是否达到目标值
//			if(pay_amount>=volunteers_bai_amount){
//				reuslt=reuslt&&true;
//			}else{
//				reuslt=reuslt&&false;
//			}
//		}
		
		//获取任务出发点
		//资助农民
		AccountService as=new AccountService();
		String remarks="";
		String changePayment=runTaskParams.getParams(AID_FARMER);
		if(!Checker.isEmpty(changePayment)&&!"null".equals(changePayment)){
			remarks=memberName+"资助农民"+VirementManager.changeF2Y(changePayment)+"元，提升志愿者服务账号提现额度。";
			as.updateAvailableCashAmount(db, changePayment,meberId,SystemAccountClass.VOLUNTEER_ACCOUNT, remarks);
		}
		//资助大学生
		changePayment=runTaskParams.getParams(AID_STUDENT);
		if(!Checker.isEmpty(changePayment)&&!"null".equals(changePayment)){
			remarks=memberName+"资助大学生"+VirementManager.changeF2Y(changePayment)+"元，提升志愿者服务账号提现额度。";
			as.updateAvailableCashAmount(db, changePayment,meberId,SystemAccountClass.VOLUNTEER_ACCOUNT, remarks);
		}
		//投资项目 单位分
		changePayment=runTaskParams.getParams(INTVISM_AGRICULT_PROJ_MONEY)+"";
		if(!Checker.isEmpty(changePayment)&&!"null".equals(changePayment)){
			remarks=memberName+"投资农业项目"+VirementManager.changeF2Y(changePayment)+"元，提升志愿者服务账号提现额度。";
			as.updateAvailableCashAmount(db, changePayment,meberId,SystemAccountClass.VOLUNTEER_ACCOUNT, remarks);
		}
		
		//大众创业
		changePayment=runTaskParams.getParams(START_BUSINESS);
		if(!Checker.isEmpty(changePayment)&&!"null".equals(changePayment)){
			remarks=memberName+"大众创业"+VirementManager.changeF2Y(changePayment)+"元，提升志愿者服务账号提现额度。";
			as.updateAvailableCashAmount(db, changePayment,meberId,SystemAccountClass.VOLUNTEER_ACCOUNT, remarks);
		}
		
		
		return reuslt;
	}
	
	
	/***
	 * 更新任务参数
	 * @param value 值
	 * @param valeKey 关键字  常亮设置
	 */
	@Override
	public void updateUserTaskParams(String value, String valeKey) throws JSONException {
		super.updateUserTaskParams(value, valeKey);
	}
	
	
	
}
