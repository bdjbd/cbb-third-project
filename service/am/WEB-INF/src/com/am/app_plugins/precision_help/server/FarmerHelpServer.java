package com.am.app_plugins.precision_help.server;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.member.MemberManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
/**
 * 资助农民后台业务
 * @param db 
 * @param helpType 帮扶类型
 * @param tomemberid 被帮扶者id
 * @param memberid 帮扶者id
 * @param pay_money 帮扶金额
 * @param roleid 帮扶者类型
 * @param creditCardAaccount 信誉卡账户
 * @param helpCreditAmount 信用保证金金额
 * @param helpProducerCreditCardAmount 生产者信誉卡额度
 * @param helpAmount 帮赠分红金额
 * @param borrowAmount 帮借单次额度
 * @param enjoyMaxAmount 经营分红最大额度
 * @param tabname 表名
 * @param totalPayment 帮扶者支付总金额
 * 
 * @author 
 *
 */
public class FarmerHelpServer {
	
	public void help(DB db, String helpType, String tomemberid, String memberid, String pay_money, String roleid,
			String creditCardAaccount, String helpCreditAmount, String helpProducerCreditCardAmount, Double helpAmount,
			Double borrowAmount, Double enjoyMaxAmount, String tabname, String antiRiskSelfAccountAmount,String totalPayment,String outAccountCode) throws JDBCException, JSONException {
		JSONObject businessJS;
		String inmakers;
		//获取帮扶者信息
		String membername = "";
		MemberManager mm=new MemberManager();
		MapList memberMap=mm.getMemberById(memberid, db);
		//判断帮扶者是否为组织机构 2016年12月22日添加
		if(!Checker.isEmpty(memberMap)){
			membername = memberMap.getRow(0).get("membername");
		}else{
			String nameSQL = "select u_name from service_mall_info where orgid = '"+ memberid+ "'";
			MapList namesql = db.query(nameSQL);
			if(namesql.size()>0){
				membername = namesql.getRow(0).get("u_name");			
			}
		}
		
		
		VirementManager server = new VirementManager();
		
		/**
		 *			  a:给被帮扶者现金账号入账 现金账号帮扶金额，现金账号入账200。入账记录为：获得xxx帮扶。
		 *            b:信誉卡账号入账1000，并且提升信誉卡借款额度，入账记录：xxx帮扶，信誉卡入账1000。
		 *            c:信用保证金账号入账1000，入账记录为：xxx帮扶，信用保证金1000元。
		 *            d:信用保证金账号入账1000，入账记录为：xxx帮扶，投资村头冷库使用信用证金。
		 *            e:抗分先自救金账号100，入账记录为：xxxx帮扶，抗分险自救金账户入账100元。
		 ***/
		
		
		//a 现金账号帮扶金额
		String helpCashAmount=Var.get("help_member_cash_account_amount");
		// 给被帮扶者信用保证金帐户充钱
		inmakers="获得"+membername+"帮扶。";
		businessJS = server.execute(db, "", tomemberid, "", SystemAccountClass.CASH_ACCOUNT, 
				helpCashAmount, inmakers, "", "",
				false);
		
		//c:信用保证金账号入账1000，入账记录为：xxx帮扶，信用保证金1000元。
		inmakers="获得"+membername+"帮扶,信用保证金"+helpCreditAmount+"元。";
		server.execute(db, "", tomemberid, "", SystemAccountClass.CREDIT_MARGIN_ACCOUNT,
				helpCreditAmount, inmakers, "", "",false);
		
		//d:信用保证金账号入账1000，入账记录为：xxx帮扶，投资村头冷库使用信用证金。
		String helpUseColdStorageAmount=Var.get("help_use_cold_storage_amount");
		inmakers="获得"+membername+"帮扶,投资村头冷库使用信用证金"+helpUseColdStorageAmount+"元。";
		server.execute(db, "", tomemberid, "", SystemAccountClass.CREDIT_MARGIN_ACCOUNT,
				helpUseColdStorageAmount, inmakers, "", "",false);
		
		//给被帮扶者信誉卡增加额度
		//b 信誉卡账号入账1000，并且提升信誉卡借款额度，入账记录：xxx帮扶，信誉卡入账1000.
		inmakers="获得"+membername+"帮扶，信誉卡入账"+helpProducerCreditCardAmount+"元";
		PrecisionHelpServer helpServer = new PrecisionHelpServer();
		helpServer.updateTomember(db, tomemberid, creditCardAaccount, helpProducerCreditCardAmount,inmakers);
		
		//给被帮扶者抗风险自救金账户入账  2016年11月20日
		//e:抗风险自救金账户100，入账记录为：xxxx帮扶，抗分险自救金账户入账100元。
		inmakers="获得"+membername+"帮扶，抗风险自救金入账"+helpProducerCreditCardAmount+"元";
		businessJS = server.execute(db, "", tomemberid, "", SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT, 
				antiRiskSelfAccountAmount, inmakers, "", "",
				false);
		
		
		//添加帮扶信息
		helpServer.addHelpInfo(db, helpType, tomemberid, memberid, pay_money, roleid);
		// 给被帮扶者投资农业  业务取消 2016年10月21日
		//helpServer.farmProject(db, tomemberid, memberid, helpAmountStorage);

		// 给帮扶信息表中添加数据
//		helpServer.addHelpInfo(db, helpType, tomemberid, memberid, pay_money, roleid);

		// 被帮扶者帮扶次数+1
		helpServer.updateToMem(db, tomemberid, tabname);
		
		//判断帮扶者是否为组织机构 2016年12月22日添加
		if(!Checker.isEmpty(memberMap)){
			
			// 分配分红徽章
			helpServer.dividendRight(db, memberid, helpType, helpAmount, borrowAmount, enjoyMaxAmount);
				
				
			// 理性农业 会员权限徽章任务，具体权限参考需求 社员分类划分
			TaskEngine taskEngine = TaskEngine.getInstance();
			RunTaskParams params = new RunTaskParams();
			params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); // 会员权限徽章任务
			params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT, MemberAuthorityBadgeTask.HELP_FARMER);
			params.pushParam(MemberAuthorityBadgeTask.HELP_FARMER, "1");
			params.pushParam(MemberAuthorityBadgeTask.MEMBER_TYPE, "2");
			params.setMemberId(memberid);
			taskEngine.executTask(params);
	
			// 获取志愿者账号提现资格任务 START
			//查询判断是否是志愿者账户支付，如果是志愿者账户支付则更新志愿者服务账户提现额度
	//		if("VOLUNTEER_ACCOUNT".equals(outAccountCode))
	//		{
				params = new RunTaskParams();
				params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
				params.pushParam(GetVolunteerAccountWithQualificationItask.AID_FARMER,VirementManager.changeY2F(totalPayment)+"");
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				// 获取志愿者账号提现资格任务 END
		}
		
	}
}
