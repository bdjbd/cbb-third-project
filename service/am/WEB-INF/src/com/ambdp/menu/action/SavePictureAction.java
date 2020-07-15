package com.ambdp.menu.action;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 相册保存按钮
 * 保存上次图片的文件路径
 * @author gonghuabin
 * 
 */
public class SavePictureAction extends DefaultAction {
	final Logger logger = Logger.getLogger(SavePictureAction.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// super.doAction(db, ac);
		Table table = ac.getTable("am_picturelist");
		db.save(table);
		String menucode = ac.getRequestParameter("am_picturelist_form.am_mobliemenuid");
		
		String id = table.getRows().get(0).getValue("id");
		ac.setSessionAttribute("am_bdp.am_picturelist_form.id", id);
		
		Connection conn = db.getConnection();
		String updateSql = "  ";
//		fu_listimgage  listimgage 列表图片
		String fileName = Utils.getFastUnitFilePath("AM_PICTURELIST","fu_listimgage", id);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE am_picturelist  SET listimgage ='" + fileName
					+ "'  WHERE id='" + id + "' ";
//			logger.info(updateSql);
			conn.createStatement().execute(updateSql);
		}
//		fu_detailedimages  detailedimages 内容图片
		fileName = Utils.getFastUnitFilePath("AM_PICTURELIST","fu_detailedimages", id);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE am_picturelist  SET detailedimages ='" + fileName
					+ "'  WHERE id='" + id + "' ";
			
			conn.createStatement().execute(updateSql);
		}
		
		conn.commit();
	}
}
