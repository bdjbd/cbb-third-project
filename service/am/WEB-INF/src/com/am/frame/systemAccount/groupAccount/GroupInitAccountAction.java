package com.am.frame.systemAccount.groupAccount;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年4月28日
 * @version 
 * 说明:<br />
 * 机构帐号初始化
 */
public interface GroupInitAccountAction {

	/**
	 * 初始化机构 
	 * @param db DB 
	 * @param orgId 机构ID
	 * @return  code:0 初始化成功，code:1000初始化失败，msg，为初始化失败的具体原因。
	 */
	public JSONObject initOrg(DB db,String orgId)throws Exception;
	
}
