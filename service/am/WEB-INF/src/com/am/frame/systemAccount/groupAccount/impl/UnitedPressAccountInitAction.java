package com.am.frame.systemAccount.groupAccount.impl;

import org.json.JSONObject;

import com.am.frame.systemAccount.groupAccount.GroupInitActionAbstract;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月28日
 * @version 
 * 说明:<br />
 * 联合社初始化Action
 * 
 */
public class UnitedPressAccountInitAction extends GroupInitActionAbstract {

	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查是否有配送中心，如果有，不初始化
		MapList checkMap=checkOrgExist(db, orgId, "united_press");
		
		if(!Checker.isEmpty(checkMap)){
			for(int i=0;i<checkMap.size();i++){
				Row row=checkMap.getRow(i);
				//f_status(0：待审核;1：审核通过;2：审核拒绝;3：撤销),
				String fStatus=row.get("f_status");
				if("3".equals(fStatus)||"2".equals(fStatus)){
					result.put("code", "1000");
					result.put("msg", "机构已经存在，请在对应的管理模块中进行操作！");
				}else{
					result.put("code", "10010");
					result.put("msg", "机构已经完成初始化，无需再次初始化！");
				}
			}
		}else{
			//配送中心
			addOrg(db, orgId, "united_press");
			
			//启用/停用帐号
			SystemAccountServer saService=new SystemAccountServer();
			saService.startSystemAllAccount(db, orgId);
			result.put("code", "0");
			result.put("msg", "初始化完成!");
		}
		
		return result;
	}


}
