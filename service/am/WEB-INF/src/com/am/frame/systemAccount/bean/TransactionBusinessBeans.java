package com.am.frame.systemAccount.bean;

import org.jgroups.util.UUID;
import org.json.JSONObject;

import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

/**
 * 账户交易业务参数对象 交易记录中的 business
 * 
 * @author mac
 *
 */
public class TransactionBusinessBeans {
	// 自己需处理的业务id
	private String business_id = "";
	// 订单id 可为随机uuid
	private String orders = "";
	// 成功回调参数
	private String success_call_back = "";
	// 自己需要的业务参数 JSON
	private JSONObject customer_params = null;
	// 出账账户sa_code
	private String out_account_code = "";
	// 入账账户的sa_code
	private String in_account_code = "";
	// 入账账户的人的member_id
	private String in_member_id = "";
	// 出账金额
	private String out_money = "";
	//组织机构转账入账使用代码
	private String in_group_account_code = "";
	//组织机构转账出账使用代码
	private String out_group_account_code = "";
	//用户转账信息
	private String person_remaker = "";

	// 是否将转入账户的登录账号输入框变为只读状态 只限于向他人转账操作
	private boolean is_out_login_phone_disabled = false;
	
	// 是否将转入账户的登录账号输入框变为只读状态 只限于向他人转账操作
	private boolean is_in_account_code_show = true;
	
	// 是否将转入金额变为只读模式
	private boolean is_out_money_disabled = false;
	
	// 最大金额限制
	private String max_long = "";
	
	//最大金额限制提示
	private String max_message = "";

	private String business_session_key = "systemAccount_transaction_business_aggregate";
	
	
	
	public String getIn_group_account_code() {
		return in_group_account_code;
	}

	public void setIn_group_account_code(String in_group_account_code) {
		this.in_group_account_code = in_group_account_code;
	}

	public String getOut_group_account_code() {
		return out_group_account_code;
	}

	public void setOut_group_account_code(String out_group_account_code) {
		this.out_group_account_code = out_group_account_code;
	}

	

	public String getOut_account_code() {
		return out_account_code;
	}

	public void setOut_account_code(String out_account_code) {
		this.out_account_code = out_account_code;
	}

	public String getIn_account_code() {
		return in_account_code;
	}

	public void setIn_account_code(String in_account_code) {
		this.in_account_code = in_account_code;
	}

	public String getIn_member_id() {
		return in_member_id;
	}

	public void setIn_member_id(String in_member_id) {
		this.in_member_id = in_member_id;
	}

	public String getOut_money() {
		return out_money;
	}

	public void setOut_money(String out_money) {
		this.out_money = out_money;
	}

	public boolean isIs_out_login_phone_disabled() {
		return is_out_login_phone_disabled;
	}

	public void setIs_out_login_phone_disabled(boolean is_out_login_phone_disabled) {
		this.is_out_login_phone_disabled = is_out_login_phone_disabled;
	}

	public boolean isIs_out_money_disabled() {
		return is_out_money_disabled;
	}

	public void setIs_out_money_disabled(boolean is_out_money_disabled) {
		this.is_out_money_disabled = is_out_money_disabled;
	}

	public String getBusiness_session_key() {
		return business_session_key;
	}

	public void setBusiness_session_key(String business_session_key) {
		this.business_session_key = business_session_key;
	}

	public String getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	public String getOrders() {
		if(Checker.isEmpty(orders))
		{
			orders = UUID.randomUUID().toString();
		}
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public String getSuccess_call_back() {
		return success_call_back;
	}

	public void setSuccess_call_back(String success_call_back) {
		this.success_call_back = success_call_back;
	}

	public JSONObject getCustomer_params() {
		return customer_params;
	}

	public void setCustomer_params(JSONObject customer_params) {
		this.customer_params = customer_params;
	}

	/**
	 * 拼接好最后的业务参数集合并且放在session中
	 * 
	 * @param ac
	 *            ActionContext 传入actionContext
	 * @throws Exception
	 */

	public void formatBusiness(ActionContext ac) throws Exception {
		JSONObject businessJso = new JSONObject();

		businessJso.put("business_id", getBusiness_id());
		businessJso.put("success_call_back", getSuccess_call_back());
		businessJso.put("orders", getOrders());
		businessJso.put("customer_params", getCustomer_params());
		businessJso.put("person_remaker", getPerson_remaker());

		JSONObject actionParasm = new JSONObject();
		actionParasm.put("out_account_code", getOut_account_code());
		actionParasm.put("in_account_code", getIn_account_code());
		actionParasm.put("in_member_id", getIn_member_id());
		actionParasm.put("out_money", getOut_money());
		actionParasm.put("is_out_login_phone_disabled", isIs_out_login_phone_disabled());
		actionParasm.put("is_out_money_disabled", isIs_out_money_disabled());
		actionParasm.put("in_group_account_code", getIn_group_account_code());
		actionParasm.put("out_group_account_code", getOut_group_account_code());
		actionParasm.put("is_in_account_code_show", isIs_in_account_code_show());
		actionParasm.put("max_long", getMax_long());
		actionParasm.put("max_message", getMax_message());
		
		JSONObject resource = new JSONObject();
		resource.put("business", businessJso);
		resource.put("action_params", actionParasm);
		
		ac.setSessionAttribute(business_session_key, resource.toString());
	}

	/**
	 * 获得拼接好最后的业务参数集合清空session
	 * 
	 * @param ac
	 *            ActionContext 传入actionContext
	 * @throws Exception
	 */
	public JSONObject getFormatBusiness(ActionContext ac) throws Exception {
		Object resourc = null;
		resourc = ac.getSessionAttribute(business_session_key);
		JSONObject jso = null;
		if(resourc == null)
		{
			jso= new JSONObject();
		}else
		{
			jso= new JSONObject(resourc.toString());
		}
		
		return jso;
	}

	public boolean isIs_in_account_code_show() {
		return is_in_account_code_show;
	}

	public void setIs_in_account_code_show(boolean is_in_account_code_show) {
		this.is_in_account_code_show = is_in_account_code_show;
	}

	public String getPerson_remaker() {
		return person_remaker;
	}

	public void setPerson_remaker(String person_remaker) {
		this.person_remaker = person_remaker;
	}

	public String getMax_long() {
		return max_long;
	}

	public void setMax_long(String max_long) {
		this.max_long = max_long;
	}

	public String getMax_message() {
		return max_message;
	}

	public void setMax_message(String max_message) {
		this.max_message = max_message;
	}

}
