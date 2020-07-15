package com.cdms.org;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.User;
import com.fastunit.adm.user.UserAuthUI;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.user.AdminUser;
import com.fastunit.user.UserFactory;
import com.fastunit.user.admin.Admin;
import com.fastunit.user.admin.AdminFactory;
import com.fastunit.user.table.ARole;
import com.fastunit.user.table.AUser;
import com.fastunit.user.table.AUserRole;
import com.fastunit.util.ArrayUtil;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;
import com.fastunit.util.MapListUtil;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.util.Html;

public class AdminRoleTreeFactory extends RoleTreeFactory {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String CHECKBOX_UNCHECKED = "<input type=checkbox disabled/>";
	private static final String CHECKBOX_CHECKED = "<input type=checkbox disabled checked/>";
	private static final String SQL_ROLES = "select role." + AUserRole.ROLE_ID + ",role."
			+ AUserRole.EXPIRED_DATE + " from " + AUserRole.TABLENAME + " role,abdp_childadminroleset child where role."
			+ AUserRole.USER_ID + "=? and role.hold=1 and role.roleid = child.roleid";

	private static final String SQL_REPELLENT_ROLES = "select role."
			+ AUserRole.ROLE_ID + ",role." + AUserRole.EXPIRED_DATE + " from "
			+ AUserRole.TABLENAME + " role,abdp_childadminroleset child  where role." + AUserRole.USER_ID + "=? and role.hold=0 "
					+ "and  role.roleid = child.roleid";

	private static final String SQL_INDIRECT_ROLES = "select role.roleid from AROLE role,abdp_childadminroleset child "
			+ " where "
			+ " role.roleid in (select t1.roleid from AORGROLE t1,AUSER t2 where t1.orgid = t2.orgid and t2.userid=?)"
			+ " or role.roleid in (select t3.roleid from AGROUPROLE t3, AUSERGROUP t4 where t3.groupid=t4.groupid and t4.userid=?) "
			+ " and role.roleid = child.roleid";

	private Admin administrator = null;
	private MapList roles;
	private MapList repellentRoles;
	private String[] indirectRoles;

	public Tree createTree(ActionContext ac, String treeDoamin) throws Exception {
		String userId = UserAuthUI.getAndSetUserId(ac);
		if (Checker.isEmpty(userId)) {
			return null;
		} else {
			User operator = ac.getVisitor().getUser();
			if (operator instanceof AdminUser) {
				String administratorId = operator.get(AUser.ADMIN_ID);
				administrator = AdminFactory.getAdmin(administratorId);
			}
			DB db = DBFactory.getDB();
			roles = db.query(SQL_ROLES, userId, Type.VARCHAR);
			repellentRoles = db.query(SQL_REPELLENT_ROLES, userId, Type.VARCHAR);
			indirectRoles = getIndirectRoles(db, userId);
			User user = UserFactory.getUser(userId);
			String[] allRoles = user.getAllRoles();
			Tree tree = super.createTree(ac, treeDoamin);
			System.err.println("++++++++++++++");
			tree.print();
			System.err.println("++++++++++++++");
			tree.setCheckAttribute("checked disabled");
			tree.setAttribute(CHECKBOX_UNCHECKED + CHECKBOX_UNCHECKED);
			tree.setAction(" (" + (roles.size() + 1) + "/" + repellentRoles.size()
					+ "/" + indirectRoles.length + ") (" + allRoles.length + "/"
					+ tree.size() + ")");
			
			return tree;
		}
	}

	protected void setTitle(Tree tree, Row row) {
		String remark = row.get(ARole.REMARK);
		if (!Checker.isEmpty(remark)) {
			tree.setTitle(remark);
		}
	}

	protected void setCustomAttribute(Tree tree, String roleId) {
		tree.setCheckValue(roleId);
		// 关联角色
		StringBuffer checkbox1 = new StringBuffer();
		if (roles.findRowIndex(AUserRole.ROLE_ID, roleId) >= 0) {
			checkbox1.append("checked");
		}
		// 排斥角色
		StringBuffer checkbox2 = new StringBuffer(
				"<input type=\"checkbox\" name=\"a.repellentroles\" value=\"").append(
				roleId).append("\"");
		if (repellentRoles.findRowIndex(AUserRole.ROLE_ID, roleId) >= 0) {
			checkbox2.append(" checked");
		}

		// 禁用administrator权限之外的
		if (administrator != null && !administrator.containsRole(roleId)) {
			checkbox1.append(" disabled");
			checkbox2.append(" disabled");
		} else {
			checkbox1.append(" onclick=exclude(this) ");
			checkbox2.append(" onclick=exclude(this) ");
		}
		tree.setCheckAttribute(checkbox1.toString());

		checkbox2.append("/>");
		// 间接关联角色
		if (ArrayUtil.find(indirectRoles, roleId) >= 0) {
			tree.setAttribute(checkbox2.append(CHECKBOX_CHECKED).toString());
		} else {
			tree.setAttribute(checkbox2.append(CHECKBOX_UNCHECKED).toString());
		}
	}

	protected void setRootAttribute(Tree tree) {
	}

	private static String[] getIndirectRoles(DB db, String userId)
			throws Exception {
		MapList list = db.query(SQL_INDIRECT_ROLES,
				new String[] { userId, userId },
				new int[] { Type.VARCHAR, Type.VARCHAR });
		return MapListUtil.getArray(list, ARole.ROLE_ID);
	}

	protected void setName(Tree tree, String roleId, String roleName,
			int privilegeCount, boolean isRoot) {

		StringBuffer html = new StringBuffer();
		html.append(privilegeCount).append(" ").append(roleName);

		// 根角色没有input
		if (!isRoot) {
			// 过期日期
			String expiredDate = null;
			Row row = roles.findRow(AUserRole.ROLE_ID, roleId);
			if (row == null) {
				row = repellentRoles.findRow(AUserRole.ROLE_ID, roleId);
			}
			html.append("<input name=\"roleexpireddate.").append(roleId).append(
					"\" onclick=\"WdatePicker()\" readonly=\"readonly\"");
			if (row == null) {// 未选中
				html.append(" style=\"visibility:hidden\" disabled=\"disabled\"");
			} else {// 选中
				expiredDate = row.get(AUserRole.EXPIRED_DATE);
				Html.setValue(html, expiredDate);
			}
			html.append("class=\"SD date");
			if (!Checker.isEmpty(expiredDate)
					&& DateUtil.getDays(expiredDate, DateUtil.getCurrentDate()) >= 0) {
				html.append(" expired");
			}
			html.append("\" />");
		}

		tree.setName(html.toString());
	}
}
