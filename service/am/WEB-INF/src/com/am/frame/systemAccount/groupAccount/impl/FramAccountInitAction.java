package com.am.frame.systemAccount.groupAccount.impl;

import org.json.JSONObject;

import com.am.frame.systemAccount.groupAccount.GroupInitActionAbstract;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月28日
 * @version 
 * 说明:<br />
 * 农厂初始化实现类
 * 
 */
public class FramAccountInitAction extends GroupInitActionAbstract {

	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查是否有农技协，如果有，不初始化
		MapList checkMap=checkOrgExist(db, orgId, "home_farm");
		
		if(Checker.isEmpty(checkMap)){
			//农技协联合会
			addOrg(db, orgId, "home_farm");
			
			//启用/停用帐号
			SystemAccountServer saService=new SystemAccountServer();
			saService.startSystemAllAccount(db, orgId);
			result.put("code", "0");
			result.put("msg", "初始化完成!");
		}else{
			result.put("code", "1000");
			result.put("msg", "机构已经完成初始化，无需再次初始化!");
		}
		
		return result;
	}

}
