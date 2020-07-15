package com.wd.wd_route_item;

import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class Wd_route_itemSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// TODO Auto-generated method stub      
		Table Tablea = ac.getTable("WD_ROUTE");
		TableRow tr = Tablea.getRows().get(0);
		if (tr.isInsertRow()) {
			db.save(Tablea);
			String id = tr.getValue("route_id");//取出主表中的主键
			Table tableb = ac.getTable("WD_ROUTE_ITEM");
			List<TableRow> tls = tableb.getRows();
			for (int i = 0; i < tls.size(); i++) {
				TableRow trb = tls.get(i);
				trb.setValue("route_id", id);//把主表中的主键依次放入子表中的外键
			}
			db.save(tableb);
			ac.setSessionAttribute("wd_blj.wd_route_item.form.route_id", id);//把主表中主键放入到Session中去  注意域名
		} else {
			super.doAction(db, ac);
		}
	}
}