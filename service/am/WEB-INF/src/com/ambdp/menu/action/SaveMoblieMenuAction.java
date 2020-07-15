package com.ambdp.menu.action;

import java.sql.Connection;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 
 * @author zhujun 2012-05-15
 * 
 * 单元：终端客户表单-am_mobliemenu_form
 * 功能：先保存终端菜单管理数据在保存菜单按钮参数数据
 *
 */
public class SaveMoblieMenuAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table Tablea = ac.getTable("am_mobliemenu");
		TableRow tr = Tablea.getRows().get(0);
		if (tr.isInsertRow()) {
			db.save(Tablea);
			String id = tr.getValue("id");
			
			Connection conn = db.getConnection();
			String updateSql = "SELECT now() ";
			//fu_menuicon  menuicon 菜单图标
			String fileName = Utils.getFastUnitFilePath("AM_MOBLIEMENU","fu_menuicon", id);
			if (fileName != null && fileName.length() > 1) {
				fileName = fileName.substring(0, fileName.length() - 1);
				updateSql = "UPDATE am_mobliemenu  SET menuicon ='" + fileName
						+ "'  WHERE id='" + id + "' ";
				conn.createStatement().execute(updateSql);
			}
			conn.commit();
			
			Table tableb = ac.getTable("am_menuparame");
			List<TableRow> tls = tableb.getRows();
			for (int i = 0; i < tls.size(); i++) {
				TableRow trb = tls.get(i);
				trb.setValue("am_mobliemenuid", id);
			}
			db.save(tableb);
			
			Table tablec = ac.getTable("am_menupluginnameofterminal");
			List<TableRow> tls2 = tablec.getRows();
			for (int i = 0; i < tls2.size(); i++) {
				TableRow trb2 = tls2.get(i);
				trb2.setValue("am_mobliemenuid", id);
			}
			db.save(tablec);
			
			ac.setSessionAttribute("am_mobliemenu_form.id", id);
		} else {
			super.doAction(db, ac);
		}
	}


}
