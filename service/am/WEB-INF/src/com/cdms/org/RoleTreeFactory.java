package com.cdms.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.user.role.Role;
import com.fastunit.user.role.RoleFactory;
import com.fastunit.user.table.ARole;
import com.fastunit.util.Checker;
import com.fastunit.util.EmptyObject;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;

public class RoleTreeFactory extends TreeFactory {
	private static final Logger log = LoggerFactory
			.getLogger(RoleTreeFactory.class);

	private static final String ICON_VALID = "/domain/adm/u/role1.png";
	private static final String ICON_DISABLED = "/domain/adm/u/role0.png";
	private static final String ACTION = "setPrivilege";
	private static final String ROOT_ATTRIBUTE = "oncontextmenu=showRootMenu()";
	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";

	private static final String SQL = "select role." + ARole.ROLE_ID + ","
			+ ARole.PARENT_ID + "," + ARole.ROLE_NAME + "," + ARole.ENABLED + ","
			+ ARole.REMARK + "," + ARole.ORDER + " from " + ARole.TABLENAME
			+ " role,abdp_childadminroleset child where role.roleid=child.roleid order by " + ARole.ORDER + "," + ARole.ROLE_ID;

	// copy from TreeHelper: 不使SQL怪异防止某些数据库不支持，同时提高效率
	public Tree createTree(ActionContext ac, String treeDoamin) throws Exception {
		DB db = DBFactory.getDB();
		MapList data = db.query(SQL);
		if (Checker.isEmpty(data)) {
			return null;
		}
		int rootRowIndex = data.findRowIndex(ARole.PARENT_ID, EmptyObject.STRING);
		if (rootRowIndex < 0) {
			rootRowIndex = data.findRowIndex(ARole.PARENT_ID, null);
		}
		if (rootRowIndex < 0) {
			log.error("could not found the root of role");
			return null;
		} else {
			Tree tree = getTree(data, rootRowIndex);
			setRootAttribute(tree);
			return tree;
		}
	}

	private Tree getTree(MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String roleId = row.get(ARole.ROLE_ID);
		String roleName = row.get(ARole.ROLE_NAME);
		Role role = RoleFactory.getRole(roleId);
		tree.setId(roleId);
		setName(tree, roleId, roleName, role.getPrivileges().size(), Checker
				.isEmpty(row.get(ARole.PARENT_ID)));

		boolean valid = "1".equals(row.get(ARole.ENABLED));
		tree.setIcon(valid ? ICON_VALID : ICON_DISABLED);
		setCustomAttribute(tree, roleId);
		setTitle(tree, row);
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get(ARole.PARENT_ID);
			if (roleId.equals(parent)) {
				tree.add(getTree(data, i));
			}
		}
		return tree;
	}

	// 授权页面使用
	protected void setTitle(Tree tree, Row row) {
	}

	protected void setCustomAttribute(Tree tree, String roleId) {
		tree.setAttribute(ATTRIBUTE);
		tree.setActionType(TreeActionType.FUNCTION);
		tree.setAction(ACTION);
	}

	protected void setRootAttribute(Tree tree) {
		tree.setAttribute(ROOT_ATTRIBUTE);
	}

	protected void setName(Tree tree, String roleId, String roleName,
			int privilegeCount, boolean isRoot) {
		tree.setName(privilegeCount + " " + roleName + " (" + roleId + ")");
	}
}
