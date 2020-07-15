package com.fastunit.demo.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.VersionRow;
import com.fastunit.jdbc.exception.VersionException;
import com.fastunit.support.action.DefaultAction;

public class Version1Action extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = new Table("demo", "BOOK");
		TableRow tr = table.addUpdateRow();
		// 设置要变更的字段值
		tr.setValue("bookname", ac.getRequestParameter("version1.bookname"));
		// 设置版本字段的值
		// 版本字段隐藏值的命名规则（全部小写）：单元编号.表名.版本字段名.v
		tr.setVersionValue(ac.getRequestParameter("version1.book.updatetime.v"));
		// 设置主键字段值
		// 主键字段隐藏值的命名规则（全部小写）：单元编号.主键字段名.k
		tr.setOldValue("bookid", ac.getRequestParameter("version1.bookid.k"));

		try {
			db.save(table);
		} catch (VersionException e) {// 如果需要，手动捕捉异常
			// 一、获取哪些数据验证失败
			String tableName = e.getTableName();// 读取表名
			System.out.println("tableName=" + tableName);
			List<VersionRow> rows = e.getRows();
			// 读取主键值
			for (int i = 0; i < rows.size(); i++) {
				VersionRow row = rows.get(i);
				System.out.println("Message=" + row.getMessage());
				System.out.println("RowIndex=" + row.getRowIndex());
				System.out.println("OldVersionValue=" + row.getOldVersionValue());
				Map<String, String> primaryKeys = row.getPrimaryKeys();
				Iterator<String> keys = primaryKeys.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					System.out.println(key + "=" + primaryKeys.get(key));
				}
			}

			// 二、设置个性华信息提示，一般不需设置
			// (平台会自动设“操作失败：该数据已被他人修改，请重新获取数据再进行操作”)
			ActionResult ar = ac.getActionResult();
			ar.setSuccessful(false);
			ar.addErrorMessage("数据已被他人修改");// LangUtil.get(ac, "app", "101")
		}

	}

}
