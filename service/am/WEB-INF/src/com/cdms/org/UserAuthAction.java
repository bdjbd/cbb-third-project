package com.cdms.org;

import com.fastunit.User;
import com.fastunit.context.ActionContext;
import com.fastunit.framework.config.Domains;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.TransactionAction;
import com.fastunit.user.AdminUser;
import com.fastunit.user.table.AUser;
import com.fastunit.user.table.AUserGroup;
import com.fastunit.user.table.AUserRole;
import com.fastunit.util.Checker;

public class UserAuthAction extends TransactionAction {

	private static final String SQL_SUPERUSER_CLEAR_GROUP = "delete from AUSERGROUP where userid=?";
	private static final String SQL_SUPERUSER_CLEAR_ROLE = "delete from AUSERROLE where userid=?";

	private static final String SQL_ADMINISTRATOR_CLEAR_GROUP = "delete from AUSERGROUP where userid=? "
			+ " and groupid in (select groupid from AADMINGROUP where adminid=?)";
	private static final String SQL_ADMINISTRATOR_CLEAR_ROLE = "delete from AUSERROLE where userid=? "
			+ " and roleid in (select roleid from AADMINROLE where adminid=?)";

	public void doAction(DB db, ActionContext ac) throws Exception {
		String userId = ac.getRequestParameter("user.authorization.userid");
		if (Checker.isEmpty(userId)) {
			return;
		}
		// clear
		User user = ac.getVisitor().getUser();
		if (user instanceof AdminUser) {
			// boolean pass = ManagementHelper.checkAdministrator(db, user.getId());
			// if (!pass) {
			// ac.getActionResult().addInfo("您的管理职能被取消，请重新登录。");
			// return;
			// }
			String administratorId = user.get(AUser.ADMIN_ID);
			String[] values = new String[] { userId, administratorId };
			int[] types = new int[] { Type.VARCHAR, Type.VARCHAR };
			db.execute(SQL_ADMINISTRATOR_CLEAR_GROUP, values, types);
			db.execute(SQL_ADMINISTRATOR_CLEAR_ROLE, values, types);
		} else {
			// boolean pass = ManagementHelper.checkSuperUser(db, user.getId());
			// if (!pass) {
			// ac.getActionResult().addInfo("您的管理职能被取消，请重新登录。");
			// return;
			// }
			db.execute(SQL_SUPERUSER_CLEAR_GROUP, userId, Type.VARCHAR);
			db.execute(SQL_SUPERUSER_CLEAR_ROLE, userId, Type.VARCHAR);
		}

		// group
		String[] checks = ac.getRequestParameters("a.groups");
		if (!Checker.isEmpty(checks)) {
			Table table = new Table(Domains.ADM, AUserGroup.TABLENAME);
			for (int i = 0; i < checks.length; i++) {
				TableRow tr = table.addInsertRow();
				tr.setValue(AUserGroup.USER_ID, userId);
				tr.setValue(AUserGroup.GROUP_ID, checks[i]);
				tr.setValue(AUserGroup.EXPIRED_DATE, ac
						.getRequestParameter("groupexpireddate." + checks[i]));
			}
			db.save(table);
		}
		// role
		checks = ac.getRequestParameters("a.roles");
		if (!Checker.isEmpty(checks)) {
			Table table = new Table(Domains.ADM, AUserRole.TABLENAME);
			for (int i = 0; i < checks.length; i++) {
				TableRow tr = table.addInsertRow();
				tr.setValue(AUserRole.USER_ID, userId);
				tr.setValue(AUserRole.ROLE_ID, checks[i]);
				tr.setValue(AUserRole.HOLD, 1);
				tr.setValue(AUserRole.EXPIRED_DATE, ac
						.getRequestParameter("roleexpireddate." + checks[i]));
			}
			db.save(table);
		}
		// repellent role
		checks = ac.getRequestParameters("a.repellentroles");
		if (!Checker.isEmpty(checks)) {
			Table table = new Table(Domains.ADM, AUserRole.TABLENAME);
			for (int i = 0; i < checks.length; i++) {
				TableRow tr = table.addInsertRow();
				tr.setValue(AUserRole.USER_ID, userId);
				tr.setValue(AUserRole.ROLE_ID, checks[i]);
				tr.setValue(AUserRole.HOLD, 0);
				tr.setValue(AUserRole.EXPIRED_DATE, ac
						.getRequestParameter("roleexpireddate." + checks[i]));
			}
			db.save(table);
		}
	}
}
