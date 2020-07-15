package com.am.triph.coupon.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


//优惠券发布管理保存功能
public class SaveCouponAction extends DefaultAction
{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		String id = ac.getRequestParameter("am_eterpelectticket_publish.form.id");
		
		String sql = "select * from am_eterpelectticket_publish where id='"+id+"'";
		
		Table table=ac.getTable("am_eterpelectticket_publish");
		
		MapList list = db.query(sql);
		
		if(!Checker.isEmpty(list))
		{
			db.save(table);
		}else
		{
			db.save(table);
			String usql = "update am_eterpelectticket_publish set surplus_number = '"+table.getRows().get(0).getValue("publish_number")+"'";
			db.execute(usql);
		}
		
	}

}
