package com.fastunit.wd_base.offlinetable;

import java.util.UUID;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/**
 * 离线模块设置,保存Action
 * */
public class SaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		Table table=ac.getTable("ABDP_OFFLINETABLE");
		Table childTable=ac.getTable("ABDP_OFFLINEMODUEL");
		if(table!=null && table.getRows().size()>0)
		{
			TableRow row=table.getRows().get(0);
			String uid=UUID.randomUUID().toString();
			if(row.isInsertRow())
			{
				row.setValue("tid",uid);
			}
			else if(row.isUpdateRow())
			{
				uid=row.getOldValue("tid");
			}
			
			if(childTable!=null && childTable.getRows().size()>0)
			{
				for(int i=0;i<childTable.getRows().size();i++)
				{
					TableRow childRow=childTable.getRows().get(i);
					childRow.setValue("tid",uid);
				}
			}
			
		}
		super.doAction(db, ac);
	}
}
