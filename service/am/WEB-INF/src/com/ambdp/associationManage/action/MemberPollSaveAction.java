package com.ambdp.associationManage.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 *@author wangxi
 *@create 2016年4月15日
 *@version
 *说明：社员代表大会保存Action
 */
public class MemberPollSaveAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//主表----内容组件菜单
		Table table=ac.getTable("mall_member_poll");
		db.save(table);
		//获取主表主键
		String memId=table.getRows().get(0).getValue("id");
		
		//获取子表--内容菜单属性
		Table childTable=ac.getTable("mall_options");
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("mp_id", memId);
		}
		
		db.save(childTable);
	}

}
