package com.am.app_plugins.precision_help;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins.precision_help.server.FarmerHelpServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 说明：消费者帮扶确认帮扶回调功能 
 * @author yuebin
 * 
 * 消费者帮扶: 
 * 1,给被帮扶者1000元消费帐户 
 * 2,帮助被帮扶者投资村头冷库1000元，投资人为农户
 * 3,给被帮扶者的信誉保证金增加1000元。
 * 
 *2016-10-21  新需求
 *1，帮扶生产者社员金额=帮扶金额+帮扶现金账号帮扶金额
 *2，金额分配：a:给被帮扶者现金账号入账 现金账号帮扶金额，现金账号入账200。
 *            b:信誉卡账号入账1000，并且提升信誉卡借款额度，入账记录：xxx帮扶，信誉卡入账1000。
 *            c:信用保证金账号入账1000，入账记录为：xxx帮扶，信用保证金1000元。
 *            d:信用保证金账号入账1000，入账记录为：xxx帮扶，投资村头冷库使用信用证金。
 *            e:抗风险自救金账户100，入账记录为：xxxx帮扶，抗分险自救金账户入账100元。
 * 3，帮扶现金账号帮扶金额 ，后台变量：help_member_cash_account_amount
 *    消费者帮扶生产者使用村头冷库信用保证金金额，后台变量：help_use_cold_storage_amount
 *    信用保证金金额，后台变量：help_credit_amount
 *    生产者信誉卡额度，后台变量： help_producer_credit_card_amount 
 *    抗分险自救金账号金额，后台变量：help_member_anti_risk_self_account_amount
 * 
 *注：调用此接口时，帮扶者的现金账号金额已经被扣除
 */
public class ConsumerConfirmHelpBusinessCallBack extends AbstraceBusinessCallBack {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		logger.info("消费者帮扶确认帮扶回调功能,业务参数：" + business);

		JSONObject result = new JSONObject();

		// 检查业务处理
		if (checkTradeState(id, business, db, type) && !checkProcessBuissnes(id, db)) {
			// 业务处理类
			JSONObject businessJS = new JSONObject(business);
			// business:{
			// 'helpType':helpType,//帮扶类型
			// 'tomemberid':tomemberid, // 被帮扶者id
			// 'roleid':roleid,// 帮扶着类型
			// 'pay_id':uuid,
			// 'memberid':$scope.pageData.memberid,//帮扶者id
			// 'inMemberid':$scope.pageData.loginaccount,//入账社员账号id
			// "orders":uuid,
			// "paymoney":money, //帮扶金额
			// "success_call_back":'com.am.app_plugins.precision_help.ConsumerConfirmHelpBusinessCallBack'
			// }

			// * 1,给被帮扶者1000元消费帐户
			// * 2,帮助被帮扶者投资村头冷库1000元，投资人为农户
			// * 3,给被帮扶者的信誉保证金增加1000元。

			// 帮扶类型
			String helpType = businessJS.getString("helpType");
			// 被帮扶者id
			String tomemberid = businessJS.getString("tomemberid");
			// 帮扶者id
			String memberid = businessJS.getString("memberid");
			// 帮扶金额
			Long money = Long.parseLong(businessJS.getString("money"));
			String pay_money = businessJS.getString("money");
			// 帮扶者类型
			String roleid = businessJS.getString("roleid");
			Long balance = 0L;
			// 出账类型
			String outAccountCode = "CASH_ACCOUNT";
			// 入账类型
			String inAccountCode = "CREDIT_MARGIN_ACCOUNT";
			// 信誉卡账户
			String creditCardAaccount =SystemAccountClass.CREDIT_CARD_ACCOUNT ;//"CREDIT_CARD_ACCOUNT";

			// 出账描述
			String oremakers = "消费者帮扶";

			// 信用保证金金额
			String helpCreditAmount = Var.get("help_credit_amount");

			// 生产者信誉卡额度
			String helpProducerCreditCardAmount = Var.get("help_producer_credit_card_amount");

			// 投资村头冷库金额
			String helpAmountStorage = Var.get("help_amount_storage");
			
			//抗风险自救金账户金额
			String antiRiskSelfAccountAmount=Var.get("help_member_anti_risk_self_account_amount");

			// 入账描述
			String inmakers = "消费者帮扶入账";

			// 帮赠分红金额比例
			String enjoySingleHelpAmount = Var.get("enjoy_single_help_amount");
			Double helpAmount = Double.parseDouble(enjoySingleHelpAmount);

			// 帮借单次额度
			String enjoySingleBorrowAmount = Var.get("enjoy_single_borrow_amount");
			Double borrowAmount = Double.parseDouble(enjoySingleBorrowAmount);
			String MaxAmount = Var.get("enjoy_max_amount");

			// 经营分红最大额度
			Double enjoyMaxAmount = Double.parseDouble(MaxAmount);
			String out_pay_id = "";
			String tabname = "am_member";

			// 查询机构帐号余额
			String accountInfoSql = "SELECT balance FROM  mall_account_info " + " WHERE member_orgid_id='" + memberid
					+ "'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='CASH_ACCOUNT')";
			MapList accountInfoList = db.query(accountInfoSql);
			if (!Checker.isEmpty(accountInfoList)) {
				// 帐号余额
				balance = Long.parseLong(accountInfoList.getRow(0).get("balance"));
			}
			if (money > balance||balance<0) {
				businessJS.put("code", "101");
				businessJS.put("msg", "账户余额不足！");
			} else {
				FarmerHelpServer server = new FarmerHelpServer();
				String selesql = "select msac.sa_code from mall_trade_detail as mtd "
						+ "left join mall_account_info  as mai on mai.id = mtd.account_id "
						+ "left join mall_system_account_class as msac on msac.id = mai.a_class_id where mtd.id= '"+id+"'";
				MapList listss = db.query(selesql);
				server.help(db, helpType, tomemberid, memberid, pay_money, roleid, creditCardAaccount, helpCreditAmount,
						helpProducerCreditCardAmount, helpAmount, borrowAmount, enjoyMaxAmount, tabname,antiRiskSelfAccountAmount,money+"",listss.getRow(0).get("sa_code"));
			}
			
			
			updateProcessBuissnes(out_pay_id, db,"1");
			
			result.put("code", "0");
			result.put("msg", "帮扶成功");
			
		} else {
			result.put("code", "9999");
			result.put("msg", "业务处理异常。");
		}

		return result.toString();
	}



}
