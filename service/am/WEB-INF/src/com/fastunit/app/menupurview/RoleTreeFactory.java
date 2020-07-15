/**
 * 角色Tree信息
 * 丁照祥
 * 2012-05-16
 * */
package com.fastunit.app.menupurview;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.util.EmptyObject;
import com.fastunit.view.tree.Tree;

public class RoleTreeFactory extends TreeFactory {

	private static final Logger log = LoggerFactory
			.getLogger(RoleTreeFactory.class);

	private static final String SQL_ORG = "select * from arole";
	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DB db = DBFactory.getDB();
		// 获得数据
		MapList data = db.query(SQL_ORG);
		if (Checker.isEmpty(data)) {
			String id = UUID.randomUUID().toString();
			return null;

		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("parentid",
				EmptyObject.STRING);
		if (rootRowIndex < 0) {
			rootRowIndex = data.findRowIndex("parentid", null);
		}
		if (rootRowIndex < 0) {
			log.error("could not found the root of role");
			return null;
		} else {
			// 从根节点创建树
			Tree tree = getTree(db, data, rootRowIndex);
			return tree;
		}
	}
	
	private Tree getTree(DB db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String roleid = row.get("roleid");
		String rolename = row.get("rolename");
		tree.setId(roleid);
		tree.setName(rolename);

		tree.setAction("/app/abdp_roleterminalmenu.form.do?m=e&roleid="+roleid);// 制定方法名
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("parentid");
			if (roleid.equals(parent)) {
				tree.add(getTree(db, data, i));
			}
		}
		return tree;
	}
}
