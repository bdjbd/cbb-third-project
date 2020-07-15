package com.am.content;

import com.am.tools.Utils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * @author Mike
 * @create 2015年1月29日
 * @version 
 * 说明:<br />
 */
public class ContentSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//1，保存数据表达数据
		Table table=ac.getTable("AM_CONTENT");
		
		db.save(table);
		
		
		//2,保存附件内容路径到对于的字段
		//获取主键
		String id=table.getRows().get(0).getValue("id");
		//bdppclistimage PC列表图
		Utils.saveFilesPathString(db, "AM_CONTENT", "pclistimage","id", id);
		
		//bdpmlistimage 手机端列表图片
		Utils.saveFilesPathString(db, "AM_CONTENT", "mlistimage","id", id);
		
		//bdpattachments 附件
		Utils.saveFilesPathString(db, "AM_CONTENT", "attachments","id", id);
	}
}
