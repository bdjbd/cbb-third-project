package com.am.instore.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：调拨管理保存Action
 */
public class AllocationSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//调拨管理
		Table table=ac.getTable("mall_allocation");
		
		db.save(table);
		//主表主键
		String id=table.getRows().get(0).getValue("id");
		//获取子表
		Table childTable=ac.getTable("mall_allocation_list_info");
		
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("allocation_id", id);
		}

		db.save(childTable);
		
		
		ac.setSessionAttribute("am_bdp.mall_allocation.form.id", id);
	}
}
