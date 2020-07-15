package com.am.frame.systemAccount.action;

import java.io.File;
import java.util.List;

import com.fastunit.Path;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月10日
 * @version 
 * 说明:<br />
 * 系统帐号保存Actoin
 */
public class MallSystemAccountClassSaveAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Table table=ac.getTable("MALL_SYSTEM_ACCOUNT_CLASS");
		db.save(table);
		
		 List<TableRow> list=table.getRows();
		 
		 if(!Checker.isEmpty(list)){
			 String id=list.get(0).getValue("id");
			 String fileName = getFastUnitFilePath("MALL_SYSTEM_ACCOUNT_CLASS","bdp_icon_path", id);
				if (fileName != null && fileName.length() > 1) {
					fileName = fileName.substring(0, fileName.length() - 1);
					String updateSql = "UPDATE MALL_SYSTEM_ACCOUNT_CLASS SET icon_path ='" + fileName
							+ "'  WHERE id='" + id + "' ";
					db.execute(updateSql);
				}
		 }
		
	}

	
	/**
	 * 获取平台上传文件的文件名，返回JSON格式字符串。
	 * @param table  表名
	 * @param columName 字段名称
	 * @param id 主键
	 * @return 文件保存路径。多个文件用逗号分割
	 */
	public  String getFastUnitFilePath(String table, String columName,
			String id) {
		String result = "";
		String fileSpar = "/";
		String relativeFilePath = fileSpar + "files" + fileSpar + table
				+ fileSpar + id + fileSpar + columName;
		String fielPath = Path.getRootPath() + relativeFilePath;
		File file = new File(fielPath);
		if (!file.exists()) {
			return result;
		}
		for (String f : file.list()) {
			result += relativeFilePath + fileSpar + f + ",";
		}
		return result;
	}

	
}
