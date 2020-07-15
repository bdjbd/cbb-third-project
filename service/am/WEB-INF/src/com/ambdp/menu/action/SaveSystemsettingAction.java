package com.ambdp.menu.action;

import java.sql.Connection;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;
import org.apache.log4j.Logger;

/**
 * 广告保存按钮
 * 保存上次图片的文件路径
 * @author gonghuabin
 * 
 */
public class SaveSystemsettingAction extends DefaultAction {
	final Logger logger = Logger.getLogger(SaveSystemsettingAction.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// super.doAction(db, ac);
		Table table = ac.getTable("am_systemsetting");
		db.save(table);
		if (Checker.isEmpty(table.getRows()))
			return;
		String id = table.getRows().get(0).getValue("id");
		Connection conn = db.getConnection();
		String updateSql = "SELECT now() ";
//		fu_listimgage  listimgage 广告图片
		String fileName = Utils.getFastUnitFilePath("AM_SYSTEMSETTING","fu_aboutimage", id);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE am_systemsetting  SET aboutimage ='" + fileName
					+ "'  WHERE id='" + id + "' ";
			
			conn.createStatement().execute(updateSql);
		}
	
		conn.commit();
	}
}
