package com.am.frame.systemAccount.groupAccount.impl;

import org.json.JSONObject;

import com.am.frame.systemAccount.groupAccount.GroupInitActionAbstract;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月28日
 * @version 
 * 说明:<br />
 * 配送中心初始化 mall_distribution_center
 */
public class DistributionCentreAccountInitAction extends GroupInitActionAbstract {

	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查是否有配送中心，如果有，不初始化
		MapList checkMap=checkOrgExist(db, orgId, "mall_distribution_center");
		
		if(!Checker.isEmpty(checkMap)){
			for(int i=0;i<checkMap.size();i++){
				Row row=checkMap.getRow(i);
				//f_status(0：待审核;1：审核通过;2：审核拒绝;3：撤销),
				String fStatus=row.get("f_status");
				if("3".equals(fStatus)||"2".equals(fStatus)){
					result.put("code", "1000");
					result.put("msg", "机构已经存在，请在对应的管理模块中进行操作！");
				}else{
					//查询机构对应的店铺ID
					String querySQL="SELECT * FROM MALL_STORE WHERE orgcode=? ";
					
					MapList stroeMap=db.query(querySQL,orgId,Type.VARCHAR);
					
					if(!Checker.isEmpty(stroeMap)){
						String id=stroeMap.getRow(0).get("id");
						result.put("store_id", id);
					}
					result.put("code", "10010");
					result.put("msg", "机构已经完成初始化，无需再次初始化！");
					
				}
			}
		}else{
			
			//配送中心
			addOrg(db, orgId, "mall_distribution_center");
			
			//启用/停用帐号
			SystemAccountServer saService=new SystemAccountServer();
			saService.startSystemAllAccount(db, orgId);
			
			//配送中，给配送中心生成一个店铺
			String checkSQL="SELECT * FROM MALL_STORE WHERE orgcode=?";
			if(Checker.isEmpty(db.query(checkSQL, orgId, Type.VARCHAR))){
				
				Table mallStoretable=new Table("am_bdp","MALL_STORE");
				TableRow insertStoreTr=mallStoretable.addInsertRow();
				insertStoreTr.setValue("orgcode", orgId);
				insertStoreTr.setValue("mallclass_id","5");
				
				db.save(mallStoretable);
				String id=mallStoretable.getRows().get(0).getValue("id");
				result.put("store_id", id);
			}
			
			result.put("code", "0");
			result.put("msg", "初始化完成!");
		}
		
		
		return result;
	}

}
