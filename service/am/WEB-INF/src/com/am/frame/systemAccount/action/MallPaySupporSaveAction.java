package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * @author YueBin
 * @create 2016年5月10日
 * @version 
 * 说明:<br />
 */
public class MallPaySupporSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table=ac.getTable("MALL_PAY_SUPPOR");
		db.save(table);
		//主表主键
		String id=table.getRows().get(0).getValue("id");
		//获取子表
		Table childTable=ac.getTable("MALL_PAY_SUPPOR_ACCOUNT_INFO");
		
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("ps_id", id);
		}
		db.save(childTable);
		
		ac.setSessionAttribute("am_bdp.mall_pay_suppor.form.id", id);
	}
	
}
