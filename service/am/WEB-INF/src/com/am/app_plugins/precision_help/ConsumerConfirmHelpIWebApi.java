package com.am.app_plugins.precision_help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins.precision_help.server.PrecisionHelpServer;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月24日
 *@version
 *说明：消费者帮扶确认帮扶WebApi
 */
public class ConsumerConfirmHelpIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		//帮扶类型
		String helpType = request.getParameter("helpType");
		//被帮扶者id
		String tomemberid = request.getParameter("tomemberid");
		//帮扶者id
		String memberid = request.getParameter("memberid");
		//帮扶金额
		Long money = Long.parseLong(request.getParameter("money"));
		String pay_money = request.getParameter("money");
		//帮扶者类型
		String roleid = request.getParameter("roleid");
		Long balance = 0L;
		//出账类型
		String outAccountCode  = "CASH_ACCOUNT";
		//入账类型
		String inAccountCode  = "CREDIT_MARGIN_ACCOUNT";
		//信誉卡账户
		String creditCardAaccount = "CREDIT_CARD_ACCOUNT";
		//出账描述
		String oremakers = "消费者帮扶";
		
		//信用保证金金额
		String helpCreditAmount = Var.get("help_credit_amount");
		
		//生产者信誉卡额度
		String helpProducerCreditCardAmount = Var.get("help_producer_credit_card_amount");
		
		//投资村头冷库金额
		String helpAmountStorage = Var.get("help_amount_storage");
		
		//入账描述
		String inmakers = "消费者帮扶入账";
		
		//帮赠分红金额比例
		String enjoySingleHelpAmount = Var.get("enjoy_single_help_amount");
		Double helpAmount = Double.parseDouble(enjoySingleHelpAmount);
		
		//帮借单次额度
		String enjoySingleBorrowAmount = Var.get("enjoy_single_borrow_amount");
		Double borrowAmount = Double.parseDouble(enjoySingleBorrowAmount);
		String MaxAmount = Var.get("enjoy_max_amount");
		
		//经营分红最大额度
		Double enjoyMaxAmount = Double.parseDouble(MaxAmount);
		String out_pay_id= "";
		String business = "";
		String tabname = "am_member";
		resultJson = new JSONObject();
		
		try {
			//查询机构帐号余额
			String accountInfoSql = "SELECT balance FROM  mall_account_info "
					+ " WHERE member_orgid_id='"+memberid+"'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='CASH_ACCOUNT')";
			MapList	accountInfoList = db.query(accountInfoSql);
			if (!Checker.isEmpty(accountInfoList)) {
				//帐号余额
				balance= Long.parseLong(accountInfoList.getRow(0).get("balance"));
			}
			if(money>balance){
				resultJson.put("code", "101");
				resultJson.put("msg", "账户余额不足！");
			}else{
				VirementManager server = new VirementManager();
				//扣除帮扶者现金帐号余额
				resultJson =server.execute(db,memberid,outAccountCode,pay_money,oremakers,out_pay_id,business);
				//给被帮扶者信用保证金帐户充钱
				resultJson =server.execute(db,"",tomemberid,"",inAccountCode,helpCreditAmount,inmakers,"","",false);
				
				//给被帮扶者信誉卡增加额度
				PrecisionHelpServer helpServer = new PrecisionHelpServer();
				helpServer.updateTomember(db,tomemberid,creditCardAaccount,helpProducerCreditCardAmount,"");
				
				//帮扶者购买农业项目
				helpServer.farmProject(db,tomemberid,memberid,helpAmountStorage);
				
				//分配分红徽章
				helpServer.dividendRight(db,memberid,helpType,helpAmount,borrowAmount,enjoyMaxAmount);
				
				//给帮扶信息表中添加数据
				helpServer.addHelpInfo(db,helpType,tomemberid,memberid,pay_money,roleid);
				
				//被帮扶者帮扶次数+1
				helpServer.updateToMem(db,tomemberid,tabname);
				
				
				//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
				TaskEngine taskEngine=TaskEngine.getInstance();
				RunTaskParams params=new RunTaskParams();
				params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
				params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,MemberAuthorityBadgeTask.HELP_FARMER);
				params.pushParam(MemberAuthorityBadgeTask.HELP_FARMER,"1");
				params.pushParam(MemberAuthorityBadgeTask.MEMBER_TYPE,"2");
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				
				//获取志愿者账号提现资格任务 START
				params=new RunTaskParams();
				params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				//获取志愿者账号提现资格任务 END
				
			}
		} catch (Exception e) {
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
		
		
	
		
		return resultJson.toString();
	}

	
	

}
