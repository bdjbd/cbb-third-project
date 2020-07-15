package com.p2p.commodity;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 商品规格保存
 * 
 * @author Administrator
 * 
 */

public class CommodityFormatSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("P2P_COMDITYFORMAT");
		List<TableRow> rows = table.getRows();
		List<Integer> addList = new ArrayList<Integer>();

		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isInsertRow() || rows.get(i).isUpdateRow()) {
				addList.add(i);
			}
		}
		db.save(table);
		for (int i = 0; i < addList.size(); i++) {
			String id = table.getRows().get(addList.get(i)).getValue("id");
			String fileName = Utils.getFastUnitFilePath("P2P_COMDITYFORMAT",
					"fu_formatimage", id);
			Connection conn = db.getConnection();
			String updateSql = "SELECT now() ";
			if (fileName != null && fileName.length() > 1) {
				fileName = fileName.substring(0, fileName.length() - 1);
				updateSql = "UPDATE P2P_COMDITYFORMAT  SET FormatImage='"
						+ fileName + "'  WHERE id='" + id + "' ";
				System.out.println(fileName);
				conn.createStatement().execute(updateSql);
				conn.commit();
			}
		}

	}
}
