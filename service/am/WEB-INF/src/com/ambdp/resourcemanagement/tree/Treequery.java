package com.ambdp.resourcemanagement.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ambdp.menu.tree.MoblieMenuTreeFactory;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;

	/***
	 * 
	 * 生成树节点
	 *
	 */
public class Treequery extends TreeFactory{

	private static final Logger log = LoggerFactory
			.getLogger(MoblieMenuTreeFactory.class);

	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";
	

	// copy from TreeHelper: 不使SQL怪异防止某些数据库不支持，同时提高效率
	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
//		String typecode = (String) ac.getSessionAttribute("typecode");
		String SQL_MENUMANAGER = "SELECT * FROM mjyc_resourceclass ORDER BY createdatetime DESC";
		DB db = DBFactory.getDB();
		// 获得数据
		MapList data = db.query(SQL_MENUMANAGER);
		if (Checker.isEmpty(data)) {
			return null;
		}
		
		// 确定根节点
		int rootRowIndex = data.findRowIndex("parentid", "01");
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

	/**
	 * 获得树
	 * 
	 * @param db
	 *            数据库连接
	 * @param data
	 *            数据集合
	 * @param index
	 * @return 返回树
	 * @throws Exception
	 */
	private Tree getTree(DB db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String id = row.get("id");
		String menuname = row.get("name");
		tree.setId(id);
		tree.setName(menuname);
		tree.setAttribute(ATTRIBUTE);
		
		// 动作类型:右键菜单
		tree.setActionType(TreeActionType.FUNCTION);
		//单击事件
//		tree.setAction("setRole");
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("parentid");
			if (id.equals(parent)) {
				tree.add(getTree(db, data, i));
			}
		}
		return tree;
	}
}
