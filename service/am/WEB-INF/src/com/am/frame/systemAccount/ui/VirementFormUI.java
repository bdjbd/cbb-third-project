package com.am.frame.systemAccount.ui;

import org.json.JSONObject;

import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月20日 下午6:58:32 
 * @version 1.0   
 */
public class VirementFormUI implements UnitInterceptor {

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
		//获得转入账户 sa_code
		String inAccountId = "";
		//获得转出账户 sa_code
		String outAccountId = "";
		
		//获取转入用户名 in_member_id
		String in_member_id = "";
		
		//获取转入金额
		String out_money = "";
		//判断是否需要只读
		boolean is_out_login_phone_disabled = false;
		//判断是否需要只读
		boolean is_out_money_disabled = false;
		//判断是否显示入账账号
		boolean is_in_account_code_show = true;
		//操作原因
		String person_remake = "";
		
		//业务请求参数
		if(paramsObj.has("action_params") && paramsObj.has("business"))
		{
			
			actionObj  = paramsObj.getJSONObject("action_params");
			businessObj = paramsObj.getJSONObject("business");
			
			
			
			inAccountId = actionObj.getString("in_account_code");
			outAccountId = actionObj.getString("out_account_code");
			in_member_id = actionObj.getString("in_member_id");
			out_money = actionObj.getString("out_money");
			is_out_login_phone_disabled = actionObj.getBoolean("is_out_login_phone_disabled");
			is_out_money_disabled = actionObj.getBoolean("is_out_money_disabled");
			is_in_account_code_show = actionObj.getBoolean("is_in_account_code_show");
			person_remake = businessObj.getString("person_remaker");
			
		}
		
		//设置个人转账操作原因
		if(!Checker.isEmpty(person_remake))
		{
			unit.getElement("person_reason").setDefaultValue(person_remake);
		}
		
		//出账金额是否为只读
		if(is_out_money_disabled)
		{
			unit.getElement("out_money").setShowMode(ElementShowMode.CONTROL_DISABLED);
			unit.getElement("out_money").setDefaultValue(out_money);
		}
		
		
		if(!is_in_account_code_show)
		{
			unit.getElement("in_account_id").setShowMode(ElementShowMode.HIDDEN);
		}
		
		unit.getElement("out_account_id").setDefaultValue(outAccountId);
		
		unit.getElement("in_account_id").setDefaultValue(inAccountId);
		
		String sql = "select msac.id as out_account_id,msac.transfer_fee_ratio from mall_system_account_class as msac "
				+ "left join mall_account_info as mai on mai.a_class_id = msac.id "
				+ "where msac.sa_code = '"+outAccountId+"' and mai.member_orgid_id = '"+ac.getVisitor().getUser().getOrgId()+"'";
		
		DB db = DBFactory.newDB();
		
		MapList list = db.query(sql);
		
		String transferFeeRatio = "";
		
		if(list.size()>0){
			
			transferFeeRatio = list.getRow(0).get("transfer_fee_ratio");
			
			if(unit.getElement("counter_fee")!=null){
				unit.getElement("counter_fee").setDefaultValue(transferFeeRatio);
				unit.getElement("counter_fee").setShowMode(ElementShowMode.CONTROL_DISABLED);
				
			}
			if(unit.getElement("counter_fees")!=null){
				unit.getElement("counter_fees").setDefaultValue(transferFeeRatio);
				unit.getElement("counter_fees").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
			
			//unit.getElement("out_account_id").setDefaultValue(list.getRow(0).get("out_account_id"));
			
		}
		
		
//		String outAccountId = ac.getRequestParameter("outaccountid");
//		String state = ac.getRequestParameter("state");
//		String  outAccountClass = ac.getRequestParameter("outaccountclass");
//		
//		
//		if(!Checker.isEmpty(outAccountClass)){
//			ac.setSessionAttribute("virementui.outaccountclass", outAccountClass);
//		}else{
//			outAccountClass =(String) ac.getSessionAttribute("virementui.outaccountclass");
//		}
//		
//		if(!Checker.isEmpty(outAccountId)){
//			ac.setSessionAttribute("ss.outaccountid", outAccountId);
//		}else{
//			outAccountId =(String) ac.getSessionAttribute("ss.outaccountid");
//		}
//		
//		if(!Checker.isEmpty(state)){
//			ac.setSessionAttribute("ss.state", state);
//		}else{
//			state =(String) ac.getSessionAttribute("ss.state");
//		}
		
		
		
		 
		return unit.write(ac);
	}

}
