package com.am.frame.systemAccount.ui;

import org.json.JSONObject;

import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月20日 下午6:58:32 
 * @version 1.0   
 */
public class VirementToOtherFormUI implements UnitInterceptor {

	/**
	 *  转账表单ui
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//获取拼接业务回调参数 business参数集合
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		JSONObject paramsObj = business.getFormatBusiness(ac);
		JSONObject businessObj  = null;
		JSONObject actionObj = null;
		
		DBManager db = new DBManager();
		
		//出账code
		String outAccountId = "";
		
		//出账状态
		String state = ac.getRequestParameter("state");
		
		//入账账户
		String  inAccountId = "";
		
		//转账金额
		String pay_money ="";
		
		//入账人的会员id
		String in_member_id = "";
		
		boolean is_out_login_phone_disabled = false;
		
		boolean is_out_money_disabled = false;
		
		//判断是否显示入账账号
		boolean is_in_account_code_show = true;
		
		//个人转账原因
		String person_remake ="";
		
		if(paramsObj.has("action_params") && paramsObj.has("business"))
		{
			
			actionObj  = paramsObj.getJSONObject("action_params");
			businessObj = paramsObj.getJSONObject("business");
			inAccountId = actionObj.getString("in_group_account_code");
			outAccountId = actionObj.getString("out_group_account_code");
			in_member_id = actionObj.getString("in_member_id");
			pay_money = actionObj.getString("out_money");
			is_out_login_phone_disabled = actionObj.getBoolean("is_out_login_phone_disabled");
			is_out_money_disabled = actionObj.getBoolean("is_out_money_disabled");
			is_in_account_code_show = actionObj.getBoolean("is_in_account_code_show");
			person_remake = businessObj.getString("person_remaker");
		}
		
		//设置个人转账操作原因
		if(!Checker.isEmpty(person_remake))
		{
			unit.getElement("other_person_reason").setDefaultValue(person_remake);
		}
		
		//is_out_login_phone_disabled 账号只读
		if(is_out_login_phone_disabled)
		{
			String sql ="select * from mall_account where id='"+in_member_id+"'";
			MapList list = db.query(sql);
			String inaccount = in_member_id;
			if(!Checker.isEmpty(list))
			{
				inaccount = list.getRow(0).get("loginaccount");
			}
			unit.getElement("out_other_memberid").setShowMode(ElementShowMode.CONTROL_DISABLED);
			unit.getElement("out_other_memberid").setDefaultValue(inaccount);
		}
		
		//金钱只读
		if(is_out_money_disabled)
		{
			unit.getElement("out_other_money").setShowMode(ElementShowMode.CONTROL_DISABLED);
			unit.getElement("out_other_money").setDefaultValue(pay_money);
		}
		
		if(!is_in_account_code_show)
		{
			if(unit.getElement("in_other_accountid")!=null)
			{
				unit.getElement("in_other_accountid").setShowMode(ElementShowMode.HIDDEN);
			}
		}
		
		if(!Checker.isEmpty(inAccountId))
		{
			if(unit.getElement("in_other_accountid")!=null)
			{
				unit.getElement("in_other_accountid").setDefaultValue(inAccountId);
			}
			
		}
		if(!Checker.isEmpty(outAccountId))
		{
			unit.getElement("out_other_accountid").setDefaultValue(outAccountId);
		}
		
		
		if(!Checker.isEmpty(state)){
			ac.setSessionAttribute("ss.state", state);
		}else{
			state =(String) ac.getSessionAttribute("ss.state");
		}
		
		
		String sql = "select msac.id as out_account_id,msac.transfer_fee_ratio from mall_system_account_class as msac "
				+ "left join mall_account_info as mai on mai.a_class_id = msac.id "
				+ "where msac.sa_code = '"+outAccountId+"' and mai.member_orgid_id = '"+ac.getVisitor().getUser().getOrgId()+"'";
		
		
		MapList list = db.query(sql);
		
		String transferFeeRatio = "";
		
		if(list.size()>0){
			
			transferFeeRatio = list.getRow(0).get("transfer_fee_ratio");
			
			if(unit.getElement("counter_fee")!=null){
				unit.getElement("counter_fee").setDefaultValue(transferFeeRatio);
			}
			if(unit.getElement("counter_fees")!=null){
				unit.getElement("counter_fees").setDefaultValue(transferFeeRatio);
			}
			
			
			//unit.getElement("out_account_id").setDefaultValue(list.getRow(0).get("out_account_id"));
			
		}
		 
		return unit.write(ac);
	}

}
