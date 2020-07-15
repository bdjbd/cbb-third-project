package com.am.frame.systemAccount.groupAccount.impl;

import org.json.JSONObject;

import com.am.frame.systemAccount.groupAccount.GroupInitActionAbstract;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;


/***
 * 扶贫办初始化类
 * @author yuebin
 *
 */
public class IPRCCAccountInitAction extends GroupInitActionAbstract {

	
	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查是否有农技协，如果有，不初始化
		MapList checkMap=checkOrgExist(db, orgId, "iprcc_office");
		
		if(Checker.isEmpty(checkMap)){
			//扶贫办
			addOrg(db, orgId, "iprcc_office");
			
			//启用/停用帐号
			SystemAccountServer saService=new SystemAccountServer();
			//saService.startSystemAllAccount(db, orgId);
			String initAccountCodes=Var.get("iprcc_office_org_init_account_code");
			saService.startSystemAllAccount(db, orgId,initAccountCodes);
			
			result.put("code", "0");
			result.put("msg", "初始化完成!");
		}else{
			result.put("code", "1000");
			result.put("msg", "机构已经完成初始化，无需再次初始化!");
		}
		
		return result;
	}
}
