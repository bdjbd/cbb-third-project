package com.cdms.casetype;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class SaveValidator extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("CDMS_CCASETYPE");
		String id = table.getRows().get(0).getValue("id");
		String case_code = table.getRows().get(0).getValue("case_code");
		String case_value = table.getRows().get(0).getValue("case_value");
		if (isRepeat(db,id,"case_code", case_code) > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("案件类型名称不能重复");
		}else if (isRepeat(db,id,"case_value", case_value) > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("案件类型值不能重复");
		}else{
			db.save(table);
		}
		

	}

	public int isRepeat(DB db,String id, String colName, String colValue)
			throws JDBCException {
		int rValue = 0;
		String sql = "select count(*) from CDMS_CCASETYPE where " + colName
				+ "='" + colValue + "' and id <>'"+id+"'";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = mapList.getRow(0).getInt(0, 0);
		}
		return rValue;
	}
}
