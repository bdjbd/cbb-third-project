package com.wisdeem.wwd.publicAccount;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

public class SelectAccountInfo extends AjaxAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		DB db=DBFactory.getDB();
		doAction(db,ac);
		return ac;
	}

	public void doAction(DB db, ActionContext ac) throws Exception {
		Ajax ajax = new Ajax(ac);
		String wchat_account = ac.getRequestParameter("wchat_account");
		if (!Checker.isEmpty(wchat_account)) {
			String sql = "select * from WS_OPERATORS_ACCOUNT where wchat_account='"
					+ wchat_account + "'";
			MapList list = db.query(sql);

			if (!Checker.isEmpty(list)) {
				String account_type = list.getRow(0).get("account_type");
				String is_valid = list.getRow(0).get("is_valid");
				String app_id = list.getRow(0).get("app_id");
				String app_secret = list.getRow(0).get("app_secret");
				String token = list.getRow(0).get("token");
				String welcomeword = list.getRow(0).get("welcomeword");
				String explain = list.getRow(0).get("explain");

				ajax.setValueByName(0, "ws_public_accounts.form.password","******");
				ajax.setValueByName(0, "ws_public_accounts.form.account_type",account_type);
				if("1".equals(is_valid)){
					ajax.setValueByName(0, "ws_public_accounts.form.is_valid","是");
				}
				if("3".equals(is_valid)){
					ajax.setValueByName(0, "ws_public_accounts.form.is_valid","否");
				}
				ajax.setValueByName(0, "ws_public_accounts.form.app_id",app_id);
				ajax.setValueByName(0, "ws_public_accounts.form.app_secret",app_secret);
				ajax.setValueByName(0, "ws_public_accounts.form.token", token);
				ajax.setValueByName(0, "ws_public_accounts.form.welcomeword",welcomeword);
				ajax.setValueByName(0, "ws_public_accounts.form.explain",explain);
			} 
			ajax.send();
		}
	}
}
