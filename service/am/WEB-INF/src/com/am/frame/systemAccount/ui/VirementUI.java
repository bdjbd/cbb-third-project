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
 *  转账表单ui
 */
public class VirementUI implements UnitInterceptor {

	/**
	 *  转账表单ui
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		
		//获取拼接业务回调参数 business参数集合
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		JSONObject businessObj =null;
		JSONObject actionObj = null;
		JSONObject resource  = business.getFormatBusiness(ac);
		
		//出账账号ID
//		String outAccountId = ac.getRequestParameter("outaccountid");
		String outAccountId =null;
		//状态
		String state = ac.getRequestParameter("state");
		
		//出账账号类型
		String outAccountClass = null;
		
		if(!Checker.isEmpty(outAccountId)){
			ac.setSessionAttribute("ss.outaccountid", outAccountId);
		}else{
			outAccountId =(String) ac.getSessionAttribute("ss.outaccountid");
		}
		
		if(!Checker.isEmpty(state)){
			ac.setSessionAttribute("ss.state", state);
		}else{
			state =(String) ac.getSessionAttribute("ss.state");
		}
		
		businessObj = resource.getJSONObject("business");
		actionObj = resource.getJSONObject("action_params");
		outAccountId = actionObj.getString("out_account_code");
		outAccountClass = actionObj.getString("in_account_code");
		
		if(!Checker.isEmpty(unitData)){
			String audite_result = unitData.getRow(0).get("audit_result");
			String account_type = unitData.getRow(0).get("account_type");
			
			
			if(Checker.isEmpty(audite_result)){
				unit.getElement("second_director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("audit_opinion").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("main_audit_opinion").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("audit_result").setShowMode(ElementShowMode.REMOVE);
			}
			if("1".equals(audite_result)||"3".equals(audite_result)){
				unit.getElement("director").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("main_audit_opinion").setShowMode(ElementShowMode.REMOVE);
			}
			
			if(!Checker.isEmpty(outAccountClass)){
				ac.setSessionAttribute("virementui.outaccountclass", outAccountClass);
			}else{
				outAccountClass =(String) ac.getSessionAttribute("virementui.outaccountclass");
			}
			
			
			if("2".equals(account_type)){
				unit.getElement("no_reasons").setShowMode(ElementShowMode.REMOVE);
			}
			
			String sql = "select msac.id as out_account_id,msac.transfer_fee_ratio from mall_system_account_class as msac "
					+ "left join mall_account_info as mai on mai.a_class_id = msac.id "
					+ "where msac.sa_code = '"+outAccountId+"' and mai.member_orgid_id = '"+ac.getVisitor().getUser().getOrgId()+"'";
			
			DBManager db = new DBManager();//DBFactory.newDB();
			
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
		}
		
		
		return unit.write(ac);
	}

}
