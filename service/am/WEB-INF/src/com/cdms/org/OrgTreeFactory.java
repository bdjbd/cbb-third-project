package com.cdms.org;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.user.org.Org;
import com.fastunit.user.org.OrgFactory;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgTreeFactory extends TreeFactory {
	private static final Logger log = LoggerFactory
			.getLogger(OrgTreeFactory.class);
	private static final String ICON = "/domain/adm/u/organization.png";
	private static final String ACTION = "setRole";
	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";
	private static final String SQL_LEADER = "select t1.userid,t1.username,t2.leadertype from AUSER t1,AORGLEADER t2 where t1.userid=t2.userid and t2.orgid=? order by t2.leadertype,t1.userid";
	private static final String SQL_ORG = "select * from AORG order by o,orgid";

	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DB db = DBFactory.getDB();
		String orgid = ac.getVisitor().getUser().getOrgId();
		MapList data = db.query("select * from AORG where orgid like ('"+orgid+"%') order by o,orgid");
		if (Checker.isEmpty(data)) {
			return null;
		} else {
//			int rootRowIndex = data.findRowIndex("parentid", orgid);
//			if (rootRowIndex < 0) {
//				rootRowIndex = data.findRowIndex("parentid", (String) null);
//			}

//			if (rootRowIndex < 0) {
//				log.error("could not found the root of role");
//				return null;
//			} else {
				Tree tree = this.getTree(db, data, 0);
				return tree;
//			}
		}
	}

	private Tree getTree(DB db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String organizationId = row.get("orgid");
		String organizationName = row.get("orgname");
		Org organization = OrgFactory.getOrg(organizationId);
		tree.setId(organizationId);
		this.setName(db, tree, organizationId, organizationName, organization
				.getRoles().length, organization.getPrivileges().size());
		tree.setIcon("/domain/adm/u/organization.png");
		this.setCustomAttribute(tree, organizationId);

		for (int i = 0; i < data.size(); ++i) {
			String parent = data.getRow(i).get("parentid");
			if (organizationId.equals(parent)) {
				tree.add(this.getTree(db, data, i));
			}
		}

		return tree;
	}

	protected void setCustomAttribute(Tree tree, String organizationId) {
		tree.setAttribute("oncontextmenu=showMenu()");
		tree.setActionType(TreeActionType.FUNCTION);
		tree.setAction("editOrg");
	}

	protected void setName(DB db, Tree tree, String orgId, String orgName,
			int roleCount, int privilegeCount) throws Exception {
		StringBuffer name = new StringBuffer();
		name.append(roleCount).append(" ").append(orgName).append(" (")
				.append(orgId).append(") ").append(privilegeCount);
		MapList list = db
				.query("select t1.userid,t1.username,t2.leadertype from AUSER t1,AORGLEADER t2 where t1.userid=t2.userid and t2.orgid=? order by t2.leadertype,t1.userid",
						orgId, 2);
		if (!Checker.isEmpty(list)) {
			for (int i = 0; i < list.size(); ++i) {
				if (i > 0) {
					name.append(",");
				}

				Row row = list.getRow(i);
				String userId = row.get("userid");
				String userName = row.get("username");
				name.append(" ").append(
						Checker.isEmpty(userName) ? userId : userName);
			}
		}

		tree.setName(name.toString());
	}
}