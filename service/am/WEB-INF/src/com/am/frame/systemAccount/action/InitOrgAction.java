package com.am.frame.systemAccount.action;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.groupAccount.GroupInitAccountAction;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月28日
 * @version 
 * 说明:<br />
 * 初始化机构.根据机构类型初始化机，
 * 初始化的机构主要有：联合社，配送中心，农技协联合会，涉农企业，农厂，合作社。
 * 其中，总农技协联合会，总联合社，总配送中心只能有一个。
 * 农厂，合作社，涉农企业可以有多个。
 * 联合会，联合社，配送中心，一个区县只能有一个。
 * 如果是配送中心，需要给配送中心分配一个门店
 */
public class InitOrgAction extends DefaultAction {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//1,获取流程机构id
		String orgId=ac.getRequestParameter("org.form.orgid");
		
		//启用/停用帐号
		SystemAccountServer saService=new SystemAccountServer();
		saService.startSystemAllAccount(db, orgId);
		logger.error("初始化机构帐号完成.机构编号:"+orgId);
		
		//2,获取机构类型
		String orgType=ac.getRequestParameter("org.form.orgtype");
		
		//3,判断是否可以初始化
		String querySQL="SELECT * FROM aorgtype WHERE orgtype=? ";
		
		JSONObject result=null;
		
		String initClassPath=null;
		
		MapList map=db.query(querySQL, orgType,Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			initClassPath=map.getRow(0).get("init_class_path");
			if(!Checker.isEmpty(initClassPath)){
				GroupInitAccountAction initAction=(GroupInitAccountAction) 
						Class.forName(initClassPath).newInstance();
				result=initAction.initOrg(db, orgId);
			}
		}else if(Checker.isEmpty(initClassPath)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("机构配置不在正确或者无需初始化机构。");
		}
		
		if(result!=null&&!"0".equals(result.get("code"))){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage(result.getString("msg"));
		}
	}

	/**
	 * 初始化机构
	 * @param db
	 * @param orgid
	 * @param orgType
	 */
	private void initOrg(DB db, String orgid, String orgType) {
	}

	/**
	 * 判断是否可以初始化.
	 * 判断依据：
	 * 总联合社，总农技协联合会，总配送中心 只能有一个。
	 * 省农技协联合会，每个省只能有一个。
	 * 市农技协联合会，每个市只能有一个。
	 * 区县联合会，每个区县只能有一个。
	 * 农厂，合作社，只能在区县下面，但是可以有多个。
	 * @param db
	 * @param orgid
	 * @param orgType
	 * @return  true 可以初始化，false 不能初始化
	 */
	private boolean checkInitOrg(DB db, String orgid, String orgType){
		
		return false;
	}
	
}
