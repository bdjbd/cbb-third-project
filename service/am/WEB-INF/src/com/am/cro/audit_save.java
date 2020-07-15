package com.am.cro;

import java.sql.Connection;
import org.apache.log4j.Logger;

import com.ambdp.menu.action.SaveAdvertAction;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 广告系统保存，审核，
 * 
 * @author 刘浩
 *
 */
public class audit_save extends DefaultAction {
	final Logger logger = Logger.getLogger(SaveAdvertAction.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("am_advertising_system");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		// id
		String id = tr.getValue("id");
		String orgcode = tr.getValue("orgcode");
		//广告标题
		String title = tr.getValue("title");		
		// 广告内容
		String  content = tr.getValue("content");
		// 列表图片
//		String audit_pic = Utils.getFastUnitFilePath("am_advertising_system",
//				"bdp_audit_pic", id);
		Connection conn = db.getConnection();
		String updateSql="";
		String fileName = Utils.getFastUnitFilePath("AM_ADVERTISING_SYSTEM","fu_audit_pic", id);
		logger.info(fileName+"wwwwwwwwwwwwwwwwwww");
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE  am_advertising_system  SET audit_pic ='" + fileName
					+ "'  WHERE id='" + id + "' ";
			
			conn.createStatement().execute(updateSql);
		}
		//创建时间
		String  create_time = tr.getValue("create_time");
		//审核状态
		String audit_status=ac.getActionParameter();
		String  audit_type = tr.getValue("audit_type");	
		String sql="update  am_advertising_system set audit_type='"+audit_type+"' ,orgcode='"+orgcode+"' ,title='"+title+"',content='"+content+"',audit_status='"+audit_status+"',create_time='"+create_time+"' where id='"+id+"'";				
		db.execute(sql);		
	}
	
}
