package com.cdms;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/*
 * 远程升级文件管理
 */
public class UploadFilesAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table t = ac.getTable("cdms_remoteupgrademanagement");
		db.save(t);

		TableRow tr = t.getRows().get(0);
		String id = tr.getValue("id");

		String url = new FileUtils().getFastUnitFilePathJSON("cdms_remoteupgrademanagement", "bdp_file_path", id);

		String sql = "update cdms_remoteupgrademanagement set file_path='" + url + "' where id='" + id + "'";

		db.execute(sql);
	}

}
