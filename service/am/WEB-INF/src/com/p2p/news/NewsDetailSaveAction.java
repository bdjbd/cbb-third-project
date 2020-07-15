package com.p2p.news;

import java.sql.Connection;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 新闻保存按钮
 * 
 * @author Administrator
 * 
 */
public class NewsDetailSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// super.doAction(db, ac);
		Table table = ac.getTable("NEWSDETAIL");
		db.save(table);
		if (Checker.isEmpty(table.getRows()))
			return;
		String nid = table.getRows().get(0).getValue("nid");
		Connection conn = db.getConnection();
		String updateSql = "SELECT now() ";
//		fu_mainimage  MainImage 列表图片
		String fileName = Utils.getFastUnitFilePath("NEWSDETAIL","fu_mainimage", nid);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE NEWSDETAIL  SET MainImage='" + fileName
					+ "'  WHERE nid=" + nid + " ";
			System.out.println(fileName);
			conn.createStatement().execute(updateSql);
		}
//		fu_contentimage  ContentImage 内容图片
		fileName = Utils.getFastUnitFilePath("NEWSDETAIL","fu_contentimage", nid);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE NEWSDETAIL  SET ContentImage='" + fileName
					+ "'  WHERE nid=" + nid + " ";
			System.out.println(fileName);
			conn.createStatement().execute(updateSql);
		}
//		fu_mmainimage  列表图片(手机)
		fileName = Utils.getFastUnitFilePath("NEWSDETAIL","fu_mmainimage", nid);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE NEWSDETAIL  SET mMainImage='" + fileName
					+ "'  WHERE nid=" + nid + " ";
			System.out.println(fileName);
			conn.createStatement().execute(updateSql);
		}
//		fu_mcontentimage 内容图片(手机)
		fileName = Utils.getFastUnitFilePath("NEWSDETAIL","fu_mcontentimage", nid);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE NEWSDETAIL  SET mContentImage='" + fileName
					+ "'  WHERE nid=" + nid + " ";
			System.out.println(fileName);
			conn.createStatement().execute(updateSql);
		}
		
		conn.commit();
	}
}
