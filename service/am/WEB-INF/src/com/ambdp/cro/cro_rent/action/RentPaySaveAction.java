package com.ambdp.cro.cro_rent.action;

import java.net.URLEncoder;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.pay.PayManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/7/10
 * @version 说明：修车厂缴费界面保存Action
 */
public class RentPaySaveAction implements Action {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		// 获取租金列表的全部列
		String[] List = ac.getRequestParameters("_s_cro_rent");
		// 获取租金列表的全部主键
		String[] Ids = ac.getRequestParameters("cro_rent.id.k");
		// 选中的租金ID
		String id = "";
		// 若列表有值
		if (!Checker.isEmpty(Ids)) {
			for (int i = 0; i < Ids.length; i++) {
				// 选中的项List[i]必定为'1'
				if ("1".equals(List[i])) {
					id = Ids[i];
				}
			}
		}
		// 检查 是否已选择缴费套餐
		if (Checker.isEmpty(id) || "".equals(id)) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请选择缴费套餐！");
			ac.getActionResult().setUrl("/am_bdp/cro_rent_page.do?m=s");
			return ac;
		}

		DB db = DBFactory.getDB();
		// 1,检查账号余额
		// 2,转账
		// 3.更新记录

		// 机构现金账户的sa_code
		String code = SystemAccountClass.GROUP_CASH_ACCOUNT;// "GROUP_CASH_ACCOUNT";
		// 用户填写的缴费留言，转账成功后 存入缴费记录表
		String payexplain = ac.getRequestParameter("payexplain.payexplain");
		// 当前修车厂的机构ID
		String orgcode = ac.getRequestParameter("payexplain.orgcode");
		// 检查 是否已输入要充值的机构id
				if (Checker.isEmpty(orgcode) || "".equals(orgcode)) {
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("请输入要充值的机构id！");
					ac.getActionResult().setUrl("/am_bdp/cro_rent_page.do?m=s");
					return ac;
				}
		// 获得当前登录者的机构ID
		String login_orgcode = ac.getVisitor().getUser().getOrgId();
		// 查询用户选择的缴费套餐(支付租金)详情
		String rent = "SELECT price,leasedays,rentexplain FROM CRO_RENTCOMMODITY WHERE id='"
				+ id + "' ";
		MapList rentMap = db.query(rent);
		String price = ""; // 套餐价格(单位：元)
		String leasedays = ""; // 续费天数
		String rentexplain = ""; // 续费说明
		if (!Checker.isEmpty(rentMap)) {
			price = rentMap.getRow(0).get("price");
			leasedays = rentMap.getRow(0).get("leasedays");
			rentexplain = rentMap.getRow(0).get("rentexplain");
		}
		logger.info("检查是否登录，现在login_orgcode==========" + login_orgcode);
		// 如果未登录,先充值现金账户，然后在用现金账户续费
		if (Checker.isEmpty(login_orgcode)||"".equals(login_orgcode)) {
			logger.info("=====================未登录===============");
			// 支付方式 支付宝 微信
			String outAccountCode = SystemAccountClass.GROUP_ALIPAY_ACCOUNT_MODE_WEB
					+ "," + SystemAccountClass.GROUP_SCAN_WECHAT_ACCOUNT_MODE;
			// 支付id
			String pay_id = UUID.randomUUID().toString();
			// 支付金额
			String pay_money = price;
			if (pay_money != null && !pay_money.equals("0")) {

				String success_call_back = "com.ambdp.cro.cro_rent.action.RentPayBusinessCallBack";// 回调类
				String pay_type = "2";// 支付类型,支付类型 1 支付 2 充值
				String account_type = "2";// 账户类型 1 系统账户 2 支付宝 3 微信 4 银联
				String inremakes = "";
				String outremakes = "";
				String platform = "2";
				String success_url = "";

				JSONObject business = new JSONObject();

				// 入账账号ID，为运营机构的现金账号
				String inAccountCode = SystemAccountClass.GROUP_CASH_ACCOUNT;

				business.put("payment_id", pay_id);
				business.put("in_account_code", inAccountCode);
				business.put("memberid", orgcode);
				business.put("orders", pay_id);
				business.put("paymoney", pay_money);
				business.put("payMemberId", orgcode);
				business.put("success_call_back", success_call_back);
				business.put("outremakes", outremakes);
				business.put("recharge_id", id);
				//缴费记录需要的字段
				business.put("orgcode", orgcode);//当前要续费的机构id
				business.put("price", price);//套餐价格
				business.put("leasedays", leasedays);//续费天数
				business.put("payexplain", payexplain);//缴费留言

				String requestUrl = "/am_bdp/common_confirm_payment.do?m=e"
						+ "&autoback="
						+ "/am_bdp/cro_rent_page.do?m=e&autoback=/app/login.do"
						+ "&memberId="
						+ orgcode
						+ "&in_account_code="
						+ inAccountCode
						+ "&out_account_code="
						+ outAccountCode
						+ "&pay_id="
						+ pay_id
						+ "&pay_money="
						+ pay_money
						+ "&pay_type="
						+ pay_type
						+ "&account_type="
						+ account_type
						+ "&business="
						+ URLEncoder.encode(business.toString(),"utf-8")
						+ "&inremakes="
						+ inremakes
						+ "&outremakes="
						+ outremakes
						+ "&platform="
						+ platform
						+ "&success_url="
						+ success_url 
						+ "&commodityname="
						+URLEncoder.encode("充值", "utf-8");
				
				ac.getActionResult().setSuccessful(true);
				ac.getActionResult().setUrl(requestUrl);

			}
		}
		// 如果已登录，直接使用现金账户缴费
		else {
			logger.info("================已登录=============");
			// 查询该修车厂的现金账户余额(单位：分) 系统帐号分类 a_class_id 支付方式sa_code
			String balanceSql = " SELECT id,a_class_id, balance,"
					+ price
					+ "*100 AS centprice, "
					+ price
					+ " AS yuanPrice "
					+ " FROM mall_account_info WHERE member_orgid_id='"
					+ orgcode
					+ "' "
					+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"
					+ SystemAccountClass.GROUP_CASH_ACCOUNT + "') ";
			MapList balanceMap = db.query(balanceSql);
			System.err.println("该修车厂的现金账户余额sql>>>" + balanceSql);

			// 账户余额(单位：分)
			Long balance = 0L;
			// 总价(字符串格式 单位：元)
			String temp = "";
			// 总价(单位：分)
			Long centprice = 0L;
			// 会员系统帐号ID
			String account_id = "";
			// 系统帐号分类ID
			String a_class_id = "";

			if (!Checker.isEmpty(balanceMap)) {
				account_id = balanceMap.getRow(0).get("id");
				a_class_id = balanceMap.getRow(0).get("a_class_id");
				balance = Long.parseLong(balanceMap.getRow(0).get("balance")); // 账户余额转成分
																				// string转long型

				temp = balanceMap.getRow(0).get("centprice"); // string型的 总价
																// 单位：分
																// (带有多余的小数点，需要转换一下)
				temp = temp.substring(0, temp.indexOf(".")); // 总价 分
																// 去掉多余的小数点
				centprice = Long.parseLong(temp); // 总价 分 转成long型
			}
			// 余额与总价进行比较 单位都为分
			if (balance < centprice || balance <= 0) {
				// 余额不足
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("现金帐号余额不足！请充值");
				ac.getActionResult().setUrl("/am_bdp/cro_rent_page.do?m=s");

			} else {
				// 转账 注意 pay_id是最关键的参数，它既作为交易详情记录表的主键ID，也作为支付ID
				// mall_trade_detail的主键ID、pay_id、payment_id支付ID应当采用同一个值
				String pay_id = UUID.randomUUID().toString();
				JSONObject business = new JSONObject();
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
					System.err.println("缴费记录表中，新增一条记录sql>>>" + insertSql);
					db.execute(insertSql);
				}

				
				// 更新汽修厂表 aorg表中的租期到期日期(新写法)(汽修厂启用停用状态更改，)
				String leasedaysSql = "UPDATE aorg SET LeaseExpireDate=(LeaseExpireDate + interval '"
						+ leasedays + " D'),leasestate='1' WHERE orgid='" + orgcode + "' ";
				db.execute(leasedaysSql);
				//更新此汽修厂下的用户过期时间，expireddate=null,expired=0
				String updateSql = "UPDATE AUSER set expireddate=null,expired=0 WHERE orgid like '" + orgcode + "%'";
				db.execute(updateSql);
				
				business.put("is_group", true);
				business.put("payment_id", pay_id);
				business.put("projectid", id);
				business.put("membername", "");
				business.put("pname", "");
				business.put("totalmoney", price);
				business.put("memberid", orgcode);
				business.put("number", 1);
				business.put("stockprice", centprice);// 单价 单位分
				business.put("recodeId", uuid);
				business.put("orders", UUID.randomUUID().toString());
				business.put("success_call_back", ""); // 回调设为空，不进行后续操作
				

				logger.info("支付参数：" + business.toString());

				String outremakes = "办理续费";

				// 系统账户 支付(单位：元) //自动操作：向交易详情表
				// 新增一条数据(默认交易状态="交易失败"trade_state='2')、更新会员（机构）现金账户表余额(扣款成功后，交易状态='交易成功'trade_state='1')
				PayManager payManager = new PayManager();
				JSONObject resultJson = payManager.excunte(orgcode,
						SystemAccountClass.GROUP_CASH_ACCOUNT, price + "",
						pay_id, business.toString(), outremakes);

				ac.getActionResult().addSuccessMessage("缴费成功！");

				// 更新交易详情表中的业务状态 业务是否处理(整数型) 0=未处理 ; 1=已处理
				String businessStateSQL = " update mall_trade_detail set is_process_buissnes=1 where id='"
						+ pay_id + "' ";
				db.execute(businessStateSQL);
			}

			if (db != null) {
				db.close();
			}
		}
		return ac;
	}
}
