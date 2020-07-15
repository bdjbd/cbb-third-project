package com.ambdp.cro.cro_rent.action;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.pay.PayManager;
import com.am.frame.transactions.rechange.Rechange;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 支付成功回调 
 * guorenjie 2017/7/24
 */
public class RentPayBusinessCallBack extends AbstraceBusinessCallBack {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String callBackExec(String id, String business, DB db, String type)
			throws Exception {
		logger.info("处理充值回调 执行转账回调 type:" + type + "\t business:" + business);

		JSONObject businessJso = new JSONObject(business);

		String payment_id = businessJso.getString("payment_id");

		String in_account_code = businessJso.getString("in_account_code");

		String memberid = businessJso.getString("memberid");

		String orders = businessJso.getString("orders");

		String outremakes = businessJso.getString("outremakes");

		// 充值金额 单位，元
		String paymoney = businessJso.getString("paymoney");
		//缴费记录字段
		String orgcode = businessJso.getString("orgcode");//当前要续费的机构id
		String price = businessJso.getString("price");//套餐价格
		String leasedays = businessJso.getString("leasedays");//续费天数
		String payexplain = businessJso.getString("payexplain");//缴费留言
		
		//调转账方法给现金账户充值余额		
		//充值金额元，转换为分
				String str=(Double.parseDouble(paymoney)*100)+"";
				
				Long money = Long.valueOf(str.substring(0,str.indexOf(".")));
				
				Rechange rechange = new Rechange();
				
				JSONObject obj = new JSONObject();
				
				obj = rechange.rechangeExc(money, in_account_code, memberid);
				
				logger.info("处理充值回调 执行转账回调 返回结果:"+obj);
				
				if(obj!=null&&"0".equals(obj.getString("code"))){
					logger.info("充值成功!");
				}
		
		// 转账 注意 pay_id是最关键的参数，它既作为交易详情记录表的主键ID，也作为支付ID
		// mall_trade_detail的主键ID、pay_id、payment_id支付ID应当采用同一个值
		String pay_id = UUID.randomUUID().toString();
		JSONObject business1 = new JSONObject();
		String uuid = UUID.randomUUID().toString();
		// 在租金缴费记录表中，新增一条记录 单位：元
		if (!Checker.isEmpty(id)) {
			String insertSql = "INSERT INTO cro_RentPayRecord( id, OrgCode, PayDate, PayMoney, LeaseDays, PayExplain) "
					+ "values ('"
					+ uuid
					+ "','"
					+ orgcode
					+ "','now()','"
					+ price
					+ "','"
					+ leasedays
					+ "','"
					+ payexplain + "') ";
			db.execute(insertSql);
		}

		// 更新汽修厂表 aorg表中的租期到期日期(新写法)(汽修厂启用停用状态更改，)
		String leasedaysSql = "UPDATE aorg SET LeaseExpireDate=(LeaseExpireDate + interval '"
				+ leasedays + " D'),leasestate='1' WHERE orgid='" + orgcode + "' ";
		db.execute(leasedaysSql);
		//更新此汽修厂下的用户过期时间，expireddate=null,expired=0
		String updateSql = "UPDATE AUSER set expireddate=null,expired=0 WHERE orgid like '" + orgcode + "%'";
		db.execute(updateSql);

		business1.put("is_group", true);
		business1.put("payment_id", pay_id);
		business1.put("projectid", id);
		business1.put("membername", "");
		business1.put("pname", "");
		business1.put("totalmoney", price);
		business1.put("memberid", orgcode);
		business1.put("number", 1);
		business1.put("recodeId", uuid);
		business1.put("orders", UUID.randomUUID().toString());
		business1.put("success_call_back", ""); // 回调设为空，不进行后续操作
		

		logger.info("支付参数：" + business1.toString());

		String outremakes1 = "办理续费";

		// 系统账户 支付(单位：元) //自动操作：向交易详情表
		// 新增一条数据(默认交易状态="交易失败"trade_state='2')、更新会员（机构）现金账户表余额(扣款成功后，交易状态='交易成功'trade_state='1')
		PayManager payManager = new PayManager();
		JSONObject resultJson = payManager.excunte(orgcode,
				SystemAccountClass.GROUP_CASH_ACCOUNT, price + "",
				pay_id, business1.toString(), outremakes1);

		

		// 更新交易详情表中的业务状态 业务是否处理(整数型) 0=未处理 ; 1=已处理
		String businessStateSQL = " update mall_trade_detail set is_process_buissnes=1 where id='"
				+ pay_id + "' ";
		db.execute(businessStateSQL);
		
		
		return "";
	}

}
