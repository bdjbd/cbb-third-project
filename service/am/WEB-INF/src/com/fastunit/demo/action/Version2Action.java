package com.fastunit.demo.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class Version2Action extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String[] bookId = ac.getRequestParameters("version2.bookid.k");
		// 一、BOOK
		Table table = new Table("demo", "BOOK");
		table.setCheckAllVersion(true);// 遍历所有数据（仅做示例，一般不调用此方法）
		String[] version1 = ac.getRequestParameters("version2.book.updatetime.v");
		String[] bookName = ac.getRequestParameters("version2.bookname");
		for (int i = 0; i < bookId.length; i++) {
			TableRow tr = table.addUpdateRow();
			tr.setValue("bookname", bookName[i]);
			tr.setVersionValue(version1[i]);
			tr.setOldValue("bookid", bookId[i]);
		}
		db.save(table);
		// 二、BOOK_HITS
		table = new Table("demo", "BOOK_HITS");
		table.setCheckAllVersion(true);// 遍历所有数据（仅做示例，一般不调用此方法）
		String[] version2 = ac
				.getRequestParameters("version2.book_hits.updatetime.v");
		String[] hits = ac.getRequestParameters("version2.hits");
		for (int i = 0; i < bookId.length; i++) {
			TableRow tr = table.addUpdateRow();
			tr.setValue("hits", hits[i]);
			tr.setVersionValue(version2[i]);
			tr.setOldValue("bookid", bookId[i]);
		}
		db.save(table);
	}

}
