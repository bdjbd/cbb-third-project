package com.ambdp.associationManage.action;

import com.am.app_plugins.precision_help.server.PrecisionHelpServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年6月15日
 *@version
 *说明：单位帮扶还款
 */
public class OrgHelpRepaymentAction extends DefaultAction{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		//出账帐号
		String outAccountCode  = SystemAccountClass.GROUP_CASH_ACCOUNT;
		//入账现金帐号
		String inCashAccount = SystemAccountClass.CONSUMER_ACCOUNT;
		String helpId="";
		String beHelpedId="";
		String money = "";
		String inmakers ="单位帮扶还款";
		Long helpAmount=0L;
		Long balance = 0L;
		
		String checkSQL="SELECT COALESCE(help_amount/ 100.00) AS money,* FROM mall_help_info  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		if(!Checker.isEmpty(map)){
			//帮扶者id
			helpId=map.getRow(0).get("help_id");
			//被帮扶者ID
			beHelpedId = map.getRow(0).get("be_helped_id");
			//帮扶金额(元)
			money = map.getRow(0).get("money");
			//帮扶金额
			helpAmount =Long.parseLong(map.getRow(0).get("help_amount"));
		}
		//查询账户余额
		String balanceSql = " SELECT balance "
				+ " FROM mall_account_info WHERE member_orgid_id='"+beHelpedId+"' "
				+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='GROUP_CASH_ACCOUNT') ";
		
		MapList balanceMap = db.query(balanceSql);
		if(!Checker.isEmpty(balanceMap)){
			balance = Long.parseLong(balanceMap.getRow(0).get("balance"));
		}
		//判断余额是否够还款金额
		if(balance<helpAmount){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("账户余额不足！");
		}else{
			VirementManager server = new VirementManager();
			//给被帮扶者信用消费账户还钱
			server.execute(db,beHelpedId,helpId,outAccountCode,inCashAccount,money,inmakers,inmakers,"",false);
			PrecisionHelpServer helpServer = new PrecisionHelpServer();
			//修改还款状态为已还款
			helpServer.updateHelpInfo(db,Id);
		}
		ac.getActionResult().setUrl("/am_bdp/mall_aorg_help_info.do?m=s&clear=am_bdp.mall_aorg_help_info.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}

}
		
