package com.am.content;

import com.am.tools.Utils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午6:32:56
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class ContentrFormRelease  extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//1，保存数据表达数据
		Table table=ac.getTable("AM_CONTENT");
		db.save(table);
		//2,保存附件内容路径到对于的字段
		//获取主键
		String id=table.getRows().get(0).getValue("id");
		//bdppclistimage PC列表图
		Utils.saveFilesPathString(db, "CFCCONTENT", "pclistimage","id", id);
		
		//bdpmlistimage 手机端列表图片
		Utils.saveFilesPathString(db, "CFCCONTENT", "mlistimage","id", id);
		
		//bdpattachments 附件
		Utils.saveFilesPathString(db, "CFCCONTENT", "attachments","id", id);
		
		String UpdateDataStateSQL = " UPDATE am_content SET datastate = '2' WHERE id = '"+id+"' ";
		db.execute(UpdateDataStateSQL);
	}
}
