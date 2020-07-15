package com.am.app_plugins.precision_help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins.precision_help.server.PrecisionHelpServer;
import com.am.frame.systemAccount.SystemAccountClass;
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
 *@create 2016年5月27日
 *@version
 *说明：单位帮扶确认帮扶WebApi
 *
 */
public class CompanyConfirmHelpIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		
			//帮扶类型
			String helpType = request.getParameter("helpType");
			//被帮扶者id
			String tomemberid = request.getParameter("tomemberid");
			//帮扶者id
			String memberid = request.getParameter("memberid");
			//帮扶金额
			String pay_money = request.getParameter("money");
			//帮扶者类型
			String roleid = request.getParameter("roleid");
			//被帮扶者表名
			String tabName = request.getParameter("tabName");
			//被帮扶者tabID
			String tabID = request.getParameter("tabID");
			//出账类型
			String outAccountCode  = SystemAccountClass.CASH_ACCOUNT;
			//入账现金帐号
			String inCashAccount = SystemAccountClass.GROUP_CASH_ACCOUNT;
			//入账自救金账户
			String inRiskSelfSavingAccoun  = SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT;
			//入账信誉卡账户
			String inCreditMarginAccount = SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT;
			//出账描述
			String oremakers = "单位帮扶";
			
			//现金账户获取帮扶金额比例
			String helpCashAmount = Var.get("unit_help_cash_amount");
			//信用保证金金额获取帮扶金额额度比例
			String helpCreditAmount = Var.get("unit_help_credit_amount");
			//单位帮扶自救金帐号获取帮扶金额比例
			String helpSaveOneself = Var.get("unit_help_save_oneself");
			
			//入账描述
			String inmakers = "单位帮扶入账";
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
			String xianjin="0";
			String zijiujin="0";
			String baozhengjin="0";
			resultJson = new JSONObject();
			
			//查询机构帐号余额
			String accountInfoSql = "select "+pay_money+"*"+helpCashAmount+" AS xianjin,"+pay_money+"*"+helpSaveOneself+" AS zijiujin,"+pay_money+"*"+helpCreditAmount+" AS baozhengjin";
			MapList	accountInfoList = db.query(accountInfoSql);
			if (!Checker.isEmpty(accountInfoList)) {
				//现金
				xianjin= accountInfoList.getRow(0).get("xianjin");
				//自救金
				zijiujin= accountInfoList.getRow(0).get("zijiujin");
				//保证金
				baozhengjin= accountInfoList.getRow(0).get("baozhengjin");
			}
				VirementManager server = new VirementManager();
				//扣除帮扶者现金帐号余额
				resultJson = server.execute(db,memberid,outAccountCode,pay_money,oremakers,out_pay_id,"");
				//给被帮扶者信用现金帐户充钱
				resultJson = server.execute(db,"",tomemberid,"",inCashAccount,xianjin,inmakers,"","",false);
				//给被帮扶者信用自救金帐户充钱
				resultJson = server.execute(db,"",tomemberid,"",inRiskSelfSavingAccoun,zijiujin,inmakers,"","",false);
				//给被帮扶者信用保证金帐户充钱
				resultJson = server.execute(db,"",tomemberid,"",inCreditMarginAccount,baozhengjin,inmakers,"","",false);
				
				PrecisionHelpServer helpServer = new PrecisionHelpServer();
				//分配分红徽章
//				helpServer.dividendRight(db,memberid,helpType,helpAmount,borrowAmount,enjoyMaxAmount);
				
				//给帮扶信息表中添加数据
				helpServer.addCompanyHelpInfo(db,helpType,tomemberid,memberid,pay_money,roleid);
				//被帮扶者帮扶次数+1
				helpServer.updateToMem(db,tabID,tabName);
				
				
				//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
				TaskEngine taskEngine=TaskEngine.getInstance();
				RunTaskParams params=new RunTaskParams();
				params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
				params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,MemberAuthorityBadgeTask.HELP_FARMER);
				params.pushParam(MemberAuthorityBadgeTask.HELP_FARMER, 1);
				params.pushParam(MemberAuthorityBadgeTask.MEMBER_TYPE,"2");
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				
				
				//获取志愿者账号提现资格任务 START
				params=new RunTaskParams();
				params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
				params.setMemberId(memberid);
				taskEngine.executTask(params);
				//获取志愿者账号提现资格任务 END
				
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
