package com.am.app_plugins_common.specRechange.callback;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins_common.specRechange.SpecRechangeService;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;


/**
 * 充值业务回调
 * @author yuebin
 *  bussines中的参数rechange_id为充值记录的id
 *  1，检查业务是否成功，
 *  2，修改充值记录的到期时间和支付状态
 *
 */

public class SpecRechangeBusinessCallBack extends AbstraceBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject businessJS=new JSONObject(business);
		
		JSONObject result=new JSONObject();
		
		logger.info("充值业务回调，business："+business);
		
		if(!checkProcessBuissnes(id, db)&&checkTradeState(id, business, db, type)){
			logger.info("充值业务回调处理业务");
			
			//业务未处理，并且交易成功
			String rechangeId=businessJS.getString("rechange_id");
			
			SpecRechangeService srs=new SpecRechangeService();
			srs.updateDueTimeToConfirm(db,rechangeId);
				
			//更新金额到系统运营账号
			VirementManager vm=new VirementManager();
			//org_operation  运营管理机构机构编号
			String iremakers=businessJS.getString("outremakes");
			
			vm.execute(db, 
					"",
					Var.get("operation_rog_orgid"),
					"",
					SystemAccountClass.GROUP_CASH_ACCOUNT,
					businessJS.getString("paymoney"),
					iremakers,
					"",
					"",
					false);
				
				
			
			//更新业务为已处理
			updateProcessBuissnes(id, db,"1");
			
			result.put("code", 0);
			result.put("MSG", "充值完成");
		}
		
		
		
		return result.toString();
	}

}
