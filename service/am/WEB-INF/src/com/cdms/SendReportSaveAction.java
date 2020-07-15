package com.cdms;
/**
 * 
 * 
 * 报告发送设置
 */
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.common.util.FileUtils;
import com.am.frame.dispatcher.AmResServlet;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class SendReportSaveAction extends DefaultAction {
	private static final Logger log = LoggerFactory.getLogger(AmResServlet.class);
	
	public void doAction(DB db, ActionContext ac) throws Exception {
		//报告发送设置表
		log.info("44444444");
		File file = ac.getFile("data_import.file_path");
		log.debug("获得上传的文件==============：" + file);	
		Table table=ac.getTable("cdms_reporttemplatemanagement");
		db.save(table);
		
		//获取主键
		TableRow tr=table.getRows().get(0);
		String id=tr.getValue("id");
		
		
		//上传的文件以临时文件的形式保存在本地服务器虚拟字段中
		
	
		
	}

}
