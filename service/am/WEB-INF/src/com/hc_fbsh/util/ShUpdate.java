package com.hc_fbsh.util;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;

/**
 * @author 郭仁杰
 * 说明：更新审核状态类
 */
public class ShUpdate {
	public static void update(ActionContext ac, DB db, String actionParam, String id)
			throws Exception {
		String updateSQL = "";
		//草稿
		if ("0".equals(actionParam)) {
			updateSQL = "UPDATE HC_FBSH SET shzt='0' WHERE id='" + id + "'";	
		//审核中
		} else if ("1".equals(actionParam)) {
			updateSQL = "UPDATE HC_FBSH SET shzt='1' WHERE id='" + id + "'";	
		//审核通过
		} else if ("2".equals(actionParam)) {
			updateSQL = "UPDATE HC_FBSH SET shzt='2' WHERE id='" + id + "'";	
		}
		db.execute(updateSQL);
		ac.getActionResult().setSuccessful(true);
	}
}
