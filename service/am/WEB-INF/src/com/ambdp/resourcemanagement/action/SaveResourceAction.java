package com.ambdp.resourcemanagement.action;

import com.am.tools.Utils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年5月5日 下午5:20:08
 * @version 处理文件上传
 */
public class SaveResourceAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//保存数据表达数据
		Table table = ac.getTable("MJYC_RESOURCEMANAGEMENT");
		db.save(table);
		
		//获取主键
		String id = table.getRows().get(0).getValue("id");
		
		String filename = "";
		
		String bdpserverroute = ac.getRequestParameter("mjyc_resourcemanagement.form.bdpserverroute");
		
		if(!Checker.isEmpty(bdpserverroute)){
			//文件名
			if(!Checker.isEmpty(table.getRows().get(0).getValue("serverroute"))){
				filename = table.getRows().get(0).getValue("serverroute").split("bdpserverroute/")[1].toString();
				filename = filename.substring(0,filename.lastIndexOf("."));
			}
			//serverroute server路径
			Utils.saveFilesPath(db, "MJYC_RESOURCEMANAGEMENT", "serverroute","id", id,filename);
		}
		
	}
	
}